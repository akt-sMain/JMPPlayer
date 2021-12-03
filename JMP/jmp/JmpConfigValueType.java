package jmp;

import jmp.core.DataManager.TypeOfKey;

public class JmpConfigValueType {

    public String value = "";
    public TypeOfKey type = TypeOfKey.STRING;

    public JmpConfigValueType(String value, TypeOfKey type) {
        this.value = value;
        this.type = type;
    }
    public JmpConfigValueType(String value) {
        this.value = value;
        this.type = TypeOfKey.STRING;
    }
}
