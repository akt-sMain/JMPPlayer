package jlib.midi;

public class DefineCommand {
    public static final int NOTE_OFF = 128;
    public static final int NOTE_ON = 144;
    public static final int POLYPHONIC_KEY_PRESSURE = 160;
    public static final int CONTROL_CHANGE = 176;
    public static final int PROGRAM_CHANGE = 192;
    public static final int CHANNEL_PRESSURE = 208;
    public static final int PITCH_BEND = 224;
    public static final int SYSEX_BEGIN = 240;
    public static final int MIDI_TIME_CODE = 241;
    public static final int SONG_POSITION = 242;
    public static final int SONG_SELECT = 243;
    public static final int TUNE_SELECT = 246;
    public static final int SYSEX_END = 247;
    public static final int MIDI_CLOCK = 248;
    public static final int START = 250;
    public static final int CONTINUE = 251;
    public static final int STOP = 252;
    public static final int ACTIVE_SENSING = 254;
    public static final int RESET = 255;
    public static final int _END = 0;
    public static final String[] IDENTS = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "NOTE_OFF", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "NOTE_ON", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "POLYPHONIC_KEY_PRESSURE", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "CONTROL_CHANGE", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "PROGRAM_CHANGE", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "CHANNEL_PRESSURE", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "PITCH_BEND", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "SYSEX_BEGIN", "MIDI_TIME_CODE", "SONG_POSITION", "SONG_SELECT", "", "", "TUNE_SELECT",
            "SYSEX_END", "MIDI_CLOCK", "", "START", "CONTINUE", "STOP", "", "ACTIVE_SENSING", "RESET", };

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
