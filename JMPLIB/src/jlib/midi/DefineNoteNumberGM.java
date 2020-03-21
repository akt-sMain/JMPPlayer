package jlib.midi;

public class DefineNoteNumberGM {
    public static final int C_MI1 = 0;
    public static final int CS_MI1 = 1;
    public static final int D_MI1 = 2;
    public static final int DS_MI1 = 3;
    public static final int E_MI1 = 4;
    public static final int F_MI1 = 5;
    public static final int FS_MI1 = 6;
    public static final int G_MI1 = 7;
    public static final int GS_MI1 = 8;
    public static final int A_MI1 = 9;
    public static final int AS_MI1 = 10;
    public static final int B_MI1 = 11;
    public static final int C_0 = 12;
    public static final int CS_0 = 13;
    public static final int D_0 = 14;
    public static final int DS_0 = 15;
    public static final int E_0 = 16;
    public static final int F_0 = 17;
    public static final int FS_0 = 18;
    public static final int G_0 = 19;
    public static final int GS_0 = 20;
    public static final int A_0 = 21;
    public static final int AS_0 = 22;
    public static final int B_0 = 23;
    public static final int C_1 = 24;
    public static final int CS_1 = 25;
    public static final int D_1 = 26;
    public static final int DS_1 = 27;
    public static final int E_1 = 28;
    public static final int F_1 = 29;
    public static final int FS_1 = 30;
    public static final int G_1 = 31;
    public static final int GS_1 = 32;
    public static final int A_1 = 33;
    public static final int AS_1 = 34;
    public static final int B_1 = 35;
    public static final int C_2 = 36;
    public static final int CS_2 = 37;
    public static final int D_2 = 38;
    public static final int DS_2 = 39;
    public static final int E_2 = 40;
    public static final int F_2 = 41;
    public static final int FS_2 = 42;
    public static final int G_2 = 43;
    public static final int GS_2 = 44;
    public static final int A_2 = 45;
    public static final int AS_2 = 46;
    public static final int B_2 = 47;
    public static final int C_3 = 48;
    public static final int CS_3 = 49;
    public static final int D_3 = 50;
    public static final int DS_3 = 51;
    public static final int E_3 = 52;
    public static final int F_3 = 53;
    public static final int FS_3 = 54;
    public static final int G_3 = 55;
    public static final int GS_3 = 56;
    public static final int A_3 = 57;
    public static final int AS_3 = 58;
    public static final int B_3 = 59;
    public static final int C_4 = 60;
    public static final int CS_4 = 61;
    public static final int D_4 = 62;
    public static final int DS_4 = 63;
    public static final int E_4 = 64;
    public static final int F_4 = 65;
    public static final int FS_4 = 66;
    public static final int G_4 = 67;
    public static final int GS_4 = 68;
    public static final int A_4 = 69;
    public static final int AS_4 = 70;
    public static final int B_4 = 71;
    public static final int C_5 = 72;
    public static final int CS_5 = 73;
    public static final int D_5 = 74;
    public static final int DS_5 = 75;
    public static final int E_5 = 76;
    public static final int F_5 = 77;
    public static final int FS_5 = 78;
    public static final int G_5 = 79;
    public static final int GS_5 = 80;
    public static final int A_5 = 81;
    public static final int AS_5 = 82;
    public static final int B_5 = 83;
    public static final int C_6 = 84;
    public static final int CS_6 = 85;
    public static final int D_6 = 86;
    public static final int DS_6 = 87;
    public static final int E_6 = 88;
    public static final int F_6 = 89;
    public static final int FS_6 = 90;
    public static final int G_6 = 91;
    public static final int GS_6 = 92;
    public static final int A_6 = 93;
    public static final int AS_6 = 94;
    public static final int B_6 = 95;
    public static final int C_7 = 96;
    public static final int CS_7 = 97;
    public static final int D_7 = 98;
    public static final int DS_7 = 99;
    public static final int E_7 = 100;
    public static final int F_7 = 101;
    public static final int FS_7 = 102;
    public static final int G_7 = 103;
    public static final int GS_7 = 104;
    public static final int A_7 = 105;
    public static final int AS_7 = 106;
    public static final int B_7 = 107;
    public static final int C_8 = 108;
    public static final int CS_8 = 109;
    public static final int D_8 = 110;
    public static final int DS_8 = 111;
    public static final int E_8 = 112;
    public static final int F_8 = 113;
    public static final int FS_8 = 114;
    public static final int G_8 = 115;
    public static final int GS_8 = 116;
    public static final int A_8 = 117;
    public static final int AS_8 = 118;
    public static final int B_8 = 119;
    public static final int C_9 = 120;
    public static final int CS_9 = 121;
    public static final int D_9 = 122;
    public static final int DS_9 = 123;
    public static final int E_9 = 124;
    public static final int F_9 = 125;
    public static final int FS_9 = 126;
    public static final int G_9 = 127;

    static String getNoteMessage(int data) {
        switch (data) {
            case C_MI1:
                return "C-1";
            case CS_MI1:
                return "C#-1";
            case D_MI1:
                return "D-1";
            case DS_MI1:
                return "D#-1";
            case E_MI1:
                return "E-1";
            case F_MI1:
                return "F-1";
            case FS_MI1:
                return "F#-1";
            case G_MI1:
                return "G-1";
            case GS_MI1:
                return "G#-1";
            case A_MI1:
                return "A-1";
            case AS_MI1:
                return "A#-1";
            case B_MI1:
                return "B-1";
            case C_0:
                return "C0";
            case CS_0:
                return "C#0";
            case D_0:
                return "D0";
            case DS_0:
                return "D#0";
            case E_0:
                return "E0";
            case F_0:
                return "F0";
            case FS_0:
                return "F#0";
            case G_0:
                return "G0";
            case GS_0:
                return "G#0";
            case A_0:
                return "A0";
            case AS_0:
                return "A#0";
            case B_0:
                return "B0";
            case C_1:
                return "C1";
            case CS_1:
                return "C#1";
            case D_1:
                return "D1";
            case DS_1:
                return "D#1";
            case E_1:
                return "E1";
            case F_1:
                return "F1";
            case FS_1:
                return "F#1";
            case G_1:
                return "G1";
            case GS_1:
                return "G#1";
            case A_1:
                return "A1";
            case AS_1:
                return "A#1";
            case B_1:
                return "B1";
            case C_2:
                return "C2";
            case CS_2:
                return "C#2";
            case D_2:
                return "D2";
            case DS_2:
                return "D#2";
            case E_2:
                return "E2";
            case F_2:
                return "F2";
            case FS_2:
                return "F#2";
            case G_2:
                return "G2";
            case GS_2:
                return "G#2";
            case A_2:
                return "A2";
            case AS_2:
                return "A#2";
            case B_2:
                return "B2";
            case C_3:
                return "C3";
            case CS_3:
                return "C#3";
            case D_3:
                return "D3";
            case DS_3:
                return "D#3";
            case E_3:
                return "E3";
            case F_3:
                return "F3";
            case FS_3:
                return "F#3";
            case G_3:
                return "G3";
            case GS_3:
                return "G#3";
            case A_3:
                return "A3";
            case AS_3:
                return "A#3";
            case B_3:
                return "B3";
            case C_4:
                return "C4";
            case CS_4:
                return "C#4";
            case D_4:
                return "D4";
            case DS_4:
                return "D#4";
            case E_4:
                return "E4";
            case F_4:
                return "F4";
            case FS_4:
                return "F#4";
            case G_4:
                return "G4";
            case GS_4:
                return "G#4";
            case A_4:
                return "A4";
            case AS_4:
                return "A#4";
            case B_4:
                return "B4";
            case C_5:
                return "C5";
            case CS_5:
                return "C#5";
            case D_5:
                return "D5";
            case DS_5:
                return "D#5";
            case E_5:
                return "E5";
            case F_5:
                return "F5";
            case FS_5:
                return "F#5";
            case G_5:
                return "G5";
            case GS_5:
                return "G#5";
            case A_5:
                return "A5";
            case AS_5:
                return "A#5";
            case B_5:
                return "B5";
            case C_6:
                return "C6";
            case CS_6:
                return "C#6";
            case D_6:
                return "D6";
            case DS_6:
                return "D#6";
            case E_6:
                return "E6";
            case F_6:
                return "F6";
            case FS_6:
                return "F#6";
            case G_6:
                return "G6";
            case GS_6:
                return "G#6";
            case A_6:
                return "A6";
            case AS_6:
                return "A#6";
            case B_6:
                return "B6";
            case C_7:
                return "C7";
            case CS_7:
                return "C#7";
            case D_7:
                return "D7";
            case DS_7:
                return "D#7";
            case E_7:
                return "E7";
            case F_7:
                return "F7";
            case FS_7:
                return "F#7";
            case G_7:
                return "G7";
            case GS_7:
                return "G#7";
            case A_7:
                return "A7";
            case AS_7:
                return "A#7";
            case B_7:
                return "B7";
            case C_8:
                return "C8";
            case CS_8:
                return "C#8";
            case D_8:
                return "D8";
            case DS_8:
                return "D#8";
            case E_8:
                return "E8";
            case F_8:
                return "F8";
            case FS_8:
                return "F#8";
            case G_8:
                return "G8";
            case GS_8:
                return "G#8";
            case A_8:
                return "A8";
            case AS_8:
                return "A#8";
            case B_8:
                return "B8";
            case C_9:
                return "C9";
            case CS_9:
                return "C#9";
            case D_9:
                return "D9";
            case DS_9:
                return "D#9";
            case E_9:
                return "E9";
            case F_9:
                return "F9";
            case FS_9:
                return "F#9";
            case G_9:
                return "G9";
            default:
                return "";
        }
    }
}
