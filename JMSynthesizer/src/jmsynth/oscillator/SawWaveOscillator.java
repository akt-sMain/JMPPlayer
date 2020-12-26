package jmsynth.oscillator;

import jmsynth.sound.Tone;

public class SawWaveOscillator implements IOscillator {

    private static double LEVEL_OFFSET = COMMON_LEVEL_OFFSET;

    public SawWaveOscillator() {
    }

    @Override
    public int makeTone(byte[] data, int bufSize, Tone tone) {

        int amplitude = (int) tone.getAmplitude();
        int length = bufSize;
        int overallLeval = tone.getOverallLevel();
        byte y = 0;// 生成データ一時格納変数（音量）
        int step = tone.getToneStep();

        overallLeval *= LEVEL_OFFSET;
        for (int i = 0; i < length; i = i + 2) {
            step++;
            double f = (1.0 * step / amplitude) - (step / amplitude);

            y = (byte)(((2.0 * (1.0 - f)) * overallLeval) - (overallLeval));

            /* Lch 分 */
            data[i] += y;
            /* Rch 分 */
            data[i + 1] += y;
        }
        tone.setToneStep(step);
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
