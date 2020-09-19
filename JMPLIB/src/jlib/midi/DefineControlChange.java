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
    public static final int DAMPER_PEDAL = 64;
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
    public static final String[] IDENTS = { //
            "BANK_SELECT_MSB", // 0
            "MODULATION", // 1
            "BLESS_CONTROL", // 2
            "", // 3
            "FOOT_CONTROL", // 4
            "PORTAMENTO_TIME", // 5
            "DATA_ENTRY_MSB", // 6
            "MAIN_VOLUME", // 7
            "BALANCE_CONTROL", // 8
            "", // 9
            "PAN_POT", // 10
            "EXPRESSION", // 11
            "", // 12
            "", // 13
            "", // 14
            "", // 15
            "GENERAL_CONTROL1", // 16
            "GENERAL_CONTROL2", // 17
            "GENERAL_CONTROL3", // 18
            "GENERAL_CONTROL4", // 19
            "", // 20
            "", // 21
            "", // 22
            "", // 23
            "", // 24
            "", // 25
            "", // 26
            "", // 27
            "", // 28
            "", // 29
            "", // 30
            "", // 31
            "BANK_SELECT_LSB", // 32
            "MODULATION_LSB", // 33
            "BLESS_CONTROL_LSB", // 34
            "", // 35
            "FOOT_CONTROL_LSB", // 36
            "PORTAMENTO_TIME_LSB", // 37
            "DATA_ENTRY_LSB", // 38
            "VOLUME", // 39
            "BALANCE", // 40
            "", // 41
            "", // 42
            "", // 43
            "", // 44
            "GENERAL_CONTROL1_LSB", // 45
            "GENERAL_CONTROL2_LSB", // 46
            "GENERAL_CONTROL3_LSB", // 47
            "GENERAL_CONTROL4_LSB", // 48
            "", // 49
            "", // 50
            "", // 51
            "", // 52
            "", // 53
            "", // 54
            "", // 55
            "", // 56
            "", // 57
            "", // 58
            "", // 59
            "", // 60
            "", // 61
            "", // 62
            "", // 63
            "DAMPER_PEDAL", // 64
            "PORTAMENTO", // 65
            "SOSTENUTO", // 66
            "SOFT_PEDAL", // 67
            "", // 68
            "FREEZE", // 69
            "MEMORY_PATCH_SELECT", // 70
            "HARMONIC_CONTENT", // 71
            "RELEASE_TIME", // 72
            "ATTACK_TIME", // 73
            "BRIGHTNESS", // 74
            "DECAY_TIME", // 75
            "VIBRATE_RATE", // 76
            "VIBRATE_DEPTH", // 77
            "VIBRATE_DELAY", // 78
            "", // 79
            "", // 80
            "", // 81
            "", // 82
            "", // 83
            "PORTAMENTO_CONTROL", // 84
            "", // 85
            "", // 86
            "", // 87
            "", // 88
            "", // 89
            "", // 90
            "REVERB", // 91
            "TREMOLO_DEPTH", // 92
            "CHORUS", // 93
            "DELAY_VARIATION", // 94
            "PHASER_DEPTH", // 95
            "DATA_INCREMENT", // 96
            "DATA_DECREMENT", // 97
            "NRPN_LSB", // 98
            "NRPN_MSB", // 99
            "RPN_LSB", // 100
            "RPN_MSB", // 101
            "", // 102
            "", // 103
            "", // 104
            "", // 105
            "", // 106
            "", // 107
            "", // 108
            "", // 109
            "", // 110
            "", // 111
            "", // 112
            "", // 113
            "", // 114
            "", // 115
            "", // 116
            "", // 117
            "", // 118
            "", // 119
            "ALL_SOUND_OFF", // 120
            "RESET_ALL_CONTROLLER", // 121
            "LOCAL_CONTROL", // 122
            "ALL_NOTE_OFF", // 123
            "OMNI_OFF", // 124
            "OMNI_ON", // 125
            "MONO_ON", // 126
            "POLY_ON",// 127
    };//

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
