package me.yxp.qfun.utils.thread;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleIntervalExecutor {
    private final Handler mHandler;
    private final long mIntervalMs;
    private final CopyOnWriteArrayList<Runnable> mTasks = new CopyOnWriteArrayList<>();
    private boolean mIsExecuting = false;

    public SimpleIntervalExecutor(long intervalMs) {
        mIntervalMs = intervalMs;
        
        HandlerThread workerThread = new HandlerThread("SimpleIntervalExecutor");
        workerThread.start();
        mHandler = new Handler(workerThread.getLooper());
    }

    public void addTask(Runnable task) {
        if (!mIsExecuting && task != null) {
            mTasks.add(task);
        }
    }

    public void startExecute() {
        if (mIsExecuting || mTasks.isEmpty()) {
            return;
        }
        
        mIsExecuting = true;
        executeNextTask(0);
    }

    private void executeNextTask(final int index) {
        if (index >= mTasks.size()) {
            // 所有任务执行完成，重置状态并清空任务列表
            resetAndClear();
            return;
        }

        mHandler.post(() -> {
            try {
                mTasks.get(index).run();
            } catch (Exception ignored) {
            }

            if (index + 1 < mTasks.size()) {
                mHandler.postDelayed(() -> 
                    executeNextTask(index + 1), 
                    mIntervalMs
                );
            } else {
                // 最后一个任务完成，重置状态并清空任务列表
                resetAndClear();
            }
        });
    }

    private void resetAndClear() {
        mIsExecuting = false;
        mTasks.clear(); // 清空任务列表，准备下次使用
    }

    public boolean isExecuting() {
        return mIsExecuting;
    }

    public void destroy() {
        resetAndClear();
        if (mHandler != null) {
            mHandler.getLooper().quit();
        }
    }
}