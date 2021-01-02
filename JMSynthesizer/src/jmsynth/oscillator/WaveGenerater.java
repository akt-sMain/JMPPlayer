package jmsynth.oscillator;

public class WaveGenerater {
    private WaveGenerater() {}

    /**
     * サイン波生成
     *
     * @param f
     * @param overallLeval
     * @param reverse
     * @return
     */
    public static byte makeSinWave(double f, int overallLeval, boolean reverse) {
        double ff = (reverse == false) ? (1.0 - f) : f;
        return (byte) ((Math.sin(2.0 * Math.PI * ff) * overallLeval));
    }

    /**
     * 矩形波生成
     *
     * @param f
     * @param overallLevel
     * @param reverse
     * @return
     */
    public static byte makeSquareWave(double f, int overallLevel, boolean reverse) {
        return makePulseWave(f, overallLevel, 0.5, reverse);
    }

    /**
     * パルス波生成
     *
     * @param f
     * @param overallLevel
     * @param reverse
     * @return
     */
    public static byte makePulseWave(double f, int overallLevel, boolean reverse) {
        return makePulseWave(f, overallLevel, 0.2, reverse);
    }

    private static final double DUTY_THRESHOLD = 0.02;
    private static final double DUTY_OFFSET = 0.5;

    /**
     * パルス波生成
     *
     * @param f
     * @param overallLevel
     * @param duty
     * @param reverse
     * @return
     */
    public static byte makePulseWave(double f, int overallLevel, double duty, boolean reverse) {
        double ff = (reverse == true) ? (1.0 - f) : f;

        byte y = 0;
        /* 矩形波を少しゆがませる */
        if (ff > duty) {
            if (((1.0 - DUTY_THRESHOLD) < ff) || (ff < (duty + DUTY_THRESHOLD))) {
                y = (byte) (overallLevel * DUTY_OFFSET);
            }
            else {
                y = (byte) (overallLevel);
            }
        }
        else {
            if (((duty - DUTY_THRESHOLD) < ff) || (ff < (0.0 + DUTY_THRESHOLD))) {
                y = (byte) (-overallLevel * DUTY_OFFSET);
            }
            else {
                y = (byte) (-overallLevel);
            }
        }
        return y;
    }

    /**
     * のこぎり波生成
     *
     * @param f
     * @param overallLeval
     * @param reverse
     * @return
     */
    public static byte makeSawWave(double f, int overallLeval, boolean reverse) {
        double ff = (reverse == true) ? (1.0 - f) : f;
        return (byte)(((2.0 * ff) * overallLeval) - (overallLeval));
    }

    /**
     * 三角波生成
     *
     * @param f
     * @param overallLevel
     * @param reverse
     * @return
     */
    public static byte makeTriangleWave(double f, int overallLevel, boolean reverse) {
        double ff = (reverse == true) ? (1.0 - f) : f;

        byte y = 0;
        int slope = (int)((ff / 0.25) + 1) % 2; // 0=上り, 1=下り
        double quarter = ff % 0.25;
        double sign = (ff < 0.5) ? -1.0 : 1.0;
        if (slope == 0) {
            // 上り
            y = (byte) (((overallLevel - (overallLevel * (quarter / 0.25)))) * sign);
        }
        else {
            // 下り
            y = (byte) ((overallLevel * (quarter / 0.25)) * sign);
        }
        return y;
    }

}
