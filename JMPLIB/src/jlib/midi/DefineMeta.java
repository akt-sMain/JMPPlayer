﻿package jlib.midi;

public class DefineMeta {
    static final int SEQUENCE_NUMBER = 0;
    static final int TEXT = 1;
    static final int COPYRIGHT_NOTICE = 2;
    static final int TRACK_NAME = 3;
    static final int INSTRUMENT_NAME = 4;
    static final int LYRICS = 5;
    static final int MARKER = 6;
    static final int CUE_POINT = 7;
    static final int PROGRAM_NAME = 8;
    static final int DEVICE_NAME = 9;
    static final int CHANNEL_PREFIX = 32;
    static final int PORT = 33;
    static final int END_OF_TRACK = 47;
    static final int SET_TEMPO = 81;
    static final int SMPTE_OFFSET = 84;
    static final int BEAT = 88;
    static final int KEY_SEGNATURE = 89;
    static final int SEQUENCER_SPECFIC = 177;
    static final int _END = 0;
    static final String[] IDENTS = {//
            "SEQUENCE_NUMBER",//0
            "TEXT",//1
            "COPYRIGHT_NOTICE",//2
            "TRACK_NAME",//3
            "INSTRUMENT_NAME",//4
            "LYRICS",//5
            "MARKER",//6
            "CUE_POINT",//7
            "PROGRAM_NAME",//8
            "DEVICE_NAME",//9
            "",//10
            "",//11
            "",//12
            "",//13
            "",//14
            "",//15
            "",//16
            "",//17
            "",//18
            "",//19
            "",//20
            "",//21
            "",//22
            "",//23
            "",//24
            "",//25
            "",//26
            "",//27
            "",//28
            "",//29
            "",//30
            "",//31
            "CHANNEL_PREFIX",//32
            "PORT",//33
            "",//34
            "",//35
            "",//36
            "",//37
            "",//38
            "",//39
            "",//40
            "",//41
            "",//42
            "",//43
            "",//44
            "",//45
            "",//46
            "END_OF_TRACK",//47
            "",//48
            "",//49
            "",//50
            "",//51
            "",//52
            "",//53
            "",//54
            "",//55
            "",//56
            "",//57
            "",//58
            "",//59
            "",//60
            "",//61
            "",//62
            "",//63
            "",//64
            "",//65
            "",//66
            "",//67
            "",//68
            "",//69
            "",//70
            "",//71
            "",//72
            "",//73
            "",//74
            "",//75
            "",//76
            "",//77
            "",//78
            "",//79
            "",//80
            "SET_TEMPO",//81
            "",//82
            "",//83
            "SMPTE_OFFSET",//84
            "",//85
            "",//86
            "",//87
            "BEAT",//88
            "KEY_SEGNATURE",//89
            "",//90
            "",//91
            "",//92
            "",//93
            "",//94
            "",//95
            "",//96
            "",//97
            "",//98
            "",//99
            "",//100
            "",//101
            "",//102
            "",//103
            "",//104
            "",//105
            "",//106
            "",//107
            "",//108
            "",//109
            "",//110
            "",//111
            "",//112
            "",//113
            "",//114
            "",//115
            "",//116
            "",//117
            "",//118
            "",//119
            "",//120
            "",//121
            "",//122
            "",//123
            "",//124
            "",//125
            "",//126
            "",//127
            "",//128
            "",//129
            "",//130
            "",//131
            "",//132
            "",//133
            "",//134
            "",//135
            "",//136
            "",//137
            "",//138
            "",//139
            "",//140
            "",//141
            "",//142
            "",//143
            "",//144
            "",//145
            "",//146
            "",//147
            "",//148
            "",//149
            "",//150
            "",//151
            "",//152
            "",//153
            "",//154
            "",//155
            "",//156
            "",//157
            "",//158
            "",//159
            "",//160
            "",//161
            "",//162
            "",//163
            "",//164
            "",//165
            "",//166
            "",//167
            "",//168
            "",//169
            "",//170
            "",//171
            "",//172
            "",//173
            "",//174
            "",//175
            "",//176
            "SEQUENCER_SPECFIC",//177
            "",//178
            "",//179
            "",//180
            "",//181
            "",//182
            "",//183
            "",//184
            "",//185
            "",//186
            "",//187
            "",//188
            "",//189
            "",//190
            "",//191
            "",//192
            "",//193
            "",//194
            "",//195
            "",//196
            "",//197
            "",//198
            "",//199
            "",//200
            "",//201
            "",//202
            "",//203
            "",//204
            "",//205
            "",//206
            "",//207
            "",//208
            "",//209
            "",//210
            "",//211
            "",//212
            "",//213
            "",//214
            "",//215
            "",//216
            "",//217
            "",//218
            "",//219
            "",//220
            "",//221
            "",//222
            "",//223
            "",//224
            "",//225
            "",//226
            "",//227
            "",//228
            "",//229
            "",//230
            "",//231
            "",//232
            "",//233
            "",//234
            "",//235
            "",//236
            "",//237
            "",//238
            "",//239
            "",//240
            "",//241
            "",//242
            "",//243
            "",//244
            "",//245
            "",//246
            "",//247
            "",//248
            "",//249
            "",//250
            "",//251
            "",//252
            "",//253
            "",//254
            "",//255
    };//

    static String dataToIdent(int data) {
        if (0 > data || data >= IDENTS.length)
            return "";
        return IDENTS[data];
    }

    static int identToData(String ident) {
        for (int i=0; i<IDENTS.length; i++)
            if (IDENTS[i].equalsIgnoreCase(ident) == true)
                return i;
        return -1;
    }

}
