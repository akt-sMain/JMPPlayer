package jmp.task;

import java.util.ArrayList;

import function.Utility;

/**
 * シーケンスタスク。 <br>
 * コールバックを実行する
 *
 * @author akkut
 *
 */
public class TaskOfSequence extends Thread implements ITask {
    private ArrayList<ICallbackFunction> callbackQue = new ArrayList<ICallbackFunction>();
    public static final long CYCLIC_TIME = 100; // (mills)
    private boolean isRunnable = true;

    public TaskOfSequence() {
    }

    @Override
    public void run() {
        while (isRunnable) {

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
            Utility.threadSleep(CYCLIC_TIME);
        }

        // コールバック関数のクリア
        callbackQue.clear();
    }

    public void queuing(ICallbackFunction callbackFunction) {
        callbackQue.add(callbackFunction);
    }

    @Override
    public void exit() {
        isRunnable = false;
    }

}
