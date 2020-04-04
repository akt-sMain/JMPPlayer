package jlib.midi;

public class DefineMeta {
    public static final int SEQUENCE_NUMBER = 0;
    public static final int TEXT = 1;
    public static final int COPYRIGHT_NOTICE = 2;
    public static final int TRACK_NAME = 3;
    public static final int INSTRUMENT_NAME = 4;
    public static final int LYRICS = 5;
    public static final int MARKER = 6;
    public static final int CUE_POINT = 7;
    public static final int PROGRAM_NAME = 8;
    public static final int DEVICE_NAME = 9;
    public static final int CHANNEL_PREFIX = 32;
    public static final int PORT = 33;
    public static final int END_OF_TRACK = 47;
    public static final int SET_TEMPO = 81;
    public static final int SMPTE_OFFSET = 84;
    public static final int BEAT = 88;
    public static final int KEY_SEGNATURE = 89;
    public static final int SEQUENCER_SPECFIC = 177;
    public static final int _END = 0;
    public static final String[] IDENTS = { "SEQUENCE_NUMBER", "TEXT", "COPYRIGHT_NOTICE", "TRACK_NAME", "INSTRUMENT_NAME", "LYRICS", "MARKER", "CUE_POINT",
            "PROGRAM_NAME", "DEVICE_NAME", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "CHANNEL_PREFIX", "PORT", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "END_OF_TRACK", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "SET_TEMPO", "", "", "SMPTE_OFFSET", "", "", "", "BEAT", "KEY_SEGNATURE", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "SEQUENCER_SPECFIC", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", };

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
