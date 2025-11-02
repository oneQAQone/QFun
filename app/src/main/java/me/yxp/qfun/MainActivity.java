package me.yxp.qfun;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import io.github.libxposed.service.XposedService;
import me.yxp.qfun.utils.hook.hookstatus.HookStatus;
import me.yxp.qfun.utils.qq.HostInfo;

public class MainActivity extends Activity {
    private ImageView mIvActivationStatus;
    private TextView mTvHookStatus;
    private TextView mTvFramework;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        HookStatus.init(this);
        initView();
        updateActivationStatus();
    }

    private void initView() {
        TextView tvVersion = findViewById(R.id.main_version);
        tvVersion.setText(String.format("%s(%s)",
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE));

        mIvActivationStatus = findViewById(R.id.main_isChecked);
        mTvHookStatus = findViewById(R.id.main_hookstatus);
        mTvFramework = findViewById(R.id.main_framework);
    }

    public void gotoTelegram(View v) {
        jump("https://t.me/QFunChannel");
    }

    public void gotoGithub(View v) {
        jump("https://github.com/oneQAQone/QFun");
    }

    public void gotoQQGroup(View v) {
        jump("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1067198087&card_type=group&source=qrcode");
    }

    private void jump(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void updateActivationStatus() {
        boolean isHookEnabled = checkHookStatus();

        mIvActivationStatus.setImageResource(isHookEnabled ?
                R.drawable.checked : R.drawable.unchecked);
        mTvHookStatus.setText(isHookEnabled ? "已激活" : "未激活");
        updateFrameworkInfo(isHookEnabled);
    }

    private boolean checkHookStatus() {
        boolean isEnabledByLegacyApi = HookStatus.isModuleEnabled() || HostInfo.isInHostProcess();
        boolean isEnabledByLibXposedApi = checkLibXposedStatus();

        return isEnabledByLegacyApi || isEnabledByLibXposedApi;
    }

    private boolean checkLibXposedStatus() {
        XposedService xposedService = HookStatus.getXposedService().getValue();
        if (xposedService == null) {
            return false;
        }

        Set<String> scope = new HashSet<>(xposedService.getScope());
        scope.retainAll(Set.of(HostInfo.PACKAGE_NAME_QQ, HostInfo.PACKAGE_NAME_TIM));
        return !scope.isEmpty();
    }

    private void updateFrameworkInfo(boolean isHookEnabled) {
        if (!isHookEnabled) {
            mTvFramework.setText(HookStatus.getHookProviderNameForLegacyApi());
            return;
        }

        XposedService xposedService = HookStatus.getXposedService().getValue();
        if (xposedService != null) {
            String frameworkInfo = String.format("%s %s (%s), API %s",
                    xposedService.getFrameworkName(),
                    xposedService.getFrameworkVersion(),
                    xposedService.getFrameworkVersionCode(),
                    xposedService.getAPIVersion());
            mTvFramework.setText(frameworkInfo);
        } else {
            mTvFramework.setText(HookStatus.getHookProviderNameForLegacyApi());
        }
    }
}
