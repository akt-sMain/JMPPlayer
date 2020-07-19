package jmp;

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
}
