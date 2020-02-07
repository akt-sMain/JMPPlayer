package jmp;

import jlib.IJmpWindow;
import jlib.IWindowManager;
import jlib.manager.JMPCoreAccessor;

public class WindowManager implements IWindowManager {

    public static final String WINDOW_NAME_MAIN = "MAIN";
    public static final String WINDOW_NAME_FILE_LIST = "FILE_LIST";
    public static final String WINDOW_NAME_HISTORY = "HISTORY";

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

}
