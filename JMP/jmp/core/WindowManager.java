package jmp.core;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import function.Platform;
import function.Utility;
import jlib.core.IWindowManager;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jlib.plugin.IPlugin;
import jmp.JMPFlags;
import jmp.gui.BuiltinSynthSetupDialog;
import jmp.gui.FFmpegConvertDialog;
import jmp.gui.HistoryDialog;
import jmp.gui.LicenseReaderDialog;
import jmp.gui.MidiDataTransportDialog;
import jmp.gui.MidiFileListDialog;
import jmp.gui.MidiMessageMonitor;
import jmp.gui.PluginManagerDialog;
import jmp.gui.SelectLanguageDialog;
import jmp.gui.SelectSynthsizerDialog;
import jmp.gui.WindowDatabase;
import jmp.gui.ui.IJMPComponentUI;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;
import jmplayer.JMPPlayerWindow;

public class WindowManager extends AbstractManager implements IWindowManager {

    public static final Rectangle DEFAULT_PLAYER_WINDOW_SIZE = new Rectangle(20, 20, 480, 210);

    public static final String[] WINDOW_NAMELIST = { WINDOW_NAME_MAIN, WINDOW_NAME_FILE_LIST, WINDOW_NAME_HISTORY, WINDOW_NAME_MIDI_SETUP,
            WINDOW_NAME_MIDI_MONITOR, WINDOW_NAME_MIDI_SENDER, WINDOW_NAME_LANGUAGE, WINDOW_NAME_LICENSE, WINDOW_NAME_FFMPEG, WINDOW_NAME_PLUGIN_MANAGER };

    private WindowDatabase database = null;

    /* JMsynth用のフレーム(WindowDatabaseに含まない) */
    private BuiltinSynthSetupDialog builtinSynthFrame = null;

    WindowManager(int pri) {
        super(pri, "window");

        database = new WindowDatabase();
    }

    @Override
    protected boolean initFunc() {

        // Windowインスタンス作成
        makeWindowInstance();

        return super.initFunc();
    }

    @Override
    protected boolean endFunc() {
        if (super.endFunc() == false) {
            return false;
        }
        closeBuiltinSynthFrame();
        setVisibleAll(false);
        return true;
    }

    @Override
    protected void notifyUpdateCommonRegister(String key) {
        SystemManager system = JMPCore.getSystemManager();
        if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_PLAYER_BACK_COLOR)) == true) {
            for (IJmpWindow win : database.getAccessor()) {
                if (win != null) {
                    if (win instanceof IJMPComponentUI) {
                        IJMPComponentUI ui = (IJMPComponentUI) win;
                        ui.updateBackColor();
                    }
                }
            }
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_DEBUGMODE)) == true) {
            for (IJmpWindow win : database.getAccessor()) {
                if (win != null) {
                    if (win instanceof IJMPComponentUI) {
                        IJMPComponentUI ui = (IJMPComponentUI) win;
                        ui.updateDebugMenu();
                    }
                }
            }
        }
        super.notifyUpdateCommonRegister(key);
    }

    private void makeWindowInstance() {
        register(WINDOW_NAME_LICENSE, new LicenseReaderDialog());
        register(WINDOW_NAME_MIDI_MONITOR, new MidiMessageMonitor());
        register(WINDOW_NAME_MIDI_SENDER, new MidiDataTransportDialog());
        register(WINDOW_NAME_LANGUAGE, new SelectLanguageDialog());
        register(WINDOW_NAME_HISTORY, new HistoryDialog());
        register(WINDOW_NAME_FILE_LIST, new MidiFileListDialog());
        register(WINDOW_NAME_PLUGIN_MANAGER, new PluginManagerDialog());
        register(WINDOW_NAME_FFMPEG, new FFmpegConvertDialog());

        // メインウィンドウ登録
        registerMainWindow(new JMPPlayerWindow());
    }

    public void processingBeforePlay() {
        // 再生前に行う処理
        IJmpWindow win;

        win = getWindow(WINDOW_NAME_LICENSE);
        if (win.isWindowVisible() == true) {
            win.hideWindow();
        }
        win = getWindow(WINDOW_NAME_PLUGIN_MANAGER);
        if (win.isWindowVisible() == true) {
            win.hideWindow();
        }
        win = getWindow(WINDOW_NAME_LANGUAGE);
        if (win.isWindowVisible() == true) {
            win.hideWindow();
        }
    }

    public boolean register(IJmpWindow window) {
        if (database == null) {
            return false;
        }
        return database.addWindow(window);
    }

    public boolean register(String name, IJmpWindow window) {
        if (database == null) {
            return false;
        }
        return database.setWindow(name, window);
    }

    public void registerMainWindow(IJmpMainWindow win) {
        if (getMainWindow() == null) {
            database.setMainWindow(win);
            register(WINDOW_NAME_MAIN, win);
        }
    }

    @Override
    public IJmpMainWindow getMainWindow() {
        return database.getMainWindow();
    }

    @Override
    public IJmpWindow getWindow(String name) {
        return database.getWindow(name);
    }

    @Override
    public String[] getWindowNameList() {
        return WINDOW_NAMELIST;
    }

    public void setVisibleAll(boolean visible) {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                if (visible == true) {
                    win.showWindow();
                }
                else {
                    win.hideWindow();
                }
            }
        }
    }

    public void updatePluginMenuItems() {
        PluginManager pm = JMPCore.getPluginManager();

        database.clearPluginMenuItem();
        for (String name : pm.getPluginsNameSet()) {
            IPlugin p = pm.getPlugin(name);
            database.addPluginMenuItem(name, p);
        }

        ((JMPPlayerWindow)getMainWindow()).updatePluginMenu();
    }

    public List<JMenuItem> getPluginMenuItems() {
        return database.getPluginMenuItems();
    }

    public void updateConfig(String key) {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                win.updateConfig(key);
            }
        }
    }

    public void updateLanguage() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                win.updateLanguage();
            }
        }
    }

    public void initializeLayout() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                win.initializeLayout();
            }
        }

        // プラグインもすべて閉じる
        PluginManager pm = JMPCore.getPluginManager();
        for (String pname : pm.getPluginsNameSet()) {
            IPlugin plg = pm.getPlugin(pname);
            if (plg != null) {
                plg.close();
            }
        }
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);
        updateConfig(key);

        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_LANGUAGE) == true) {
            // 言語更新
            updateLanguage();
        }

        // 再描画を実行させる
        JMPFlags.ForcedCyclicRepaintFlag = true;
    }

    public void showErrorMessageDialogSync(String message) {
        IJmpMainWindow win = getMainWindow();
        Component parent = null;
        if (win instanceof Component) {
            parent = (Component) win;
        }
        else {
            parent = null;
        }

        JOptionPane.showMessageDialog(parent, message, JMPCore.getLanguageManager().getLanguageStr(LangID.Error), JOptionPane.ERROR_MESSAGE);
        SystemManager.TempResisterEx = null;
    }

    public void showErrorMessageDialog(String message) {
        if (SystemManager.TempResisterEx != null) {
            String stackTrace = function.Error.getMsg(SystemManager.TempResisterEx);
            showMessageDialog(Utility.stringsCombin(message, Platform.getNewLine(), Platform.getNewLine(), stackTrace),
                    JMPCore.getLanguageManager().getLanguageStr(LangID.Error), JOptionPane.ERROR_MESSAGE);
        }
        else {
            showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Error), JOptionPane.ERROR_MESSAGE);
        }
        SystemManager.TempResisterEx = null;
    }

    public void showInformationMessageDialog(String message) {
        showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Message), JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessageDialog(String message, int option) {
        showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Message), option);
    }

    public void showMessageDialog(String message, String title, int option) {
        JMPCore.getTaskManager().queuing(new ICallbackFunction() {
            @Override
            public void callback() {
                IJmpMainWindow win = getMainWindow();
                Component parent = null;
                if (win instanceof Component) {
                    parent = (Component) win;
                }
                else {
                    parent = null;
                }
                JOptionPane.showMessageDialog(parent, message, title, option);
            }
        });
    }

    public void setBuiltinSynthFrame(BuiltinSynthSetupDialog frame) {
        if (this.builtinSynthFrame != null && this.builtinSynthFrame != frame) {
            this.builtinSynthFrame.dispose();
        }
        this.builtinSynthFrame = frame;
    }

    public boolean isValidBuiltinSynthFrame() {
        if (JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT).equals(SelectSynthsizerDialog.JMSYNTH_ITEM_NAME) == false) {
            return false;
        }
        if (builtinSynthFrame == null) {
            return false;
        }
        return true;
    }

    public boolean isVisibleBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return false;
        }
        return builtinSynthFrame.isVisible();
    }

    public void openBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return;
        }
        builtinSynthFrame.setVisible(true);
    }

    public void closeBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return;
        }
        builtinSynthFrame.setVisible(false);
    }

    public void repaintBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return;
        }
        builtinSynthFrame.repaintWavePane();
    }

    public void repaint(String name) {
        IJmpWindow win = getWindow(name);
        repaint(win);
    }

    public void repaint(IJmpWindow win) {
        if (win != null) {
            if (win instanceof Component) {
                Component w = (Component)win;
                if (w != null) {
                    w.repaint();
                }
            }
        }
    }
}
