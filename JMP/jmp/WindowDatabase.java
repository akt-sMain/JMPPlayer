package jmp;

import java.util.HashMap;
import java.util.Map;

import jlib.IJmpWindow;

public class WindowDatabase {

    // 設定データベース
    private Map<String, IJmpWindow> database = null;

    public WindowDatabase() {
        database = new HashMap<String, IJmpWindow>();
        database.clear();
    }

    public boolean setWindow(String name, IJmpWindow window) {
        boolean ret = true;
        if (database.containsKey(name) == false) {
            database.put(name, window);
        }
        else {
            ret = false;
        }
        return ret;
    }

    public IJmpWindow getWindow(String name) {
        if (database.containsKey(name) == true) {
            return database.get(name);
        }
        else {
            return null;
        }
    }

}
