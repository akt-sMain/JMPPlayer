package jmp.core;

import jlib.IJmpWindow;
import jlib.manager.IWindowManager;
import jlib.manager.JMPCoreAccessor;
import jmp.WindowDatabase;

public class WindowManager extends AbstractManager implements IWindowManager {

    public static final String[] WINDOW_NAMELIST = { "MAIN", "FILE_LIST", "HISTORY" };
    public static final String WINDOW_NAME_MAIN = WINDOW_NAMELIST[0];
    public static final String WINDOW_NAME_FILE_LIST = WINDOW_NAMELIST[1];
    public static final String WINDOW_NAME_HISTORY = WINDOW_NAMELIST[2];

    private WindowDatabase database = null;

    WindowManager(int pri) {
        super(pri, "window");

        database = new WindowDatabase();

        JMPCoreAccessor.register(this);
    }

    @Override
    public boolean initFunc() {
        boolean result = true;
        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return result;
    }

    @Override
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
