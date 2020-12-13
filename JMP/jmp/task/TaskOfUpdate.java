package jmp.task;

import jlib.gui.IJmpMainWindow;
import jlib.plugin.IPlugin;
import jmp.JMPLoader;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.WindowManager;

/**
 * 更新タスク
 *
 * @author abs
 *
 */
public class TaskOfUpdate extends TaskOfBase {
    public TaskOfUpdate() {
        super(50);
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
        WindowManager wm = JMPCore.getWindowManager();
        PluginManager pm = JMPCore.getPluginManager();
        IJmpMainWindow win = wm.getMainWindow();

        win.update();
        pm.update();

        // スタンドアロンプラグインが閉じられているか確認
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            IPlugin plg = JMPCore.getStandAlonePlugin();
            if (plg != null) {
                if (plg.isOpen() == false) {
                    JMPLoader.exit();
                }
            }
        }
    }

    @Override
    void end() {
    }
}
