package jmp.skin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import function.Utility;

public class SkinGlobalConfig {

    public static final String KEY_USE = "USE";

    private String name = "default";

    public SkinGlobalConfig() {
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
            }
        }
    }

    public void initialize() {
        name = "default";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
