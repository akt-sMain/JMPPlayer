package jmsynth.oscillator;

import jmsynth.sound.Tone;

public class LongCycleNoiseOscillator implements IOscillator {

    @Override
    public int makeTone(byte[] data, int sampleRate, Tone tone) {
        int length = sampleRate;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード
        byte overallLevel = (byte) (tone.getOverallLevel());// ネイティブに変数ロード

        byte y = 0;// 生成データ一時格納変数（音量）

        /* 周期の設定（短いほど高音になる） */
        int amplitude = (int) tone.getAmplitude();
        for (int i = 0; i < length; i = i + 2) {
            toneStep++;
            double f = (1.0 * (double)toneStep / (double)amplitude) - (double)(toneStep / amplitude);
            y = (byte) (WaveGenerater.makeSquareWave(f, overallLevel, false) & 0xff);

            /* Lch 分 */
            data[i] += y;
            /* Rch 分 */
            data[i + 1] += y;
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
