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

    static String getControlChangeMessage(int data) {
        switch (data) {
            case BANK_SELECT_MSB:
                return "BANK_SELECT_MSB";
            case MODULATION:
                return "MODULATION";
            case BLESS_CONTROL:
                return "BLESS_CONTROL";
            case FOOT_CONTROL:
                return "FOOT_CONTROL";
            case PORTAMENTO_TIME:
                return "PORTAMENTO_TIME";
            case DATA_ENTRY_MSB:
                return "DATA_ENTRY_MSB";
            case MAIN_VOLUME:
                return "MAIN_VOLUME";
            case BALANCE_CONTROL:
                return "BALANCE_CONTROL";
            case PAN_POT:
                return "PAN_POT";
            case EXPRESSION:
                return "EXPRESSION";
            case GENERAL_CONTROL1:
                return "GENERAL_CONTROL1";
            case GENERAL_CONTROL2:
                return "GENERAL_CONTROL2";
            case GENERAL_CONTROL3:
                return "GENERAL_CONTROL3";
            case GENERAL_CONTROL4:
                return "GENERAL_CONTROL4";
            case BANK_SELECT_LSB:
                return "BANK_SELECT_LSB";
            case MODULATION_LSB:
                return "MODULATION_LSB";
            case BLESS_CONTROL_LSB:
                return "BLESS_CONTROL_LSB";
            case FOOT_CONTROL_LSB:
                return "FOOT_CONTROL_LSB";
            case PORTAMENTO_TIME_LSB:
                return "PORTAMENTO_TIME_LSB";
            case DATA_ENTRY_LSB:
                return "DATA_ENTRY_LSB";
            case VOLUME:
                return "VOLUME";
            case BALANCE:
                return "BALANCE";
            case GENERAL_CONTROL1_LSB:
                return "GENERAL_CONTROL1_LSB";
            case GENERAL_CONTROL2_LSB:
                return "GENERAL_CONTROL2_LSB";
            case GENERAL_CONTROL3_LSB:
                return "GENERAL_CONTROL3_LSB";
            case GENERAL_CONTROL4_LSB:
                return "GENERAL_CONTROL4_LSB";
            case DAMPER_REDAL:
                return "DAMPER_REDAL";
            case PORTAMENTO:
                return "PORTAMENTO";
            case SOSTENUTO:
                return "SOSTENUTO";
            case SOFT_PEDAL:
                return "SOFT_PEDAL";
            case FREEZE:
                return "FREEZE";
            case MEMORY_PATCH_SELECT:
                return "MEMORY_PATCH_SELECT";
            case HARMONIC_CONTENT:
                return "HARMONIC_CONTENT";
            case RELEASE_TIME:
                return "RELEASE_TIME";
            case ATTACK_TIME:
                return "ATTACK_TIME";
            case BRIGHTNESS:
                return "BRIGHTNESS";
            case DECAY_TIME:
                return "DECAY_TIME";
            case VIBRATE_RATE:
                return "VIBRATE_RATE";
            case VIBRATE_DEPTH:
                return "VIBRATE_DEPTH";
            case VIBRATE_DELAY:
                return "VIBRATE_DELAY";
            case PORTAMENTO_CONTROL:
                return "PORTAMENTO_CONTROL";
            case REVERB:
                return "REVERB";
            case TREMOLO_DEPTH:
                return "TREMOLO_DEPTH";
            case CHORUS:
                return "CHORUS";
            case DELAY_VARIATION:
                return "DELAY_VARIATION";
            case PHASER_DEPTH:
                return "PHASER_DEPTH";
            case DATA_INCREMENT:
                return "DATA_INCREMENT";
            case DATA_DECREMENT:
                return "DATA_DECREMENT";
            case NRPN_LSB:
                return "NRPN_LSB";
            case NRPN_MSB:
                return "NRPN_MSB";
            case RPN_LSB:
                return "RPN_LSB";
            case RPN_MSB:
                return "RPN_MSB";
            case ALL_SOUND_OFF:
                return "ALL_SOUND_OFF";
            case RESET_ALL_CONTROLLER:
                return "RESET_ALL_CONTROLLER";
            case LOCAL_CONTROL:
                return "LOCAL_CONTROL";
            case ALL_NOTE_OFF:
                return "ALL_NOTE_OFF";
            case OMNI_OFF:
                return "OMNI_OFF";
            case OMNI_ON:
                return "OMNI_ON";
            case MONO_ON:
                return "MONO_ON";
            case POLY_ON:
                return "POLY_ON";
            default:
                return "";
        }
    }
}
