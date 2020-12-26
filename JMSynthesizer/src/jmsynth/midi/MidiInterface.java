package jmsynth.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

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
        if (message instanceof ShortMessage) {

            ShortMessage sm = (ShortMessage) message;
            int channel = sm.getChannel();
            if (controller.checkChannel(channel) == false) {
                return;
            }

            switch (sm.getCommand()) {
                case ShortMessage.NOTE_ON: {
                    controller.noteOn(channel, sm.getData1(), sm.getData2());
                }
                    break;
                case ShortMessage.NOTE_OFF: {
                    controller.noteOff(channel, sm.getData1());
                }
                    break;
                case ShortMessage.PITCH_BEND: {
                    // -8192 ～ +8191
                    int pitch = (sm.getData2() << 7) + (sm.getData1() & 0x7f) - 8192;
                    controller.pitchBend(channel, pitch);
                }
                    break;
                case ShortMessage.CONTROL_CHANGE:

                    switch (sm.getData1()) {
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
                                    controller.pitchBendSenc(channel, sm.getData2());
                                }
                                    break;
                                case 0x08:// ビブラート・レイト（Vibrato Rate）
                                {
                                    controller.setVibratoRate(channel, sm.getData2());
                                }
                                    break;
                                case 0x09:// ビブラート・デプス（Vibrato Depth）
                                {
                                    controller.setVibratoDepth(channel, sm.getData2());
                                }
                                    break;
                                case 0x0A:// ビブラート・ディレイ（Vibrato Delay）
                                {
                                    controller.setVibratoDelay(channel, sm.getData2());
                                }
                                    break;
                            }
                            break;
                        case 0x07:// メインボリューム(チャンネルの音量を設定）
                                  // float vol =
                                  // ((float)sm.getData2()/150);//127
                        {
                            float vol = ((float) sm.getData2() / 200);// 127
                            controller.setVolume(channel, vol);
                        }
                            break;
                        case 0x0A:// PAN
                            controller.setPan(channel, sm.getData2());
                            break;
                        case 0x0B:// Expression
                            controller.setExpression(channel, sm.getData2());
                            break;
                        case 0x60:// variation
                            controller.setVariation(channel, sm.getData2());
                            break;
                        case 0x61:// Portamento
                            break;
                        case 0x62:// NRPN
                            controller.setNRPN(channel, sm.getData2());
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
                case ShortMessage.PROGRAM_CHANGE: {
                    // オシレータの切り替え
                    if (isAutoSelectOscillator() == true) {
                        OscillatorSet osc = getProgramChangeOscillator(channel, sm.getData1());
                        controller.setOscillator(channel, osc);
                    }
                    controller.setVolume(channel, 1.0f);
                }
                    break;
                case 0x20:// バンク・セレクト LSB
                    break;
                default:
                    System.out.println(" ShortMessage" + sm.getCommand());
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
            ret = new OscillatorSet(WaveType.SINE);
        }
        else if (8 <= pc && pc < 16) {
            // Chromatic Percussion
            ret = new OscillatorSet(WaveType.TRIANGLE);
        }
        else if (16 <= pc && pc < 24) {
            // Organ
            ret = new OscillatorSet(WaveType.SAW);
        }
        else if (24 <= pc && pc < 32) {
            // Guitar
            ret = new OscillatorSet(WaveType.SAW);
        }
        else if (32 <= pc && pc < 40) {
            // Bass
            ret = new OscillatorSet(WaveType.TRIANGLE);
        }
        else if (40 <= pc && pc < 48) {
            // Strings
            ret = new OscillatorSet(WaveType.SAW);
        }
        else if (48 <= pc && pc < 56) {
            // Ensemble
            ret = new OscillatorSet(WaveType.SAW);
        }
        else if (56 <= pc && pc < 64) {
            // Brass
            ret = new OscillatorSet(WaveType.SAW);
        }
        else if (64 <= pc && pc < 72) {
            // Reed
            ret = new OscillatorSet(WaveType.SAW);
        }
        else if (72 <= pc && pc < 80) {
            // Pipe
            ret = new OscillatorSet(WaveType.PULSE);
        }
        else if (80 <= pc && pc < 88) {
            // Synth Lead
            ret = new OscillatorSet(WaveType.SQUARE);
        }
        else if (88 <= pc && pc < 96) {
            // Synth Pad
            // ret = new OscillatorSet(WaveType.NOISE);
            ret = new OscillatorSet(WaveType.SINE);
        }
        else if (96 <= pc && pc < 104) {
            // Synth Effects
            ret = new OscillatorSet(WaveType.NOISE);
            // ret = new OscillatorSet(WaveType.SINE);
        }
        else if (104 <= pc && pc < 112) {
            // Synth Effects
            ret = new OscillatorSet(WaveType.TRIANGLE);
        }
        else if (112 <= pc && pc < 120) {
            // Percussive
            ret = new OscillatorSet(WaveType.TRIANGLE);
        }
        else if (120 <= pc && pc < 128) {
            // Sound effects
            ret = new OscillatorSet(WaveType.NOISE);
            // ret = new OscillatorSet(WaveType.SINE);
        }
        else {
            ret = new OscillatorSet(WaveType.NOISE);
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
