package jmsynth.midi;

import java.util.Arrays;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jlib.midi.MidiByte;
import jmsynth.oscillator.IOscillator.WaveType;
import jmsynth.oscillator.OscillatorSet;
import jmsynth.sound.ISynthController;

public class MidiInterface implements Receiver {

    ISynthController controller = null;

    private boolean isAutoSelectOscillator = true;

    public MidiInterface(ISynthController controller) {
        this.controller = controller;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message == null) {
            return;
        }
        byte[] aMessage = message.getMessage();
        int length = message.getLength();
        if (MidiByte.isChannelMessage(aMessage, length) == true) {

            int channel = MidiByte.getChannel(aMessage, length);
            if (controller.checkChannel(channel) == false) {
                return;
            }

            int command = MidiByte.getCommand(aMessage, length);
            int data1 = MidiByte.getData1(aMessage, length);
            int data2 = MidiByte.getData2(aMessage, length);

            switch (command) {
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON: {
                    controller.noteOn(channel, data1, data2);
                }
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF: {
                    controller.noteOff(channel, data1);
                }
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND: {
                    // -8192 ～ +8191
                    int pitch = (data2 << 7) + (data1 & 0x7f) - 8192;
                    controller.pitchBend(channel, pitch);
                }
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE:

                    switch (data1) {
                        case 0x00://
                            break;
                        case 0x01:// モジュレーション・デプス
                            break;

                        case 0x05:// Portamento Time
                            break;
                        case 0x06:// データエントリ（ＲＰＮ／ＮＲＰＮで指定したパラメータの値を設定）
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
                        case 0x07:// メインボリューム(チャンネルの音量を設定）
                                  // float vol =
                                  // ((float)sm.getData2()/150);//127
                        {
                            float vol = ((float) data2 / 200);// 127
                            controller.setVolume(channel, vol);
                        }
                            break;
                        case 0x0A:// PAN
                            controller.setPan(channel, data2);
                            break;
                        case 0x0B:// Expression
                            controller.setExpression(channel, data2);
                            break;
                        case 0x60:// variation
                            controller.setVariation(channel, data2);
                            break;
                        case 0x61:// Portamento
                            break;
                        case 0x62:// NRPN
                            controller.setNRPN(channel, data2);
                            break;
                        case 0x65:// 101
                            controller.setNRPN(channel, 0x00);
                            break;
                        case 0x79:// リセット オール コントローラー
                            controller.resetAllController(channel);
                            break;
                        case 0x7B:// All Note Off
                            controller.allNoteOff(channel);
                            break;
                        default:
                            break;
                    }
                    break;
                case MidiByte.Status.Channel.ChannelVoice.Fst.PROGRAM_CHANGE: {
                    // オシレータの切り替え
                    if (isAutoSelectOscillator() == true) {
                        OscillatorSet osc = getProgramChangeOscillator(channel, data1);
                        controller.setOscillator(channel, osc);
                    }
                    controller.setVolume(channel, 1.0f);
                }
                    break;
                default:
                    System.out.println("unknown " + command);
                    break;
            }
        }
        else if (MidiByte.isMetaMessage(aMessage, length) == true) {
        }
        else if (MidiByte.isSystemMessage(aMessage, length) == true) {
            int status = MidiByte.getStatus(aMessage, length);
            switch (status) {
                case MidiByte.Status.System.SystemCommon.Fst.SYSEX_BEGIN:
                    if (Arrays.equals(aMessage, MidiByte.GM_SYSTEM_ON) == true) {
                        controller.systemReset();
                    }
                    else if (Arrays.equals(aMessage, MidiByte.GS_RESET) == true) {
                        controller.systemReset();
                    }
                    else if (Arrays.equals(aMessage, MidiByte.XG_SYSTEM_ON) == true) {
                        controller.systemReset();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private OscillatorSet getProgramChangeOscillator(int ch, int pc) {
        OscillatorSet ret = new OscillatorSet(WaveType.NOISE);
        if (ch == 9) {
            // 10chはノイズ固定
            return ret;
        }

        if (0 <= pc && pc < 8) {
            // Piano
            ret = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        }
        else if (8 <= pc && pc < 16) {
            // Chromatic Percussion
            ret = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        }
        else if (16 <= pc && pc < 24) {
            // Organ
            ret = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        }
        else if (24 <= pc && pc < 32) {
            // Guitar
            ret = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        }
        else if (32 <= pc && pc < 40) {
            // Bass
            ret = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        }
        else if (40 <= pc && pc < 48) {
            // Strings
            ret = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        }
        else if (48 <= pc && pc < 56) {
            // Ensemble
            ret = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        }
        else if (56 <= pc && pc < 64) {
            // Brass
            ret = new OscillatorSet(WaveType.PULSE);
        }
        else if (64 <= pc && pc < 72) {
            // Reed
            ret = new OscillatorSet(WaveType.PULSE);
        }
        else if (72 <= pc && pc < 80) {
            // Pipe
            ret = new OscillatorSet(WaveType.PULSE);
        }
        else if (80 <= pc && pc < 88) {
            // Synth Lead
            switch (pc) {
                case 80:
                    // Square Wave
                    ret = new OscillatorSet(WaveType.SQUARE);
                    break;
                case 81:
                    // Saw Wave
                    ret = new OscillatorSet(WaveType.SAW);
                    break;
                case 82:
                    // Syn Calliope
                    ret = new OscillatorSet(0.1, 0.0, 1.0, 0.1, WaveType.TRIANGLE);
                    break;
                case 83:
                    // Chiffer Lead
                    ret = new OscillatorSet(0.25, 0.0, 1.0, 0.25, WaveType.TRIANGLE);
                    break;
                case 84:
                    // Charang
                    ret = new OscillatorSet(0.1, 0.0, 1.0, 0.0, WaveType.PULSE);
                    break;
                case 85:
                    // Solo Vox
                    ret = new OscillatorSet(0.1, 0.0, 1.0, 0.0, WaveType.SAW);
                    break;
                case 86:
                    // 7th Saw
                    ret = new OscillatorSet(0.1, 0.0, 1.0, 0.0, WaveType.SAW);
                    break;
                case 87:
                default:
                    // Bass & Lead
                    ret = new OscillatorSet(0.0, 0.0, 1.0, 0.0, WaveType.SAW);
                    break;
            }
        }
        else if (88 <= pc && pc < 96) {
            // Synth Pad
            // ret = new OscillatorSet(WaveType.NOISE);
            ret = new OscillatorSet(WaveType.SINE);
            switch (pc) {
                case 88:
                    // Fantasia
                    ret = new OscillatorSet(0.0, 1.0, 0.25, 0.5, WaveType.SINE);
                    break;
                default:
                    ret = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
                    break;
            }
        }
        else if (96 <= pc && pc < 104) {
            // Synth Effects
            ret = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
            // ret = new OscillatorSet(WaveType.SINE);
        }
        else if (104 <= pc && pc < 112) {
            // ethnic
            ret = new OscillatorSet(WaveType.PULSE);
        }
        else if (112 <= pc && pc < 120) {
            // Percussive
            ret = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        }
        else if (120 <= pc && pc < 128) {
            // Sound effects
            ret = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
            // ret = new OscillatorSet(WaveType.SINE);
        }
        else {
            ret = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
            // ret = new OscillatorSet(WaveType.SINE);
        }
        return ret;
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
