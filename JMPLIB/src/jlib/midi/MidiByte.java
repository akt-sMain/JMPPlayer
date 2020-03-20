package jlib.midi;

/**
 * MIDIメッセージバイト定義
 *
 * @author akt
 *
 */
public class MidiByte {
    /**
     * LSB(7bit)とMSB(7bit)を結合した14bitの値を取得する
     *
     * @param lsb LSB(7bit)
     * @param msb MSB(7bit)
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
                    public static final int NOTE_OFF = CommandDefine.NOTE_OFF;

                    /**
                     * ノートオン
                     */
                    public static final int NOTE_ON = CommandDefine.NOTE_ON;

                    /**
                     * ポリフォニックキープレッシャー <br>
                     * キー毎のアフタータッチ
                     */
                    public static final int POLYPHONIC_KEY_PRESSURE = CommandDefine.POLYPHONIC_KEY_PRESSURE;

                    /**
                     * コントロールチェンジ
                     */
                    public static final int CONTROL_CHANGE = CommandDefine.CONTROL_CHANGE;

                    /**
                     * プログラムチェンジ
                     */
                    public static final int PROGRAM_CHANGE = CommandDefine.PROGRAM_CHANGE;

                    /**
                     * チャンネルプレッシャー <br>
                     * チャンネル全体のアフタータッチ
                     */
                    public static final int CHANNEL_PRESSURE = CommandDefine.CHANNEL_PRESSURE;

                    /**
                     * ピッチベンド <br>
                     * MSB,LSBを連結し14ビットの値(-8192~+8191)として扱う
                     */
                    public static final int PITCH_BEND = CommandDefine.PITCH_BEND;
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
                    public static final int ALL_SOUND_OFF = ControlChangeDefine.ALL_SOUND_OFF;

                    /**
                     * リセットオールコントローラー
                     */
                    public static final int RESET_ALL_CONTROLLER = ControlChangeDefine.RESET_ALL_CONTROLLER;

                    /**
                     * ローカルコントロール <br>
                     * オフにするとそのチャンネルはMIDI経由でのみ制御される
                     */
                    public static final int LOCAL_CONTROL = ControlChangeDefine.LOCAL_CONTROL;

                    /**
                     * オールノートオフ <br>
                     * 発音中の音をノートオフ状態にする(オールサウンドオフよりも弱い)
                     */
                    public static final int ALL_NOTE_OFF = ControlChangeDefine.ALL_NOTE_OFF;

                    /**
                     * オムニオフ
                     */
                    public static final int OMNI_OFF = ControlChangeDefine.OMNI_OFF;

                    /**
                     * オムニオン
                     */
                    public static final int OMNI_ON = ControlChangeDefine.OMNI_ON;

                    /**
                     * モノモード
                     */
                    public static final int MONO_MODE = ControlChangeDefine.MONO_ON;

                    /**
                     * ポリモード
                     */
                    public static final int POLY_MODE = ControlChangeDefine.POLY_ON;
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
                    public static final int SYSEX_BEGIN = CommandDefine.SYSEX_BEGIN;

                    /**
                     * MIDIタイムコード<br>
                     * Fst: td hex (t=0～7:フレームL/H,秒L/H,分L/H,時L/H)
                     */
                    public static final int MIDI_TIME_CODE = CommandDefine.MIDI_TIME_CODE;

                    /**
                     * ソングポジション<br>
                     * 曲先頭からのMIDIビートのカウンター(1MIDIビートは6MIDIクロック)<br>
                     * Fst: LSB(7bit) Snd: MSB(7bit)
                     */
                    public static final int SONG_POSITION = CommandDefine.SONG_POSITION;

                    /**
                     * ソングセレクト<br>
                     * 複数のソングを扱う場合に使用<br>
                     * Fst: ソング番号
                     */
                    public static final int SONG_SELECT = CommandDefine.SONG_SELECT;

                    /**
                     * チューンリクエスト<br>
                     * アナログシンセの自動チューニング機能を動作させる
                     */
                    public static final int TUNE_SELECT = CommandDefine.TUNE_SELECT;

                    /**
                     * システムエクスクルーシブ終了
                     */
                    public static final int SYSEX_END = CommandDefine.SYSEX_END;
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
                    public static final int MIDI_CLOCK = CommandDefine.MIDI_CLOCK;

                    /**
                     * 現在のシーケンスを開始
                     */
                    public static final int START = CommandDefine.START;

                    /**
                     * 前回停止した位置から再生する
                     */
                    public static final int CONTINUE = CommandDefine.CONTINUE;

                    /**
                     * 再生中のシーケンスを停止
                     */
                    public static final int STOP = CommandDefine.STOP;

                    /**
                     * アクティブセンシング<br>
                     * 送出側は300mSec以内に1度送出。<br>
                     * 受信側は受信していたアクティブセンシングが途絶えた場合、音を停止
                     */
                    public static final int ACTIVE_SENSING = CommandDefine.ACTIVE_SENSING;

                    /**
                     * 電源投入時の状態にリセットする<br>
                     * 使用が推奨されるものではなく、<br>
                     * 送信側機器は電源投入後、自動的に送出しない
                     */
                    public static final int RESET = CommandDefine.RESET;
                }
            }
        }/* System */
    }/* Status */
}
