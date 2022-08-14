package jmp.task;

import java.util.ArrayList;
import java.util.Iterator;

import jmp.util.JmpUtil;

public abstract class TaskOfBase implements ITask, Runnable {

    private ArrayList<ICallbackFunction> callbackQue = null;
    private ArrayList<Runnable> runnableQue = null;
    private boolean isRunnable = true;
    protected Thread thread = null;
    protected long sleepTime = 100;
    protected long waitTime = 0;

    public TaskOfBase(long sleepTime) {
        this.callbackQue = new ArrayList<ICallbackFunction>();
        this.runnableQue = new ArrayList<Runnable>();
        this.sleepTime = sleepTime;
        this.isRunnable = true;
        this.waitTime = 0;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        begin();

        while (isRunnable) {

            long pastTime = System.currentTimeMillis();

            if (callbackQue != null) {
                execCallback();
            }
            if (runnableQue != null) {
                execRunnable();
            }
            loop();

            long newTime = System.currentTimeMillis();
            long pSleepTime = sleepTime - (newTime - pastTime);
            if (pSleepTime < 1) {
                pSleepTime = 1;
            }
            notifySleepTimeCalc(pSleepTime);

            if (this.waitTime > 0) {
                // 待ち時間を加算
                pSleepTime += this.waitTime;
                this.waitTime = 0;
            }
            JmpUtil.threadSleep(pSleepTime);
        }

        end();

        // コールバック関数のクリア
        clearQue();
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

    @Override
    public void waitTask(long sleepTime) {
        this.waitTime = sleepTime;
    }

    public void queuing(ICallbackFunction callbackFunction) {
        callbackQue.add(callbackFunction);
    }
    
    public void queuing(Runnable runnable) {
        runnableQue.add(runnable);
    }
    
    @Override
    public void clearQue() {
        if (callbackQue != null) {
            callbackQue.clear();
        }
        if (runnableQue != null) {
            runnableQue.clear();
        }
    }

    protected void execCallback() {
        synchronized (callbackQue) {
            // スタックされたコールバックを呼び出し
            Iterator<ICallbackFunction> i = callbackQue.iterator();
            while (i.hasNext()) {
                ICallbackFunction exec = i.next();
                exec.preCall();
                exec.callback();
                exec.postCall();
                i.remove();
            }
        }
    }
    
    protected void execRunnable() {
        synchronized (runnableQue) {
            // スタックされたコールバックを呼び出し
            Iterator<Runnable> i = runnableQue.iterator();
            while (i.hasNext()) {
                Runnable exec = i.next();
                exec.run();
                i.remove();
            }
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

    @Override
    public boolean isRunnable() {
        return isRunnable;
    }

}
