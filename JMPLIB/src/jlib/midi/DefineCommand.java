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
    public static final String[] IDENTS = { //
            "", // 0
            "", // 1
            "", // 2
            "", // 3
            "", // 4
            "", // 5
            "", // 6
            "", // 7
            "", // 8
            "", // 9
            "", // 10
            "", // 11
            "", // 12
            "", // 13
            "", // 14
            "", // 15
            "", // 16
            "", // 17
            "", // 18
            "", // 19
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
            "", // 32
            "", // 33
            "", // 34
            "", // 35
            "", // 36
            "", // 37
            "", // 38
            "", // 39
            "", // 40
            "", // 41
            "", // 42
            "", // 43
            "", // 44
            "", // 45
            "", // 46
            "", // 47
            "", // 48
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
            "", // 64
            "", // 65
            "", // 66
            "", // 67
            "", // 68
            "", // 69
            "", // 70
            "", // 71
            "", // 72
            "", // 73
            "", // 74
            "", // 75
            "", // 76
            "", // 77
            "", // 78
            "", // 79
            "", // 80
            "", // 81
            "", // 82
            "", // 83
            "", // 84
            "", // 85
            "", // 86
            "", // 87
            "", // 88
            "", // 89
            "", // 90
            "", // 91
            "", // 92
            "", // 93
            "", // 94
            "", // 95
            "", // 96
            "", // 97
            "", // 98
            "", // 99
            "", // 100
            "", // 101
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
            "", // 120
            "", // 121
            "", // 122
            "", // 123
            "", // 124
            "", // 125
            "", // 126
            "", // 127
            "NOTE_OFF", // 128
            "", // 129
            "", // 130
            "", // 131
            "", // 132
            "", // 133
            "", // 134
            "", // 135
            "", // 136
            "", // 137
            "", // 138
            "", // 139
            "", // 140
            "", // 141
            "", // 142
            "", // 143
            "NOTE_ON", // 144
            "", // 145
            "", // 146
            "", // 147
            "", // 148
            "", // 149
            "", // 150
            "", // 151
            "", // 152
            "", // 153
            "", // 154
            "", // 155
            "", // 156
            "", // 157
            "", // 158
            "", // 159
            "POLYPHONIC_KEY_PRESSURE", // 160
            "", // 161
            "", // 162
            "", // 163
            "", // 164
            "", // 165
            "", // 166
            "", // 167
            "", // 168
            "", // 169
            "", // 170
            "", // 171
            "", // 172
            "", // 173
            "", // 174
            "", // 175
            "CONTROL_CHANGE", // 176
            "", // 177
            "", // 178
            "", // 179
            "", // 180
            "", // 181
            "", // 182
            "", // 183
            "", // 184
            "", // 185
            "", // 186
            "", // 187
            "", // 188
            "", // 189
            "", // 190
            "", // 191
            "PROGRAM_CHANGE", // 192
            "", // 193
            "", // 194
            "", // 195
            "", // 196
            "", // 197
            "", // 198
            "", // 199
            "", // 200
            "", // 201
            "", // 202
            "", // 203
            "", // 204
            "", // 205
            "", // 206
            "", // 207
            "CHANNEL_PRESSURE", // 208
            "", // 209
            "", // 210
            "", // 211
            "", // 212
            "", // 213
            "", // 214
            "", // 215
            "", // 216
            "", // 217
            "", // 218
            "", // 219
            "", // 220
            "", // 221
            "", // 222
            "", // 223
            "PITCH_BEND", // 224
            "", // 225
            "", // 226
            "", // 227
            "", // 228
            "", // 229
            "", // 230
            "", // 231
            "", // 232
            "", // 233
            "", // 234
            "", // 235
            "", // 236
            "", // 237
            "", // 238
            "", // 239
            "SYSEX_BEGIN", // 240
            "MIDI_TIME_CODE", // 241
            "SONG_POSITION", // 242
            "SONG_SELECT", // 243
            "", // 244
            "", // 245
            "TUNE_SELECT", // 246
            "SYSEX_END", // 247
            "MIDI_CLOCK", // 248
            "", // 249
            "START", // 250
            "CONTINUE", // 251
            "STOP", // 252
            "", // 253
            "ACTIVE_SENSING", // 254
            "RESET",// 255
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
