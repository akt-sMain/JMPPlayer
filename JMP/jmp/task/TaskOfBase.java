package jmp.task;

import function.Utility;

public abstract class TaskOfBase implements ITask, Runnable {

    private boolean isRunnable = true;
    private Thread thread = null;
    protected long sleepTime = 100;

    public TaskOfBase(long sleepTime) {
        this.sleepTime = sleepTime;
        this.isRunnable = true;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        begin();

        while (isRunnable) {

            long pastTime = System.currentTimeMillis();

            loop();

            long newTime = System.currentTimeMillis();
            long pSleepTime = sleepTime - (newTime - pastTime);
            if (pSleepTime < 1) {
                pSleepTime = 1;
            }
            notifySleepTimeCalc(pSleepTime);
            Utility.threadSleep(pSleepTime);
        }

        end();
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
