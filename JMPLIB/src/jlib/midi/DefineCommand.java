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

    static String getCommandMessage(int data) {
        switch (data) {
            case NOTE_OFF:
                return "NOTE_OFF";
            case NOTE_ON:
                return "NOTE_ON";
            case POLYPHONIC_KEY_PRESSURE:
                return "POLYPHONIC_KEY_PRESSURE";
            case CONTROL_CHANGE:
                return "CONTROL_CHANGE";
            case PROGRAM_CHANGE:
                return "PROGRAM_CHANGE";
            case CHANNEL_PRESSURE:
                return "CHANNEL_PRESSURE";
            case PITCH_BEND:
                return "PITCH_BEND";
            case SYSEX_BEGIN:
                return "SYSEX_BEGIN";
            case MIDI_TIME_CODE:
                return "MIDI_TIME_CODE";
            case SONG_POSITION:
                return "SONG_POSITION";
            case SONG_SELECT:
                return "SONG_SELECT";
            case TUNE_SELECT:
                return "TUNE_SELECT";
            case SYSEX_END:
                return "SYSEX_END";
            case MIDI_CLOCK:
                return "MIDI_CLOCK";
            case START:
                return "START";
            case CONTINUE:
                return "CONTINUE";
            case STOP:
                return "STOP";
            case ACTIVE_SENSING:
                return "ACTIVE_SENSING";
            case RESET:
                return "RESET";
            default:
                return "";
        }
    }
}
