package jmp.task;

import function.Utility;
import jlib.IJmpMainWindow;
import jlib.IPlugin;
import jmp.JMPCore;
import jmp.JMPLoader;
import jmp.PluginManager;
import jmp.SystemManager;

/**
 * 更新タスク
 *
 * @author abs
 *
 */
public class TaskOfUpdate extends Thread implements ITask {
    // ! 一定周期再描画時間（ms）
    public static long CyclicRepaintTime = 20;

    private boolean isRunnable = true;

    public TaskOfUpdate() {

    }

    @Override
    public void run() {
	SystemManager system = JMPCore.getSystemManager();
	PluginManager pm = JMPCore.getPluginManager();

	IJmpMainWindow win = system.getMainWindow();
	while (isRunnable) {
	    win.update();
	    pm.update();

	    // スタンドアロンプラグインが閉じられているか確認
	    IPlugin plg = JMPCore.StandAlonePlugin;
	    if (plg != null) {
		if (plg.isOpen() == false) {
		    JMPLoader.exit();
		}
	    }

	    Utility.threadSleep(CyclicRepaintTime);
	}
    }

    @Override
    public void exit() {
	isRunnable = false;
    }
}
