package jmp.util;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Utility;
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

    public static ImageIcon convertImageIcon(Image img) {
        return img == null ? null : new ImageIcon(img);
    }

    public static boolean toBoolean(String str) {
        return toBoolean(str, false);
    }

    public static boolean toBoolean(String str, boolean def) {
        return Utility.tryParseBoolean(str, def);
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int def) {
        return Utility.tryParseInt(str, def);
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    public static long toLong(String str, long def) {
        return Utility.tryParseLong(str, def);
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }

    public static float toFloat(String str, float def) {
        return Utility.tryParseFloat(str, def);
    }
}
