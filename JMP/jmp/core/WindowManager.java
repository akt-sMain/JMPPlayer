package jmp.core;

import jlib.core.IWindowManager;
import jlib.gui.IJmpWindow;
import jmp.gui.WindowDatabase;

public class WindowManager extends AbstractManager implements IWindowManager {

    public static final String WINDOW_NAME_MAIN = "MAIN";
    public static final String WINDOW_NAME_FILE_LIST = "FILE_LIST";
    public static final String WINDOW_NAME_HISTORY = "HISTORY";
    public static final String WINDOW_NAME_MIDI_SETUP = "MIDI_SETUP";
    public static final String[] WINDOW_NAMELIST = { WINDOW_NAME_MAIN, WINDOW_NAME_FILE_LIST, WINDOW_NAME_HISTORY,
            WINDOW_NAME_MIDI_SETUP };

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
        return result;
    }

    @Override
    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
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

}
