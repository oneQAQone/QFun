package mqq.app;

import com.tencent.qphone.base.remote.SimpleAccount;
import com.tencent.qphone.base.util.BaseApplication;

import java.util.List;

public abstract class MobileQQ extends BaseApplication {
    public static String PACKAGE_NAME = "com.tencent.mobileqq";

    public static MobileQQ getMobileQQ() {
        throw new UnsupportedOperationException("only view.");
    }

    public String getQQProcessName() {
        throw new UnsupportedOperationException("only view.");
    }

    public void otherProcessExit(boolean isCrashed) {
        throw new UnsupportedOperationException("only view.");
    }

    public AppRuntime peekAppRuntime() {
        throw new RuntimeException();
    }

    public void qqProcessExit(boolean stopMSF) {
        throw new UnsupportedOperationException("only view.");
    }

    public AppRuntime waitAppRuntime(BaseActivity baseActivity) {
        return null;
    }

    public AppRuntime waitAppRuntime() {
        return waitAppRuntime(null);
    }

    public List<SimpleAccount> getAllAccounts() {
        return null;
    }

    public int getMsfConnectedNetType() {
        return 0;
    }
}
