package me.yxp.qfun.utils.hook.xpcompat;

import androidx.annotation.NonNull;

import me.yxp.qfun.loader.hookapi.IHookBridge;

/*package*/ class WrappedCallbacks {

    private WrappedCallbacks() {
    }

    public static class WrappedHookParam extends XC_MethodHook.MethodHookParam {

        private IHookBridge.IMemberHookParam param;

        private WrappedHookParam() {
        }

        @Override
        public Object getResult() {
            return param.getResult();
        }

        @Override
        public void setResult(Object result) {
            param.setResult(result);
        }

        @Override
        public Throwable getThrowable() {
            return param.getThrowable();
        }

        @Override
        public void setThrowable(Throwable throwable) {
            param.setThrowable(throwable);
        }

        @Override
        public boolean hasThrowable() {
            return param.getThrowable() != null;
        }

        @Override
        public Object getResultOrThrowable() throws Throwable {
            Throwable throwable = param.getThrowable();
            if (throwable != null) {
                throw throwable;
            }
            return param.getResult();
        }
    }

    public static class WrappedHookCallback implements IHookBridge.IMemberHookCallback {

        private final XC_MethodHook callback;

        public WrappedHookCallback(@NonNull XC_MethodHook callback) {
            this.callback = callback;
        }

        @Override
        public void beforeHookedMember(@NonNull IHookBridge.IMemberHookParam param) throws Throwable {
            WrappedHookParam wrappedParam = new WrappedHookParam();
            wrappedParam.param = param;
            wrappedParam.method = param.getMember();
            wrappedParam.thisObject = param.getThisObject();
            wrappedParam.args = param.getArgs();
            param.setExtra(wrappedParam);
            callback.beforeHookedMethod(wrappedParam);
        }

        @Override
        public void afterHookedMember(@NonNull IHookBridge.IMemberHookParam param) throws Throwable {
            WrappedHookParam wrappedParam = (WrappedHookParam) param.getExtra();
            if (wrappedParam == null) {
                throw new IllegalStateException("beforeHookedMember not called");
            }
            wrappedParam.method = param.getMember();
            wrappedParam.thisObject = param.getThisObject();
            wrappedParam.args = param.getArgs();
            callback.afterHookedMethod(wrappedParam);
        }
    }

}
