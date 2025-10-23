package me.yxp.qfun.utils.thread;

public class LoopHolder {
    private boolean mIsRunning;
    private Runnable mRunnable;
    private int mSleepTime;

    public LoopHolder() {
        mIsRunning = false;
        mSleepTime = 1000;
    }

    public void setRunnable(SyncUtils.MyRunnable myRunnable) {
        mRunnable = () -> {
            while (mIsRunning) {
                try {
                    myRunnable.run();
                    Thread.sleep(mSleepTime);
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

    public void stop() {
        mIsRunning = false;
    }

    public void setSleepTime(int sleepTime) {
        mSleepTime = sleepTime;
    }

}