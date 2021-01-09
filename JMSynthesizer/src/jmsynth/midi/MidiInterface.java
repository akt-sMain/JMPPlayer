package jmsynth.midi;

import java.util.Arrays;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jmsynth.oscillator.OscillatorSet;
import jmsynth.oscillator.OscillatorSet.WaveType;
import jmsynth.sound.ISynthController;

public class MidiInterface implements Receiver {

    private ISynthController controller = null;

    private boolean isAutoSelectOscillator = true;

    private ProgramChangeTable table = null;

    public MidiInterface(ISynthController controller) {
        this.controller = controller;
        this.table = new DefaultProgramChangeTable();
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message == null) {
            return;
        }
        byte[] aMessage = message.getMessage();
        int length = message.getLength();
        if (MidiUtil.isChannelMessage(aMessage, length) == true) {

            int channel = MidiUtil.getChannel(aMessage, length);
            if (controller.checkChannel(channel) == false) {
                return;
            }

            int command = MidiUtil.getCommand(aMessage, length);
            int data1 = MidiUtil.getData1(aMessage, length);
            int data2 = MidiUtil.getData2(aMessage, length);

            switch (command) {
                case DefineCommand.NOTE_ON: {
                    controller.noteOn(channel, data1, data2);
                }
                    break;
                case DefineCommand.NOTE_OFF: {
                    controller.noteOff(channel, data1);
                }
                    break;
                case DefineCommand.PITCH_BEND: {
                    // -8192 ～ +8191
                    int pitch = (data2 << 7) + (data1 & 0x7f) - 8192;
                    controller.pitchBend(channel, pitch);
                }
                    break;
                case DefineCommand.CONTROL_CHANGE:

                    switch (data1) {
                        case DefineControlChange.BANK_SELECT_MSB://
                            break;
                        case DefineControlChange.MODULATION:// モジュレーション・デプス
                            controller.setModulationDepth(channel, data2);
                            break;

                        case DefineControlChange.PORTAMENTO_TIME:// Portamento Time
                            break;
                        case DefineControlChange.DATA_ENTRY_MSB:// データエントリ（ＲＰＮ／ＮＲＰＮで指定したパラメータの値を設定）
                                  // System.out.println("NRPN "+NRPN +" " +
                                  // sm.getData2() +" ");
                            switch (controller.getNRPN(channel)) {
                                // ピッチベンド・センシティビティ
                                case 0x00: {
                                    controller.pitchBendSenc(channel, data2);
                                }
                                    break;
                                case 0x08:// ビブラート・レイト（Vibrato Rate）
                                {
                                    controller.setVibratoRate(channel, data2);
                                }
                                    break;
                                case 0x09:// ビブラート・デプス（Vibrato Depth）
                                {
                                    controller.setVibratoDepth(channel, data2);
                                }
                                    break;
                                case 0x0A:// ビブラート・ディレイ（Vibrato Delay）
                                {
                                    controller.setVibratoDelay(channel, data2);
                                }
                                    break;
                            }
                            break;
                        case DefineControlChange.MAIN_VOLUME:// メインボリューム(チャンネルの音量を設定）
                                  // float vol =
                                  // ((float)sm.getData2()/150);//127
                        {
                            float vol = ((float) data2 / 200);// 127
                            controller.setVolume(channel, vol);
                        }
                            break;
                        case DefineControlChange.PAN_POT:// PAN
                            controller.setPan(channel, data2);
                            break;
                        case DefineControlChange.EXPRESSION:// Expression
                            controller.setExpression(channel, data2);
                            break;
//                        case 0x60:// variation
//                            controller.setVariation(channel, data2);
//                            break;
//                        case 0x61:// Portamento
//                            break;
                        case DefineControlChange.NRPN_LSB:// NRPN
                            controller.setNRPN(channel, data2);
                            break;
                        case DefineControlChange.RPN_MSB:// 101
                            controller.setNRPN(channel, 0x00);
                            break;
                        case DefineControlChange.RESET_ALL_CONTROLLER:// リセット オール コントローラー
                            controller.resetAllController(channel);
                            break;
                        case DefineControlChange.ALL_SOUND_OFF:// All Sound Off
                        case DefineControlChange.ALL_NOTE_OFF:// All Note Off
                            controller.allNoteOff(channel);
                            break;
                        default:
                            break;
                    }
                    break;
                case DefineCommand.PROGRAM_CHANGE: {
                    // オシレータの切り替え
                    if (isAutoSelectOscillator() == true) {
                        if (channel != 9) { //10chはドラム
                            OscillatorSet osc = getProgramChangeOscillator(channel, data1);
                            controller.setOscillator(channel, osc);
                        }
                    }
                    controller.setVolume(channel, 1.0f);
                }
                    break;
                default:
                    System.out.println("unknown " + command);
                    break;
            }
        }
        else if (MidiUtil.isMetaMessage(aMessage, length) == true) {
        }
        else if (MidiUtil.isSystemMessage(aMessage, length) == true) {
            int status = MidiUtil.getStatus(aMessage, length);
            switch (status) {
                case DefineCommand.SYSEX_BEGIN:
                    if (Arrays.equals(aMessage, MidiUtil.GM_SYSTEM_ON) == true) {
                        controller.systemReset();
                    }
                    else if (Arrays.equals(aMessage, MidiUtil.GS_RESET) == true) {
                        controller.systemReset();
                    }
                    else if (Arrays.equals(aMessage, MidiUtil.XG_SYSTEM_ON) == true) {
                        controller.systemReset();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private OscillatorSet getProgramChangeOscillator(int ch, int pc) {
        if (ch == 9) {
            // 10chはノイズ固定
            return new OscillatorSet(WaveType.NOISE);
        }
        return table.getOscillatorSet(pc);
    }

    public void open() {
        controller.openDevice();
    }

    @Override
    public void close() {
        controller.closeDevice();
    }

    public boolean isAutoSelectOscillator() {
        return isAutoSelectOscillator;
    }

    public void setAutoSelectOscillator(boolean isAutoSelectOscillator) {
        this.isAutoSelectOscillator = isAutoSelectOscillator;
    }

    public ISynthController getSynthController() {
        return controller;
    }
}
