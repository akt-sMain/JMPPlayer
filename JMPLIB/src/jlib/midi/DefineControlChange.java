package jlib.midi;

public class DefineControlChange {
    public static final int BANK_SELECT_MSB = 0;
    public static final int MODULATION = 1;
    public static final int BLESS_CONTROL = 2;
    public static final int FOOT_CONTROL = 4;
    public static final int PORTAMENTO_TIME = 5;
    public static final int DATA_ENTRY_MSB = 6;
    public static final int MAIN_VOLUME = 7;
    public static final int BALANCE_CONTROL = 8;
    public static final int PAN_POT = 10;
    public static final int EXPRESSION = 11;
    public static final int GENERAL_CONTROL1 = 16;
    public static final int GENERAL_CONTROL2 = 17;
    public static final int GENERAL_CONTROL3 = 18;
    public static final int GENERAL_CONTROL4 = 19;
    public static final int BANK_SELECT_LSB = 32;
    public static final int MODULATION_LSB = 33;
    public static final int BLESS_CONTROL_LSB = 34;
    public static final int FOOT_CONTROL_LSB = 36;
    public static final int PORTAMENTO_TIME_LSB = 37;
    public static final int DATA_ENTRY_LSB = 38;
    public static final int VOLUME = 39;
    public static final int BALANCE = 40;
    public static final int GENERAL_CONTROL1_LSB = 45;
    public static final int GENERAL_CONTROL2_LSB = 46;
    public static final int GENERAL_CONTROL3_LSB = 47;
    public static final int GENERAL_CONTROL4_LSB = 48;
    public static final int DAMPER_REDAL = 64;
    public static final int PORTAMENTO = 65;
    public static final int SOSTENUTO = 66;
    public static final int SOFT_PEDAL = 67;
    public static final int FREEZE = 69;
    public static final int MEMORY_PATCH_SELECT = 70;
    public static final int HARMONIC_CONTENT = 71;
    public static final int RELEASE_TIME = 72;
    public static final int ATTACK_TIME = 73;
    public static final int BRIGHTNESS = 74;
    public static final int DECAY_TIME = 75;
    public static final int VIBRATE_RATE = 76;
    public static final int VIBRATE_DEPTH = 77;
    public static final int VIBRATE_DELAY = 78;
    public static final int PORTAMENTO_CONTROL = 84;
    public static final int REVERB = 91;
    public static final int TREMOLO_DEPTH = 92;
    public static final int CHORUS = 93;
    public static final int DELAY_VARIATION = 94;
    public static final int PHASER_DEPTH = 95;
    public static final int DATA_INCREMENT = 96;
    public static final int DATA_DECREMENT = 97;
    public static final int NRPN_LSB = 98;
    public static final int NRPN_MSB = 99;
    public static final int RPN_LSB = 100;
    public static final int RPN_MSB = 101;
    public static final int ALL_SOUND_OFF = 120;
    public static final int RESET_ALL_CONTROLLER = 121;
    public static final int LOCAL_CONTROL = 122;
    public static final int ALL_NOTE_OFF = 123;
    public static final int OMNI_OFF = 124;
    public static final int OMNI_ON = 125;
    public static final int MONO_ON = 126;
    public static final int POLY_ON = 127;
    public static final int _END = 0;
    public static final String[] IDENTS = { "BANK_SELECT_MSB", "MODULATION", "BLESS_CONTROL", "", "FOOT_CONTROL",
            "PORTAMENTO_TIME", "DATA_ENTRY_MSB", "MAIN_VOLUME", "BALANCE_CONTROL", "", "PAN_POT", "EXPRESSION", "", "",
            "", "", "GENERAL_CONTROL1", "GENERAL_CONTROL2", "GENERAL_CONTROL3", "GENERAL_CONTROL4", "", "", "", "", "",
            "", "", "", "", "", "", "", "BANK_SELECT_LSB", "MODULATION_LSB", "BLESS_CONTROL_LSB", "",
            "FOOT_CONTROL_LSB", "PORTAMENTO_TIME_LSB", "DATA_ENTRY_LSB", "VOLUME", "BALANCE", "", "", "", "",
            "GENERAL_CONTROL1_LSB", "GENERAL_CONTROL2_LSB", "GENERAL_CONTROL3_LSB", "GENERAL_CONTROL4_LSB", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "DAMPER_REDAL", "PORTAMENTO", "SOSTENUTO", "SOFT_PEDAL", "",
            "FREEZE", "MEMORY_PATCH_SELECT", "HARMONIC_CONTENT", "RELEASE_TIME", "ATTACK_TIME", "BRIGHTNESS",
            "DECAY_TIME", "VIBRATE_RATE", "VIBRATE_DEPTH", "VIBRATE_DELAY", "", "", "", "", "", "PORTAMENTO_CONTROL",
            "", "", "", "", "", "", "REVERB", "TREMOLO_DEPTH", "CHORUS", "DELAY_VARIATION", "PHASER_DEPTH",
            "DATA_INCREMENT", "DATA_DECREMENT", "NRPN_LSB", "NRPN_MSB", "RPN_LSB", "RPN_MSB", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "ALL_SOUND_OFF", "RESET_ALL_CONTROLLER", "LOCAL_CONTROL",
            "ALL_NOTE_OFF", "OMNI_OFF", "OMNI_ON", "MONO_ON", "POLY_ON", };

    public static String dataToIdent(int data) {
        if (0 > data || data >= IDENTS.length)
            return "";
        return IDENTS[data];
    }

    public static int identToData(String ident) {
        for (int i = 0; i < IDENTS.length; i++)
            if (IDENTS[i].equalsIgnoreCase(ident) == true)
                return i;
        return -1;
    }

}
