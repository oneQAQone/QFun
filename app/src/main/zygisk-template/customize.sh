SKIPUNZIP=1

ui_print "- 正在解压模块文件..."
unzip -o "$ZIPFILE" "module.prop" -d "$MODPATH" >&2

mkdir -p "$MODPATH/zygisk"

if [ "$IS64BIT" = "true" ]; then
    unzip -oj "$ZIPFILE" "lib/arm64-v8a/libqfun_zygisk.so" -d "$MODPATH/zygisk" >&2 2>/dev/null
    if [ -f "$MODPATH/zygisk/libqfun_zygisk.so" ]; then
        mv -f "$MODPATH/zygisk/libqfun_zygisk.so" "$MODPATH/zygisk/arm64-v8a.so"
    fi
fi

unzip -oj "$ZIPFILE" "lib/armeabi-v7a/libqfun_zygisk.so" -d "$MODPATH/zygisk" >&2 2>/dev/null
if [ -f "$MODPATH/zygisk/libqfun_zygisk.so" ]; then
    mv -f "$MODPATH/zygisk/libqfun_zygisk.so" "$MODPATH/zygisk/armeabi-v7a.so"
fi

if [ ! -f "$MODPATH/zygisk/arm64-v8a.so" ] && [ ! -f "$MODPATH/zygisk/armeabi-v7a.so" ]; then
    abort "! 未能在 APK 中找到适用的 Zygisk 库文件"
fi

ui_print "- 正在部署 module.apk..."
cp -af "$ZIPFILE" "$MODPATH/module.apk"

ui_print "- 正在安装 QFun 应用程序..."
pm install -r "$ZIPFILE" || ui_print "! 自动安装应用失败，请随后手动安装"

ui_print "- 正在解压支持文件..."
unzip -o "$ZIPFILE" "webroot/*" -d "$MODPATH" >&2 2>/dev/null
unzip -o "$ZIPFILE" "uninstall.sh" -d "$MODPATH" >&2 2>/dev/null
unzip -o "$ZIPFILE" "action.sh" -d "$MODPATH" >&2 2>/dev/null

OLD_CONFIG="/data/adb/modules/$MODID/target_config.json"
if [ -f "$OLD_CONFIG" ]; then
    ui_print "- 发现已有目标配置，正在迁移..."
    cp -af "$OLD_CONFIG" "$MODPATH/target_config.json"
else
    ui_print "- 初始化默认目标配置..."
    cat << 'EOF' > "$MODPATH/target_config.json"
{
  "version": 1,
  "targets": [
    {
      "packageName": "com.tencent.mobileqq",
      "userId": 0,
      "enabled": true
    },
    {
      "packageName": "com.tencent.tim",
      "userId": 0,
      "enabled": true
    }
  ]
}
EOF
fi

set_perm_recursive "$MODPATH" 0 0 0755 0644
set_perm_recursive "$MODPATH/zygisk" 0 0 0755 0755
set_perm "$MODPATH/module.apk" 0 0 0644
set_perm "$MODPATH/target_config.json" 0 0 0644
[ -f "$MODPATH/uninstall.sh" ] && set_perm "$MODPATH/uninstall.sh" 0 0 0755
[ -f "$MODPATH/action.sh" ] && set_perm "$MODPATH/action.sh" 0 0 0755