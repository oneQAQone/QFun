const CONFIG_DIR = '/data/adb/modules/qfun_zygisk';
const CONFIG_FILE = `${CONFIG_DIR}/target_config.json`;
const GITHUB_REPO_URL = 'https://github.com/oneQAQone/QFun';
const TARGETS = new Map([
    ['com.tencent.mobileqq', 'QQ'],
    ['com.tencent.tim', 'TIM'],
]);

let installedTargets = [];
let config = { version: 1, targets: [] };
let initError = '';
let callbackId = 0;

function runShell(command, timeout = 15000) {
    return new Promise(resolve => {
        let done = false;
        const name = `__qfun_exec_${++callbackId}`;
        const finish = (code, stdout, stderr) => {
            if (done) return;
            done = true;
            clearTimeout(timer);
            try { delete window[name]; } catch (_) {}
            resolve({ code: Number(code) || 0, stdout: String(stdout || ''), stderr: String(stderr || '') });
        };
        window[name] = finish;
        const timer = setTimeout(() => finish(-1, '', 'command timeout'), timeout);

        try {
            if (typeof ksu !== 'undefined' && typeof ksu.exec === 'function') {
                ksu.exec(command, '{}', name);
                return;
            }
            if (typeof exec === 'function') {
                Promise.resolve(exec(command)).then(r => finish(r?.errno ?? 0, r?.stdout, r?.stderr)).catch(e => finish(-1, '', e));
                return;
            }
            finish(127, '', 'KernelSU exec API unavailable');
        } catch (e) {
            finish(-1, '', e);
        }
    });
}

function showToast(message) {
    try {
        if (typeof ksu !== 'undefined' && typeof ksu.toast === 'function') return ksu.toast(message);
    } catch (_) {}
    try { alert(message); } catch (_) {}
}

function parseUserIds(text) {
    const ids = new Set([0]);
    for (const m of String(text).matchAll(/UserInfo\{(\d+)/g)) ids.add(Number(m[1]));
    for (const line of String(text).split(/\r?\n/)) {
        const m = line.match(/^\s*UserInfo\{(\d+)/);
        if (m) ids.add(Number(m[1]));
    }
    return [...ids].sort((a, b) => a - b);
}

async function loadConfig() {
    const r = await runShell(`cat '${CONFIG_FILE}'`);
    if (r.code !== 0 || !r.stdout.trim()) {
        config = { version: 1, targets: [] };
        return;
    }
    try {
        const parsed = JSON.parse(r.stdout);
        config = parsed && Array.isArray(parsed.targets) ? parsed : { version: 1, targets: [] };
    } catch (e) {
        throw new Error(`配置文件 JSON 无效: ${e}`);
    }
}

function enabled(packageName, userId) {
    return config.targets.some(t => t.packageName === packageName && Number(t.userId) === userId && t.enabled === true);
}

async function scanApplications() {
    const a = await runShell('cmd user list');
    const b = await runShell('pm list users');
    const users = parseUserIds(`${a.stdout}\n${b.stdout}`);
    const found = [];

    for (const userId of users) {
        const r = await runShell(`pm list packages --user ${userId}`);
        if (r.code !== 0) continue;
        const seen = new Set();
        for (const line of r.stdout.split(/\r?\n/)) {
            const pkg = line.replace(/^package:/, '').trim();
            if (!TARGETS.has(pkg) || seen.has(pkg)) continue;
            seen.add(pkg);
            found.push({ packageName: pkg, userId, label: TARGETS.get(pkg), isClone: userId !== 0, enabled: enabled(pkg, userId) });
        }
    }
    installedTargets = found;
}

async function writeConfig() {
    const data = JSON.stringify({
        version: 1,
        targets: installedTargets.map(t => ({ packageName: t.packageName, userId: t.userId, enabled: !!t.enabled }))
    }, null, 2);
    const encoded = btoa(unescape(encodeURIComponent(data)));
    const command = `mkdir -p '${CONFIG_DIR}' && echo '${encoded}' | base64 -d > '${CONFIG_FILE}.tmp' && chmod 0644 '${CONFIG_FILE}.tmp' && mv -f '${CONFIG_FILE}.tmp' '${CONFIG_FILE}' && cat '${CONFIG_FILE}'`;
    const r = await runShell(command);
    if (r.code !== 0) throw new Error(r.stderr || `exit ${r.code}`);
    JSON.parse(r.stdout);
}

async function toggleItem(index, checked) {
    const item = installedTargets[index];
    if (!item) return;
    const old = item.enabled;
    item.enabled = !!checked;
    renderList();
    try {
        await writeConfig();
        showToast(`${item.label} User ${item.userId} 已${checked ? '启用' : '停用'}`);
    } catch (e) {
        item.enabled = old;
        renderList();
        showToast(`保存失败: ${e}`);
    }
}

async function forceStopItem(index) {
    const item = installedTargets[index];
    if (!item) return;
    const r = await runShell(`am force-stop --user ${item.userId} ${item.packageName}`);
    showToast(r.code === 0 ? `${item.label} 已停止` : `停止失败: ${r.stderr || r.code}`);
}

function renderList() {
    const container = document.getElementById('content-list');
    if (!container) return;
    if (initError) {
        container.innerHTML = `<div class="empty">${escapeHtml(initError)}</div>`;
        return;
    }
    if (!installedTargets.length) {
        container.innerHTML = '<div class="empty">未检测到已安装的 QQ、TIM<br><small>请确认 Root 管理器已授予 WebUI 执行权限</small></div>';
        return;
    }
    container.innerHTML = installedTargets.map((item, index) => `
        <div class="card">
            <div class="target-info">
                <div class="target-title">${escapeHtml(item.label)} <span class="${item.isClone ? 'badge clone' : 'badge'}">${item.isClone ? `分身空间 (User ${item.userId})` : '主应用空间'}</span></div>
                <div class="target-pkg">${escapeHtml(item.packageName)}</div>
                <button class="stop-btn" onclick="forceStopItem(${index})">强停</button>
            </div>
            <label class="switch"><input type="checkbox" ${item.enabled ? 'checked' : ''} onchange="toggleItem(${index}, this.checked)"><span class="slider"></span></label>
        </div>`).join('');
}

function escapeHtml(value) {
    return String(value).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

async function openGitHub() {
    await runShell(`am start -a android.intent.action.VIEW -d '${GITHUB_REPO_URL}'`);
}

async function init() {
    try {
        await loadConfig();
        await scanApplications();
    } catch (e) {
        console.error(e);
        initError = `初始化失败：${e}`;
    }
    renderList();
}

globalThis.toggleItem = toggleItem;
globalThis.forceStopItem = forceStopItem;
globalThis.openGitHub = openGitHub;

if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
else init();
