package jmp.task;

import java.util.ArrayList;

/**
 * タイマータスク。 <br>
 * 時系列コールバック関数の登録が可能
 *
 * @author akkut
 *
 */
public class TaskOfTimer extends TaskOfBase {

    //public static final long CYCLIC_TIMER_TASK_TIME = 100;

    private ArrayList<CallbackPackage> callbackPackages = new ArrayList<CallbackPackage>();

    public TaskOfTimer() {
        super(100);
    }

    public void addCallbackPackage(CallbackPackage pakage) {
        callbackPackages.add(pakage);
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
        for (int i = 0; i < callbackPackages.size(); i++) {
            CallbackPackage cp = callbackPackages.get(i);
            cp.callback();

            if (cp.isDeleteConditions() == true) {
                // コールバック関数の削除
                cp = null;
                callbackPackages.remove(i);
            }
        }
    }

    @Override
    void end() {
        // コールバック関数のクリア
        callbackPackages.clear();
    }
}
