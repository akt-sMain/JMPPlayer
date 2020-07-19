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
import jmp.CommonRegister;
import jmp.ConfigDatabase;
import jmp.gui.FFmpegConvertDialog;
import jmp.gui.HistoryDialog;
import jmp.gui.LicenseReaderDialog;
import jmp.gui.MidiDataTransportDialog;
import jmp.gui.MidiFileListDialog;
import jmp.gui.MidiMessageMonitor;
import jmp.gui.SelectLanguageDialog;
import jmp.gui.WindowDatabase;
import jmp.gui.ui.IJMPComponentUI;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;
import jmplayer.JMPPlayer;

public class WindowManager extends AbstractManager implements IWindowManager {

    public static final Rectangle DEFAULT_PLAYER_WINDOW_SIZE = new Rectangle(20, 20, 480, 210);

    public static final String WINDOW_NAME_MAIN = "MAIN";
    public static final String WINDOW_NAME_FILE_LIST = "FILE_LIST";
    public static final String WINDOW_NAME_HISTORY = "HISTORY";
    public static final String WINDOW_NAME_MIDI_SETUP = "MIDI_SETUP";
    public static final String WINDOW_NAME_MIDI_MONITOR = "MIDI_MONITOR";
    public static final String WINDOW_NAME_MIDI_SENDER = "MIDI_SENDER";
    public static final String WINDOW_NAME_LANGUAGE = "LANGUAGE";
    public static final String WINDOW_NAME_LICENSE = "LICENSE";
    public static final String WINDOW_NAME_FFMPEG = "FFMPEG";
    public static final String[] WINDOW_NAMELIST = { WINDOW_NAME_MAIN, WINDOW_NAME_FILE_LIST, WINDOW_NAME_HISTORY, WINDOW_NAME_MIDI_SETUP,
            WINDOW_NAME_MIDI_MONITOR, WINDOW_NAME_MIDI_SENDER, WINDOW_NAME_LANGUAGE, WINDOW_NAME_LICENSE, WINDOW_NAME_FFMPEG };

    private WindowDatabase database = null;

    WindowManager(int pri) {
        super(pri, "window");

        database = new WindowDatabase();
    }

    @Override
    protected boolean initFunc() {
        boolean result = true;
        if (initializeFlag == false) {
            initializeFlag = true;
        }

        // Windowインスタンス作成
        makeWindowInstance();

        return result;
    }

    @Override
    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        setVisibleAll(false);
        return true;
    }

    @Override
    protected void notifyUpdateCommonRegister(String key) {
        if (key.equals(CommonRegister.COMMON_REGKEY_PLAYER_BACK_COLOR) == true) {
            for (IJmpWindow win : database.getAccessor()) {
                if (win != null) {
                    if (win instanceof IJMPComponentUI) {
                        IJMPComponentUI ui = (IJMPComponentUI)win;
                        ui.updateBackColor();
                    }
                }
            }
        }
        super.notifyUpdateCommonRegister(key);
    }

    private void makeWindowInstance() {
        new LicenseReaderDialog();
        new MidiMessageMonitor();
        new MidiDataTransportDialog();
        new SelectLanguageDialog();
        new HistoryDialog();
        new MidiFileListDialog();

        /* Windows用のGUI */
        new FFmpegConvertDialog();

        // メインウィンドウ登録
        registerMainWindow(new JMPPlayer());
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

    public void addPluginMenuItem(String name, IPlugin plugin) {
        database.addPluginMenuItem(name, plugin);
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
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);
        updateConfig(key);

        if (key.equals(ConfigDatabase.CFG_KEY_LANGUAGE) == true) {
            // 言語更新
            updateLanguage();
        }
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
        JMPCore.getTaskManager().getTaskOfSequence().queuing(new ICallbackFunction() {
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
}
