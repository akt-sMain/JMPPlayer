package jmp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import function.Platform;
import function.Platform.SystemProperty;
import function.Utility;

public class ConfigDatabase {

    private static final String KER_SEPARATOR = "<->";

    // 設定データベース
    private Map<String, String> database = null;
    public static final String CFG_KEY_PLAYLIST = "PLAYLIST";
    public static final String CFG_KEY_MIDIOUT = "MIDIOUT";
    public static final String CFG_KEY_MIDIIN = "MIDIIN";
    public static final String CFG_KEY_AUTOPLAY = "AUTOPLAY";
    public static final String CFG_KEY_LOOPPLAY = "LOOPPLAY";
    public static final String CFG_KEY_SHOW_STARTUP_DEVICE_SETUP = "SHOW_STARTUP_DEVICE_SETUP";
    public static final String CFG_KEY_LANGUAGE = "LANGUAGE";
    public static final String CFG_KEY_LOADED_FILE = "LOADED_FILE";
    public static final String CFG_KEY_FFMPEG_PATH = "FFMPEG_PATH";
    public static final String CFG_KEY_FFMPEG_OUTPUT = "FFMPEG_OUTPUT";
    public static final String CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE = "FFMPEG_LEAVE_OUTPUT_FILE";
    public static final String CFG_KEY_USE_FFMPEG_PLAYER = "USE_FFMPEG_PLAYER";
    // ↓KEY追加後、必ずCFG_KEYSETに追加すること!!
    public static final String[] CFG_KEYSET = { CFG_KEY_PLAYLIST, CFG_KEY_MIDIOUT, CFG_KEY_MIDIIN, CFG_KEY_AUTOPLAY, CFG_KEY_LOOPPLAY,
            CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, CFG_KEY_LANGUAGE, CFG_KEY_LOADED_FILE, CFG_KEY_FFMPEG_PATH, CFG_KEY_FFMPEG_OUTPUT,
            CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, CFG_KEY_USE_FFMPEG_PLAYER, };

    public ConfigDatabase() {
        database = new HashMap<String, String>();
        database.clear();
        for (String key : CFG_KEYSET) {
            database.put(key, "");
        }

        initialize();
    }

    public void initialize() {
        String current = Platform.getCurrentPath();
        String desktop = getDesktopPath(current);

        // 初期化
        setConfigParam(CFG_KEY_PLAYLIST, desktop);
        setConfigParam(CFG_KEY_MIDIOUT, "");
        setConfigParam(CFG_KEY_MIDIIN, "");
        setConfigParam(CFG_KEY_AUTOPLAY, "FALSE");
        setConfigParam(CFG_KEY_LOOPPLAY, "FALSE");
        setConfigParam(CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, "TRUE");
        setConfigParam(CFG_KEY_LANGUAGE, "0");
        setConfigParam(CFG_KEY_LOADED_FILE, "");
        setConfigParam(CFG_KEY_FFMPEG_PATH, "ffmpeg.exe");
        setConfigParam(CFG_KEY_FFMPEG_OUTPUT, "output");
        setConfigParam(CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, "FALSE");
        setConfigParam(CFG_KEY_USE_FFMPEG_PLAYER, "FALSE");
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

    public void setConfigParam(String key, String value) {
        if (database.containsKey(key) == true) {
            database.put(key, value);
        }
    }

    public String getConfigParam(String key) {
        String ret = "";
        if (database.containsKey(key) == true) {
            ret = database.get(key);
        }
        return ret;
    }

    public boolean output(String path) {
        boolean ret = true;

        try {
            List<String> textContents = new LinkedList<String>();

            for (String key : database.keySet()) {
                String value = getConfigParam(key);
                textContents.add(key + KER_SEPARATOR + value);
            }
            Utility.outputTextFile(path, textContents);
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public boolean reading(String path) {
        boolean ret = true;
        File file = new File(path);
        if (file.exists() == false) {
            return false;
        }

        try {
            List<String> textContents = Utility.getTextFileContents(path);

            for (String line : textContents) {
                String[] sLine = line.split(KER_SEPARATOR);
                if (sLine.length >= 1) {
                    String key = sLine[0].trim();
                    for (String ckey : CFG_KEYSET) {
                        if (key.equals(ckey) == true) {
                            String value = (sLine.length >= 2) ? sLine[1] : "";
                            database.put(key, value);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

}
