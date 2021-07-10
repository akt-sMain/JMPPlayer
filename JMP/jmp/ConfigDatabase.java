package jmp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jmp.util.JmpUtil;

public class ConfigDatabase {

    private static final String KER_SEPARATOR = "<->";

    // 設定データベース
    private Map<String, String> database = null;

    private String[] keyset = null;

    public ConfigDatabase(String[] keys) {
        setup(keys);
    }
    public ConfigDatabase(Set<String> keys) {
        setup(keySetToArray(keys));
    }

    private void setup(String[] keys) {
        database = new HashMap<String, String>();
        database.clear();

        keyset = keys;

        for (String key : keyset) {
            database.put(key, "");
        }
    }

    public static String[] keySetToArray(Set<String> keys) {
        String[] aKey = new String[keys.size()];
        int i = 0;
        Iterator<String> ite = keys.iterator();
        while (ite.hasNext()) {
            String s = ite.next();
            aKey[i] = s;
            i++;
        }
        return aKey;
    }

    public static ConfigDatabase create(String path) {
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        File file = new File(path);
        if (file.exists() == false) {
            return null;
        }

        try {
            List<String> textContents = JmpUtil.readTextFile(file);

            for (String line : textContents) {
                String[] sLine = line.split(KER_SEPARATOR);
                if (sLine.length >= 1) {
                    String key = sLine[0].trim();
                    String value = (sLine.length >= 2) ? sLine[1] : "";
                    keys.add(key);
                    values.add(value);
                }
            }
        }
        catch (Exception e) {
            return null;
        }

        String[] aKeys = new String[keys.size()];
        for (int i=0; i<aKeys.length; i++) {
            aKeys[i] = keys.get(i);
        }

        ConfigDatabase db = new ConfigDatabase(aKeys);
        for (int i=0; i<aKeys.length; i++) {
            db.setConfigParam(aKeys[i], values.get(i));
        }
        return db;
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
