package jmp;

import javax.swing.filechooser.FileNameExtensionFilter;

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
}
