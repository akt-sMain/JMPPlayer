package jlib.midi;

import java.util.ArrayList;
import java.util.List;

/**
 * MIDIメッセージバイト定義
 *
 * @author akt
 *
 */
public class MidiByte {

    private static List<String> cloneIdentList(String[] idents, boolean deleteBlank) {
        List<String> ret = new ArrayList<String>();
        for (String ident : idents) {
            if (deleteBlank == true && ident.isEmpty() == true) {
                continue;
            }
            ret.add(ident);
        }
        return ret;
    }

    public static List<String> cloneCommandIdentList(boolean deleteBlank) {
        return cloneIdentList(DefineCommand.IDENTS, deleteBlank);
    }

    public static List<String> cloneControlChangeIdentList(boolean deleteBlank) {
        return cloneIdentList(DefineControlChange.IDENTS, deleteBlank);
    }

    public static List<String> cloneNoteNumberIdentList(boolean deleteBlank) {
        return cloneIdentList(DefineNoteNumberGM.IDENTS, deleteBlank);
    }

    /**
     * ステータスバイトがチャンネルメッセージか
     *
     * @param statusByte
     *            ステータスバイト
     * @return
     */
    public static boolean isChannelMessage(int statusByte) {
        int command = statusByte & 0xf0;
        return command < 0xf0;
    }

    /**
     * ステータスバイトがシステムメッセージか
     *
     * @param statusByte
     *            ステータスバイト
     * @return
     */
    public static boolean isSystemMessage(int statusByte) {
        int command = statusByte & 0xf0;
        return command >= 0xf0;
    }

    /**
     * LSB(7bit)とMSB(7bit)を結合した14bitの値を取得する
     *
     * @param lsb
     *            LSB(7bit)
     * @param msb
     *            MSB(7bit)
     * @return 14bit value
     */
    public static int mergeLsbMsbValue(int lsb, int msb) {
        int ret = 0x00;
        int bit = 7;
        // 先にMSBをシフトする
        ret = msb << bit;

        int mask = (8 - bit) >> 0xff;
        int value = (ret | (lsb & mask));
        return value;
    }

    /**
     * ステータスバイトからコマンド文字列に変換する
     *
     * @param statusByte
     *            ステータスバイト
     * @return コマンド文字列
     */
    public static String convertByteToChannelCommandString(int statusByte) {
        String res = "--";
        int command = (statusByte & 0xf0);
        if (command < 0xf0) {
            // チャンネルメッセージ
            res = DefineCommand.dataToIdent(command);
        }
        else {
            // システムメッセージ
            res = DefineCommand.dataToIdent(statusByte);
        }
        return res;
    }

    /**
     * コマンド文字列からステータスバイトに変換する
     *
     * @param ident
     *            コマンド文字列
     * @return ステータスバイト
     */
    public static int convertChannelCommandStringToByte(String ident) {
        return DefineCommand.identToData(ident);
    }

    /**
     * 第1データバイトからコントロールチェンジ文字列を取得する
     *
     * @param data1Byte
     *            第1データバイト
     * @return コントロールチェンジ文字列
     */
    public static String convertByteToControlChangeString(int data1Byte) {
        return DefineControlChange.dataToIdent(data1Byte);
    }

    public static int convertControlChangeStringToByte(String ident) {
        return DefineControlChange.identToData(ident);
    }

    /**
     * 第1データバイトからノート情報文字列を取得する
     *
     * @param data1Byte
     *            第1データバイト
     * @return ノート情報文字列
     */
    public static String convertByteToNoteString(int data1Byte) {
        return DefineNoteNumberGM.dataToIdent(data1Byte);
    }

    public static int convertNoteStringToByte(String ident) {
        return DefineNoteNumberGM.identToData(ident);
    }

    /**
     * ステータスバイト定義
     *
     * @author akt
     *
     */
    public class Status {

        /**
         * チャンネルメッセージバイト定義
         *
         * @author akt
         *
         */
        public class Channel {

            /**
             * チャンネルボイスメッセージバイト定義
             *
             * @author akt
             *
             */
            public class ChannelVoice {
                public class Fst {

                    /**
                     * ノートオフ
                     */
                    public static final int NOTE_OFF = DefineCommand.NOTE_OFF;

                    /**
                     * ノートオン
                     */
                    public static final int NOTE_ON = DefineCommand.NOTE_ON;

                    /**
                     * ポリフォニックキープレッシャー <br>
                     * キー毎のアフタータッチ
                     */
                    public static final int POLYPHONIC_KEY_PRESSURE = DefineCommand.POLYPHONIC_KEY_PRESSURE;

                    /**
                     * コントロールチェンジ
                     */
                    public static final int CONTROL_CHANGE = DefineCommand.CONTROL_CHANGE;

                    /**
                     * プログラムチェンジ
                     */
                    public static final int PROGRAM_CHANGE = DefineCommand.PROGRAM_CHANGE;

                    /**
                     * チャンネルプレッシャー <br>
                     * チャンネル全体のアフタータッチ
                     */
                    public static final int CHANNEL_PRESSURE = DefineCommand.CHANNEL_PRESSURE;

                    /**
                     * ピッチベンド <br>
                     * MSB,LSBを連結し14ビットの値(-8192~+8191)として扱う
                     */
                    public static final int PITCH_BEND = DefineCommand.PITCH_BEND;
                }
            }

            /**
             * チャンネルモードメッセージバイト定義
             *
             * @author akt
             *
             */
            public class ChannelMode {
                public class Fst {
                    /**
                     * コントロールチェンジ
                     */
                    public static final int CONTROL_CHANGE = MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE;
                }

                public class Snd {
                    /**
                     * オールサウンドオフ <br>
                     * 発音中の音を残響やホールドされているものを含めて即時に停止する
                     */
                    public static final int ALL_SOUND_OFF = DefineControlChange.ALL_SOUND_OFF;

                    /**
                     * リセットオールコントローラー
                     */
                    public static final int RESET_ALL_CONTROLLER = DefineControlChange.RESET_ALL_CONTROLLER;

                    /**
                     * ローカルコントロール <br>
                     * オフにするとそのチャンネルはMIDI経由でのみ制御される
                     */
                    public static final int LOCAL_CONTROL = DefineControlChange.LOCAL_CONTROL;

                    /**
                     * オールノートオフ <br>
                     * 発音中の音をノートオフ状態にする(オールサウンドオフよりも弱い)
                     */
                    public static final int ALL_NOTE_OFF = DefineControlChange.ALL_NOTE_OFF;

                    /**
                     * オムニオフ
                     */
                    public static final int OMNI_OFF = DefineControlChange.OMNI_OFF;

                    /**
                     * オムニオン
                     */
                    public static final int OMNI_ON = DefineControlChange.OMNI_ON;

                    /**
                     * モノモード
                     */
                    public static final int MONO_MODE = DefineControlChange.MONO_ON;

                    /**
                     * ポリモード
                     */
                    public static final int POLY_MODE = DefineControlChange.POLY_ON;
                }

            }
        }

        /**
         * システムバイトメッセージバイト定義
         *
         * @author akt
         *
         */
        public class System {
            /**
             * システムコモンメッセージバイト定義
             *
             * @author akt
             *
             */
            public class SystemCommon {
                public class Fst {
                    /**
                     * システムエクスクルーシブ開始<br>
                     * Fst: マニュファクチャID 1バイトまたは3バイト
                     */
                    public static final int SYSEX_BEGIN = DefineCommand.SYSEX_BEGIN;

                    /**
                     * MIDIタイムコード<br>
                     * Fst: td hex (t=0～7:フレームL/H,秒L/H,分L/H,時L/H)
                     */
                    public static final int MIDI_TIME_CODE = DefineCommand.MIDI_TIME_CODE;

                    /**
                     * ソングポジション<br>
                     * 曲先頭からのMIDIビートのカウンター(1MIDIビートは6MIDIクロック)<br>
                     * Fst: LSB(7bit) Snd: MSB(7bit)
                     */
                    public static final int SONG_POSITION = DefineCommand.SONG_POSITION;

                    /**
                     * ソングセレクト<br>
                     * 複数のソングを扱う場合に使用<br>
                     * Fst: ソング番号
                     */
                    public static final int SONG_SELECT = DefineCommand.SONG_SELECT;

                    /**
                     * チューンリクエスト<br>
                     * アナログシンセの自動チューニング機能を動作させる
                     */
                    public static final int TUNE_SELECT = DefineCommand.TUNE_SELECT;

                    /**
                     * システムエクスクルーシブ終了
                     */
                    public static final int SYSEX_END = DefineCommand.SYSEX_END;
                }

            }

            /**
             * システムリアルタイムメッセージバイト定義
             *
             * @author akt
             *
             */
            public class SystemRealTime {
                public class Fst {
                    /**
                     * MIDIクロック<br>
                     * 四分音符あたり24回送信
                     */
                    public static final int MIDI_CLOCK = DefineCommand.MIDI_CLOCK;

                    /**
                     * 現在のシーケンスを開始
                     */
                    public static final int START = DefineCommand.START;

                    /**
                     * 前回停止した位置から再生する
                     */
                    public static final int CONTINUE = DefineCommand.CONTINUE;

                    /**
                     * 再生中のシーケンスを停止
                     */
                    public static final int STOP = DefineCommand.STOP;

                    /**
                     * アクティブセンシング<br>
                     * 送出側は300mSec以内に1度送出。<br>
                     * 受信側は受信していたアクティブセンシングが途絶えた場合、音を停止
                     */
                    public static final int ACTIVE_SENSING = DefineCommand.ACTIVE_SENSING;

                    /**
                     * 電源投入時の状態にリセットする<br>
                     * 使用が推奨されるものではなく、<br>
                     * 送信側機器は電源投入後、自動的に送出しない
                     */
                    public static final int RESET = DefineCommand.RESET;
                }
            }
        }/* System */
    }/* Status */
}
