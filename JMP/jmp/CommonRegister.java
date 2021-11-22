package jmp;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jmp.util.JmpUtil;

public class CommonRegister {
    private static final String KER_SEPARATOR = "<->";

    public class CommonRegisterINI {
        public String key, value;
        public boolean isWrite = false;

        public CommonRegisterINI(String key, String value, boolean isWrite) {
            this.key = key;
            this.value = value;
            this.isWrite = isWrite;
        }
    }

    private List<CommonRegisterINI> iniList = null;

    public CommonRegister() {
        iniList = new ArrayList<CommonRegisterINI>();
        iniList.clear();
    }

    public CommonRegister(CommonRegisterINI... inis) {
        this();

        for (int i = 0; i < inis.length; i++) {
            add(inis[i]);
        }
    }

    private CommonRegisterINI getIni(String key) {
        for (CommonRegisterINI ini : iniList) {
            if (ini.key.equalsIgnoreCase(key) == true) {
                return ini;
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        for (CommonRegisterINI ini : iniList) {
            if (ini.key.equalsIgnoreCase(key) == true) {
                return true;
            }
        }
        return false;
    }

    public void add(String key, String value) {
        add(key, value, false);
    }

    public void add(String key, String value, boolean isWrite) {
        if (containsKey(key) == true) {
            setValue(key, value);
            return;
        }
        iniList.add(new CommonRegisterINI(key, value, isWrite));
    }

    public void add(CommonRegisterINI ini) {
        if (containsKey(ini.key) == true) {
            setValue(ini.key, ini.value);
            return;
        }
        iniList.add(ini);
    }

    public boolean setValue(String key, String value) {
        if (containsKey(key) == true) {
            getIni(key).value = value;
            return true;
        }
        return false;
    }

    public String getValue(String key) {
        if (containsKey(key) == true) {
            return new String(getIni(key).value);
        }
        return "";
    }

    public String[] getKeySet() {
        int len = iniList.size();
        String[] lst = new String[len];
        for (int i = 0; i < len; i++) {
            lst[i] = iniList.get(i).key;
        }
        return lst;
    }

    public void read(String path) {
        File file = new File(path);
        if (file.exists() == false) {
            return;
        }
        if (file.canRead() == false) {
            return;
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
                            setValue(key, value);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }

    public void write(String path) {
        File file = new File(path);
        if (file != null && file.getParentFile() != null && file.getParentFile().exists() == false) {
            return;
        }

        try {
            List<String> textContents = new LinkedList<String>();
            for (CommonRegisterINI ini : iniList) {
                if (ini.isWrite == true) {
                    textContents.add(ini.key + KER_SEPARATOR + ini.value);
                }
            }
            JmpUtil.writeTextFile(file, textContents);
        }
        catch (Exception e) {
        }
    }
}
