package jmp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jmp.util.JmpUtil;

public class ConfigDatabase {

    private static final String KER_SEPARATOR = "<->";

    // 設定データベース
    private Map<String, String> database = null;

    private String[] keyset = null;

    public ConfigDatabase(String[] keys) {
        database = new HashMap<String, String>();
        database.clear();
        keyset = keys;

        for (String key : keyset) {
            database.put(key, "");
        }
    }

    public String[] getKeySet() {
        return keyset;
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
            JmpUtil.writeTextFile(path, textContents);
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
            List<String> textContents = JmpUtil.readTextFile(file);

            for (String line : textContents) {
                String[] sLine = line.split(KER_SEPARATOR);
                if (sLine.length >= 1) {
                    String key = sLine[0].trim();
                    for (String ckey : getKeySet()) {
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
