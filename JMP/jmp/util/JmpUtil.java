package jmp.util;

import javax.swing.filechooser.FileNameExtensionFilter;

import jmp.core.DataManager;

public class JmpUtil {
    private JmpUtil() {
    }

    public static String genExtensions2Str(String... ex) {
        String ret = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += ex[i];
        }
        return ret;
    }

    public static String[] genStr2Extensions(String str) {
        String[] ret = str.split(",");
        return ret;
    }

    public static FileNameExtensionFilter createFileFilter(String exName, String... ex) {
        String exs = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                exs += ", ";
            }
            exs += String.format("*.%s", ex[i]);
        }

        String description = String.format("%s (%s)", exName, exs);
        return new FileNameExtensionFilter(description, ex);
    }

    public static boolean checkConfigKey(String key, String dKey) {
        if (key.equals(dKey) == true || key.equals(DataManager.CFG_KEY_INITIALIZE) == true) {
            return true;
        }
        return false;
    }
}
