package jmsynth.oscillator;

import java.util.Random;

import jmsynth.sound.Tone;

public class NoisWaveOscillator implements IOscillator {

    //private static double LEVEL_OFFSET = 1.0;

    class NoiseSim {
        public NoiseSim(int a) {
            amp = a;
        }
        public int amp = 0;
    }

    private NoiseSim[] simSet = new NoiseSim[128];

    static final int[] AMP_TABLE = {
            150, 100, 50, 30, 20, 15, 10, 9,
              8,   7,  6,  5,  4,  3,  2, 1,
            };

    public NoisWaveOscillator() {
        this.isShortCycle = false;
        init();
    }
    public NoisWaveOscillator(boolean isShortCycle) {
        this.isShortCycle = isShortCycle;
        init();
    }

    private void init() {
        int i = 0;
        int j = 0;
        while (true) {
            simSet[i] = new NoiseSim(AMP_TABLE[j]);
            i++;
            j++;
            if (j >= 16) {
                j=0;
            }
            if (i >= simSet.length) {
                break;
            }
        }
        for (i=0;i<NOISE_VAR;i++) {
            moiseTable[i] = Math.random();
        }
    }

//    private void init() {
//        for (int i = 0; i < simSet.length; i++) {
//            simSet[i] = new NoiseSim(200);
//        }
//        simSet[35] = new NoiseSim(60); // Acou Bass Drum
//        simSet[36] = new NoiseSim(60); // Bass Drum1
//        simSet[37] = new NoiseSim(20); // Side Stick
//        simSet[38] = new NoiseSim(10); // Acou Snare
//        simSet[39] = new NoiseSim(1); // Hand Clap
//        simSet[40] = new NoiseSim(10); // E. Snare
//        simSet[41] = new NoiseSim(40); // Low Floor Tom
//        simSet[42] = new NoiseSim(5); // Closed Hi-Hat
//        simSet[43] = new NoiseSim(35); // High Floor Tom
//        simSet[44] = new NoiseSim(3); // Pedal Hi-Hat
//        simSet[45] = new NoiseSim(40); // Low Tom
//        simSet[46] = new NoiseSim(1); // Open Hi-Hat
//        simSet[47] = new NoiseSim(40); // Low Mid Tom
//        simSet[48] = new NoiseSim(35); // High Mid Tom
//        simSet[49] = new NoiseSim(1); // Crash Cymbal1
//        simSet[50] = new NoiseSim(35); // High Tom
//        simSet[51] = new NoiseSim(2); // Ride Cymbal1
//        simSet[52] = new NoiseSim(1); // Chinese Cymbal
//        simSet[53] = new NoiseSim(1); // Ride Bell
//        simSet[54] = new NoiseSim(1); // Tambourine
//        simSet[55] = new NoiseSim(1); // Crash Cymbal1
//        simSet[56] = new NoiseSim(1); // Cowbell
//        simSet[57] = new NoiseSim(1); // Crash Cymbal2
//        simSet[58] = new NoiseSim(1); // Vibraslap
//        simSet[59] = new NoiseSim(3); // Ride Cymbal2
//    }

    double[] moiseTable = new double[NOISE_VAR];

    boolean isShortCycle = true;
    final static int NOISE_VAR = 32;
    int ntStep = 0;

    @Override
    public int makeTone(byte[] data, int bufSize, Tone tone) {
        int length = bufSize;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード

        double f = 0;// 生成データ一時格納変数（音量）

        ///* ノイズ周期の設定（短いほど高音になる） */
        //int amplitude = (int) (50 - tone.getNote());

        /* ノート番号の音階に従って音の高さを調整する */
        //int amplitude = (int) (tone.getNote() * 0.1);

        /* あらかじめ定義したAmpをシミュレートする */
        int amplitude = simSet[tone.getNote()].amp;

        double overallLevel = (double) (tone.getOverallLevel() * 2.0);// ネイティブに変数ロード

        byte y = 0;
        for (int i = 0; i < length; i = i + 2) {
            toneStep++;

            /* toneStepがamplitudeを超えたら音量を変更する */
            if (toneStep > amplitude) {

                double ran;
                if (isShortCycle == true) {
                    // 短周期を繰り返す
                    ran = moiseTable[ntStep];
                    ntStep++;
                    if (ntStep >= NOISE_VAR) {
                        ntStep = 0;
                    }
                }
                else {
                    // 完全ランダム
                    Random rn = new Random();
                    int iRan = rn.nextInt(NOISE_VAR);
                    ran = moiseTable[iRan];
                    //ran = Math.random();
                }

                /* 音量データ生成 */
                f = (-overallLevel / 2 + (ran * overallLevel));
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

    @Override
    public void setWaveReverse(boolean isReverse) {
    }

    @Override
    public boolean isWaveReverse() {
        return false;
    }

}
