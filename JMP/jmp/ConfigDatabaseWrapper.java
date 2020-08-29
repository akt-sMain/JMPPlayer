package jmp;

import function.Platform;
import function.Platform.SystemProperty;
import function.Utility;
import jmp.core.DataManager;

public class ConfigDatabaseWrapper implements IJmpConfigDatabase {

    private ConfigDatabase database = null;

    public ConfigDatabaseWrapper() {
        database = new ConfigDatabase(DataManager.CFG_KEYSET);
        initialize();
    }

    public ConfigDatabaseWrapper(ConfigDatabase db) {
        database = db;
    }

    public void initialize() {
        String current = Platform.getCurrentPath();
        String desktop = getDesktopPath(current);

        // 初期化
        database.setConfigParam(DataManager.CFG_KEY_PLAYLIST, desktop);
        database.setConfigParam(DataManager.CFG_KEY_MIDIOUT, "");
        database.setConfigParam(DataManager.CFG_KEY_MIDIIN, "");
        database.setConfigParam(DataManager.CFG_KEY_AUTOPLAY, "FALSE");
        database.setConfigParam(DataManager.CFG_KEY_LOOPPLAY, "FALSE");
        database.setConfigParam(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, "TRUE");
        database.setConfigParam(DataManager.CFG_KEY_LANGUAGE, "0");
        database.setConfigParam(DataManager.CFG_KEY_LOADED_FILE, "");
        database.setConfigParam(DataManager.CFG_KEY_LYRIC_VIEW, "TRUE");
        database.setConfigParam(DataManager.CFG_KEY_FFMPEG_PATH, "ffmpeg.exe");
        database.setConfigParam(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, "FALSE");
        database.setConfigParam(DataManager.CFG_KEY_USE_FFMPEG_PLAYER, "FALSE");
        database.setConfigParam(DataManager.CFG_KEY_FFMPEG_INSTALLED, "FALSE");
    }

    private String getDesktopPath(String defaultPath) {
        String desktop = Platform.getProperty(SystemProperty.USER_HOME);
        if (Utility.isExsistFile(desktop) == false) {
            return defaultPath;
        }

        String newDesktop = Utility.pathCombin(desktop, "Desktop");
        if (Utility.isExsistFile(newDesktop) == false) {
            return desktop;
        }
        return newDesktop;
    }

    public ConfigDatabase getConfigDatabase() {
        return database;
    }

    @Override
    public void setConfigParam(String key, String value) {
        database.setConfigParam(key, value);
    }

    @Override
    public String getConfigParam(String key) {
        return database.getConfigParam(key);
    }

}
