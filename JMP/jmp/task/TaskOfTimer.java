package jmp.task;

import java.util.ArrayList;

import function.Utility;

/**
 * タイマータスク。 <br>
 * 時系列コールバック関数の登録が可能
 *
 * @author akkut
 *
 */
public class TaskOfTimer extends Thread implements ITask {
    private ArrayList<CallbackPackage> callbackPackages = new ArrayList<CallbackPackage>();
    public static final long CYCLIC_TIME = 100; // (mills)
    private boolean isRunnable = true;

    public TaskOfTimer() {

    }

    @Override
    public void run() {
	while (isRunnable) {

	    for (int i = 0; i < callbackPackages.size(); i++) {
		CallbackPackage cp = callbackPackages.get(i);
		cp.callback();

		if (cp.isDeleteConditions() == true) {
		    // コールバック関数の削除
		    cp = null;
		    callbackPackages.remove(i);
		}
	    }
	    Utility.threadSleep(CYCLIC_TIME);
	}

	// コールバック関数のクリア
	callbackPackages.clear();
    }

    @Override
    public void exit() {
	isRunnable = false;
    }

    public void addCallbackPackage(CallbackPackage pakage) {
	callbackPackages.add(pakage);
    }
}
