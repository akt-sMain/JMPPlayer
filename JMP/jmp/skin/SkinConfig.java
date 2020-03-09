package jmp.skin;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import function.Platform;
import function.Utility;

public class SkinConfig {

    public static final String KEY_USE = "USE";
    public static final String KEY_TRANSPARENT = "TRANSPARENT";
    public static final String KEY_CONVERT_TRANS = "CONVERT_TRANS";

    public static final String SKIN_FOLDER_NAME = "skin";
    public static final Color DEFAULT_TRANS_COLOR = new Color(255, 127, 39);

    private String refPath = Utility.pathCombin(Platform.getCurrentPath(false), SKIN_FOLDER_NAME);
    private String name = "default";
    private boolean isConvertTransparent = false;
    private Color transparentColor = DEFAULT_TRANS_COLOR;

    public SkinConfig() {
    }

    public void read(File file) throws IOException {
        List<String> content = Utility.getTextFileContents(file.getPath());

        for (String line : content) {
            if (line.startsWith("#") == true) {
                continue;
            }

            String[] sLine = line.split("=");
            if (sLine.length >= 2) {
                String key = sLine[0];
                String value = sLine[1];
                if (key.equalsIgnoreCase(KEY_USE) == true) {
                    name = value;
                }
                else if (key.equalsIgnoreCase(KEY_TRANSPARENT) == true) {
                    try {
                        transparentColor = Utility.convertCodeToHtmlColor(value);
                    }
                    catch (NumberFormatException nfe) {
                        transparentColor = DEFAULT_TRANS_COLOR;
                    }
                }
                else if (key.equalsIgnoreCase(KEY_CONVERT_TRANS) == true) {
                    isConvertTransparent = Utility.tryParseBoolean(value, false);
                }
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTransparentColor(Color transparentColor) {
        this.transparentColor = transparentColor;
    }

    public Color getTransparentColor() {
        return transparentColor;
    }

    public String getName() {
        return name;
    }

    public String getRefPath() {
        return refPath;
    }

    public void setRefPath(String refPath) {
        this.refPath = refPath;
    }

    public boolean isConvertTransparent() {
        return isConvertTransparent;
    }

    public void setConvertTransparent(boolean isConvertTransparent) {
        this.isConvertTransparent = isConvertTransparent;
    }

}
