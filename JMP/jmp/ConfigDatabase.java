package jmp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import function.Utility;

public class ConfigDatabase {

    // 設定データベース
    private HashMap<String, String> database = null;
    public static final String CFG_KEY_MIDILIST = "MIDILIST";
    public static final String CFG_KEY_MIDIOUT = "MIDIOUT";
    public static final String CFG_KEY_MIDIIN = "MIDIIN";
    public static final String CFG_KEY_AUTOPLAY = "AUTOPLAY";
    public static final String CFG_KEY_LOOPPLAY = "LOOPPLAY";
    public static final String CFG_KEY_SHOW_STARTUP_DEVICE_SETUP = "SHOW_STARTUP_DEVICE_SETUP";

    public ConfigDatabase() {
        database = new HashMap<String, String>();
        database.clear();

        initialize();
    }

    public void initialize() {
        database.put(CFG_KEY_MIDILIST, "");
        database.put(CFG_KEY_MIDIOUT, "");
        database.put(CFG_KEY_MIDIIN, "");
        database.put(CFG_KEY_AUTOPLAY, "FALSE");
        database.put(CFG_KEY_LOOPPLAY, "FALSE");
        database.put(CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, "TRUE");
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
                textContents.add(key + "=" + value);
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
                String[] sLine = line.split("=");
                if (sLine.length >= 1) {
                    String key = sLine[0].trim();
                    String value = (sLine.length >= 2) ? sLine[1] : "";
                    database.put(key, value);
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

}
