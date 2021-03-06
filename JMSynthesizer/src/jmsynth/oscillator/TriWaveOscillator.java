package jmsynth.oscillator;

import jmsynth.sound.Tone;

public class TriWaveOscillator implements IOscillator {

    private static double LEVEL_OFFSET = COMMON_LEVEL_OFFSET;

    public TriWaveOscillator() {
    }

    @Override
    public int makeTone(byte[] data, int bufSize, Tone tone) {
        int length = bufSize;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード
        byte overallLevel = (byte) (tone.getOverallLevel());// ネイティブに変数ロード

        byte y = 0;// 生成データ一時格納変数（音量）

        /* 周期の設定（短いほど高音になる） */
        int amplitude = (int) tone.getAmplitude();

        overallLevel *= LEVEL_OFFSET;
        for (int i = 0; i < length; i = i + 2) {
            toneStep++;

            double f = (1.0 * toneStep / amplitude) - (toneStep / amplitude);
            if (f < 0.5) {
                y = (byte) ((data[i] + (-overallLevel + (overallLevel * (f / 1) * 4))));
            }
            else {
                y = (byte) ((data[i] + (overallLevel * 3 - (overallLevel * (f / 1) * 4))));
            }

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
