package me.yxp.qfun.utils.thread;

public class LoopHolder {
    private boolean mIsRunning;
    private Runnable mRunnable;
    private long mStopTime;

    public LoopHolder() {
        mIsRunning = false;
        mStopTime = 0;
    }

    public void setRunnable(SyncUtils.MyRunnable myRunnable) {
        mRunnable = () -> {

            while (mIsRunning && System.currentTimeMillis() < mStopTime) {
                try {
                    myRunnable.run();
                } catch (Throwable th) {
                    // 忽略异常，保持循环运行
                }
            }
        };
    }

    public void start() {
        if (mIsRunning || mRunnable == null) {
            return;
        }
        mIsRunning = true;
        new Thread(mRunnable).start();
    }

    public void stop() { mIsRunning = false; }

    public void setStopTime(long time) { mStopTime = time; }

}