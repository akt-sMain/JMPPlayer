package jmp.task;

import java.util.ArrayList;

import jmp.util.JmpUtil;

public abstract class TaskOfBase implements ITask, Runnable {

    private ArrayList<ICallbackFunction> callbackQue = null;
    private boolean isRunnable = true;
    private Thread thread = null;
    protected long sleepTime = 100;

    public TaskOfBase(long sleepTime) {
        this.callbackQue = new ArrayList<ICallbackFunction>();
        this.sleepTime = sleepTime;
        this.isRunnable = true;
        this.thread = new Thread(this);
        this.thread.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
        begin();

        while (isRunnable) {

            long pastTime = System.currentTimeMillis();

            if (callbackQue != null) {
                execCallback();
            }
            loop();

            long newTime = System.currentTimeMillis();
            long pSleepTime = sleepTime - (newTime - pastTime);
            if (pSleepTime < 1) {
                pSleepTime = 1;
            }
            notifySleepTimeCalc(pSleepTime);
            JmpUtil.threadSleep(pSleepTime);
        }

        end();

        // コールバック関数のクリア
        if (callbackQue != null) {
            callbackQue.clear();
        }
    }

    /**
     * スリープタイム取得
     *
     * @return スリープタイム
     */
    public long getSleepTime() {
        return sleepTime;
    }

    @Override
    public void startTask() {
        this.thread.start();
    }

    @Override
    public void joinTask() {
        try {
            this.thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void queuing(ICallbackFunction callbackFunction) {
        callbackQue.add(callbackFunction);
    }

    protected void execCallback() {
        for (int i = 0; i < callbackQue.size(); i++) {
            // ExecutorService service = Executors.newCachedThreadPool();
            // ICallbackFunction cp = callbackQue.get(i);
            // CallableTask task = new CallableTask(cp);
            // service.submit(task);
            // service.shutdown();

            ICallbackFunction cp = callbackQue.get(i);
            if (cp != null) {
                cp.callback();
                cp = null;
            }

            // コールバック関数の削除
            callbackQue.remove(i);
        }
    }

    /**
     * タスク開始時に1度だけコールされるメソッド
     */
    abstract void begin();

    /**
     * タスク本処理
     */
    abstract void loop();

    /**
     * タスク終了時に1度だけコールされるメソッド
     */
    abstract void end();

    /** sleeptimeデバッグ用 */
    protected void notifySleepTimeCalc(long sleepTime) {
    }

    @Override
    public void exitTask() {
        isRunnable = false;
    }

}
