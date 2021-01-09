package jmsynth.oscillator;

import jmsynth.sound.Tone;

public class NoisWaveOscillator implements IOscillator {

    //private static double LEVEL_OFFSET = 1.0;

    class DrumSim {
        public DrumSim(int a) {
            amp = a;
        }
        public int amp = 0;
    }

    private DrumSim[] drumSet = new DrumSim[128];

    public NoisWaveOscillator() {
        for (int i=0; i<drumSet.length; i++) {
            drumSet[i] = new DrumSim(200);
        }
        drumSet[35] = new DrumSim(70); // Acou Bass Drum
        drumSet[36] = new DrumSim(70); // Bass Drum1
        drumSet[37] = new DrumSim(20); // Side Stick
        drumSet[38] = new DrumSim(10); // Acou Snare
        drumSet[39] = new DrumSim(1); // Hand Clap
        drumSet[40] = new DrumSim(10); // E. Snare
        drumSet[41] = new DrumSim(40); // Low Floor Tom
        drumSet[42] = new DrumSim(5); // Closed Hi-Hat
        drumSet[43] = new DrumSim(35); // High Floor Tom
        drumSet[44] = new DrumSim(3); // Pedal Hi-Hat
        drumSet[45] = new DrumSim(40); // Low Tom
        drumSet[46] = new DrumSim(1); // Open Hi-Hat
        drumSet[47] = new DrumSim(40); // Low Mid Tom
        drumSet[48] = new DrumSim(35); // High Mid Tom
        drumSet[49] = new DrumSim(1); // Crash Cymbal1
        drumSet[50] = new DrumSim(35); // High Tom
        drumSet[51] = new DrumSim(2); // Ride Cymbal1
        drumSet[52] = new DrumSim(1); // Chinese Cymbal
        drumSet[53] = new DrumSim(1); // Ride Bell
        drumSet[54] = new DrumSim(1); // Tambourine
        drumSet[55] = new DrumSim(1); // Crash Cymbal1
        drumSet[56] = new DrumSim(1); // Cowbell
        drumSet[57] = new DrumSim(1); // Crash Cymbal2
        drumSet[58] = new DrumSim(1); // Vibraslap
        drumSet[59] = new DrumSim(3); // Ride Cymbal2
    }

    @Override
    public int makeTone(byte[] data, int bufSize, Tone tone) {
        int length = bufSize;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード

        double f = 0;// 生成データ一時格納変数（音量）

        ///* ノイズ周期の設定（短いほど高音になる） */
        //int amplitude = (int) (50 - tone.getNote());

        /* ノート番号の音階に従って音の高さを調整する */
        int amplitude = drumSet[tone.getNote()].amp;
        //int amplitude = (int) (tone.getNote() * 0.1);

        double overallLevel = (double) (tone.getOverallLevel() * 2.0);// ネイティブに変数ロード

        byte y = 0;
        for (int i = 0; i < length; i = i + 2) {
            toneStep++;

            /* toneStepが_amplitudeを超えたら音量を変更する */
            if (toneStep > amplitude) {
                /* 音量データ生成 */
                f = (-overallLevel / 2 + (Math.random() * overallLevel));
                toneStep = 0;
            }

            y = (byte) (data[i] + f);
            /* Lch 分 */
            data[i] = y;
            /* Rch 分 */
            data[i + 1] = y;

        }
        tone.setToneStep(toneStep);
        return length;
    }

    @Override
    public boolean isToneSync() {
        return true;
    }

    @Override
    public int toneLoopPoint() {
        return 0;
    }

    @Override
    public int toneEndPoint() {
        return -1;
    }

}
