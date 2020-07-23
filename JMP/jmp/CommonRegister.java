package jmp;

import java.util.ArrayList;
import java.util.List;

public class CommonRegister {
    public class CommonRegisterINI {
        public String key, value;

        public CommonRegisterINI(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private List<CommonRegisterINI> iniList = null;

    public CommonRegister() {
        iniList = new ArrayList<CommonRegisterINI>();
        iniList.clear();
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
        if (containsKey(key) == true) {
            setValue(key, value);
            return;
        }
        iniList.add(new CommonRegisterINI(key, value));
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
            return getIni(key).value;
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
}
