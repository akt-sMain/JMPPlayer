package jmp.task;

import java.util.ArrayList;

/**
 * シーケンスタスク。 <br>
 * コールバックを実行する
 *
 * @author akkut
 *
 */
public class TaskOfSequence extends TaskOfBase {
    private ArrayList<ICallbackFunction> callbackQue = new ArrayList<ICallbackFunction>();

    public TaskOfSequence() {
        super(100);
    }

    public void queuing(ICallbackFunction callbackFunction) {
        callbackQue.add(callbackFunction);
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
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

    @Override
    void end() {
        // コールバック関数のクリア
        callbackQue.clear();
    }

}
