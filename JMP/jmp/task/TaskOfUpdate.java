package jmp.task;

import java.awt.Window;

import jlib.gui.IJmpMainWindow;
import jlib.plugin.IPlugin;
import jmp.JMPLoader;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.WindowManager;
import jmp.gui.JmpQuickLaunch;

/**
 * 更新タスク
 *
 * @author abs
 *
 */
public class TaskOfUpdate extends TaskOfBase {
    private static final int CYCLIC_REPAINT_MSEC = 5000;
    private static final int CYCLIC_REPAINT_MSEC_PLAY = 1000;
    private int cyclicRepaintCount = 0;
    private boolean pastRunnableState = false;

    public TaskOfUpdate() {
        super(100);
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

        // 再描画
        boolean isRepaint = false;
        if (JMPCore.getSoundManager().isPlay() == true) {
            if ((CYCLIC_REPAINT_MSEC_PLAY / getSleepTime()) <= cyclicRepaintCount) {
                isRepaint = true;
            }
        }
        else {
            if ((CYCLIC_REPAINT_MSEC / getSleepTime()) <= cyclicRepaintCount) {
                // 定周期
                isRepaint = true;
            }
            if (pastRunnableState == true) {
                // 停止時の初回のみ再描画する
                isRepaint = true;
            }
        }

        if (isRepaint == true) {
            // 再描画
            if (win instanceof Window) {
                ((Window)win).repaint();
            }
            for (JmpQuickLaunch launch : JmpQuickLaunch.Accessor) {
                launch.repaint();
            }
            cyclicRepaintCount = 0;
        }
        cyclicRepaintCount++;

        pastRunnableState = JMPCore.getSoundManager().isPlay();

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
