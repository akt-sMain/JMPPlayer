package jmp.task;

import jlib.gui.IJmpMainWindow;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.WindowManager;
import jmp.gui.JmpQuickLaunch;
import jmp.plugin.PluginWrapper;

/**
 * 更新タスク
 *
 * @author abs
 *
 */
public class TaskOfUpdate extends TaskOfBase {
    private static final int CYCLIC_UPDATE_MSEC = 100;
    private static final int CYCLIC_REPAINT_MSEC = 5000;
    private static final int CYCLIC_REPAINT_MSEC_PLAY = 1000;
    private int cyclicUpdateCount = 0;
    private int cyclicRepaintCount = 0;
    private boolean pastRunnableState = false;

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
        IJmpMainWindow mainWindow = wm.getMainWindow();

        boolean isUpdate = false;
        boolean isRepaint = false;
        boolean isRepaintMain = false;

        // 再描画カウント
        if (JMPCore.getSoundManager().isPlay() == true) {
            if ((CYCLIC_REPAINT_MSEC_PLAY / getSleepTime()) <= cyclicRepaintCount) {
                isRepaintMain = true;
            }
        }
        else {
            if (pastRunnableState == true) {
                // 停止時の初回のみ再描画する
                isRepaint = true;
            }
        }
        // 定周期再描画
        if ((CYCLIC_REPAINT_MSEC / getSleepTime()) <= cyclicRepaintCount) {
            isRepaint = true;
        }
        // 更新カウント
        if ((CYCLIC_UPDATE_MSEC / getSleepTime()) <= cyclicUpdateCount) {
            isUpdate = true;
        }

        // 強制再描画
        if (JMPFlags.ForcedCyclicRepaintFlag == true) {
            isUpdate = true;
            isRepaint = true;
            JMPFlags.ForcedCyclicRepaintFlag = false;
        }

        // 自作シンセ再描画
        wm.repaintBuiltinSynthFrame();

        // 更新
        if (isUpdate == true) {
            mainWindow.update();
            pm.update();
            cyclicUpdateCount = 0;
        }
        if (isRepaint == true) {
            // 再描画
            isRepaintMain = true;
            wm.repaintAll();
            cyclicRepaintCount = 0;
        }
        if (isRepaintMain == true) {
            mainWindow.repaintWindow();
            for (JmpQuickLaunch launch : JmpQuickLaunch.Accessor) {
                launch.repaint();
            }
        }
        cyclicRepaintCount++;
        cyclicUpdateCount++;

        pastRunnableState = JMPCore.getSoundManager().isPlay();

        // スタンドアロンプラグインが閉じられているか確認
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            PluginWrapper pw = JMPCore.getStandAlonePluginWrapper();
            if (pw != null) {
                if (pw.isOpen() == false) {
                    JMPLoader.exit();
                }
            }
        }
    }

    @Override
    void end() {
    }
}
