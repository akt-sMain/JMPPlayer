package jmp.core;

import java.awt.Rectangle;
import java.util.List;

import javax.swing.JMenuItem;

import function.Platform;
import function.Platform.KindOfPlatform;
import jlib.core.IWindowManager;
import jlib.gui.IJmpWindow;
import jlib.plugin.IPlugin;
import jmp.gui.FFmpegConvertDialog;
import jmp.gui.HistoryDialog;
import jmp.gui.LicenseReaderDialog;
import jmp.gui.MidiDataTransportDialog;
import jmp.gui.MidiFileListDialog;
import jmp.gui.MidiMessageMonitor;
import jmp.gui.SelectLanguageDialog;
import jmp.gui.WindowDatabase;
import jmp.gui.ui.IJMPComponentUI;
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
        if (key.equals(SystemManager.CommonRegister.COMMON_REGKEY_PLAYER_BACK_COLOR) == true) {
            for (String name : getWindowNameList()) {
                IJmpWindow win = getWindow(name);
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

        /* Windows用の処理 */
        if (Platform.getRunPlatform() == KindOfPlatform.WINDOWS) {
            new FFmpegConvertDialog();
        }

        // メインウィンドウ登録
        JMPCore.getSystemManager().registerMainWindow(new JMPPlayer());
    }

    public boolean register(String name, IJmpWindow window) {
        return database.setWindow(name, window);
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
        for (String name : getWindowNameList()) {
            IJmpWindow win = getWindow(name);
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

    public void updateLanguage() {
        for (String name : getWindowNameList()) {
            IJmpWindow win = getWindow(name);
            if (win != null) {
                win.updateLanguage();
            }
        }
    }

    public void initializeLayout() {
        for (String name : getWindowNameList()) {
            IJmpWindow win = getWindow(name);
            if (win != null) {
                win.initializeLayout();
            }
        }
    }

    public void updateConfig(String key) {
        for (String name : getWindowNameList()) {
            IJmpWindow win = getWindow(name);
            if (win != null) {
                win.updateConfig(key);
            }
        }
    }
}
