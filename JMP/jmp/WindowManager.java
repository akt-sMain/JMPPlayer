package jmp;

import jlib.IJmpWindow;
import jlib.manager.IWindowManager;
import jlib.manager.JMPCoreAccessor;

public class WindowManager implements IWindowManager {

    public static final String[] WINDOW_NAMELIST = { "MAIN", "FILE_LIST", "HISTORY" };
    public static final String WINDOW_NAME_MAIN = WINDOW_NAMELIST[0];
    public static final String WINDOW_NAME_FILE_LIST = WINDOW_NAMELIST[1];
    public static final String WINDOW_NAME_HISTORY = WINDOW_NAMELIST[2];

    private WindowDatabase database = null;

    // 初期化フラグ
    private boolean initializeFlag = false;

    WindowManager() {
        database = new WindowDatabase();

        JMPCoreAccessor.register(this);
    }

    public boolean initFunc() {
        boolean result = true;
        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return result;
    }

    public boolean endFunc() {
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

}
