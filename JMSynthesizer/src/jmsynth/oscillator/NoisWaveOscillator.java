package jmsynth.oscillator;

import jmsynth.sound.Tone;

public class NoisWaveOscillator implements IOscillator {

    //private static double LEVEL_OFFSET = 1.0;

    public NoisWaveOscillator() {
    }

    @Override
    public int makeTone(byte[] data, int bufSize, Tone tone) {
        int length = bufSize;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード

        double f = 0;// 生成データ一時格納変数（音量）

        ///* ノイズ周期の設定（短いほど高音になる） */
        //int amplitude = (int) (50 - tone.getNote());

        /* ノート番号の音階に従って音の高さを調整する */
        int amplitude = (int) ((128 - tone.getNote()) * 0.3);

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
