package jmsynth.sound;

public class Tone {

    // 周波数
    private double frequency;
    // 波長(バイト数)
    private double amplitude;

    // Note情報
    private float defaultPitch = 440;
    private int note = 1;

    private float pitch = 0;
    private int overallLevel = 0; // (ベロシティ値 ÷ 8) × (エクスプレッション値 ÷ 127)
    private int velocity = 60; // ベロシティ
    private int expression = 127; // エクスプレッション値

    private int toneStep = 0;
    private double tablePointer = 0;// Waveテーブルのポインタ
    private double stapTimingCounter = 0;// Waveテーブルのポインタを進めるタイミングのカウンター

    private float vibratoDepth = 0;

    private double enveropeOffset = 1.0;
    private long startMills = 0;

    public Tone() {
    }

    public void reset() { //
        setVelocity(127);

        defaultPitch = 440;
        note = 1;
        pitch = 0;
        // vibrato = 0;
        expression = 127; // エクスプレッション値

        vibratoDepth = 3;
        // vibratoRate = 0.5;
        // vibratoDelay = 60;
        // vibratoCount = 0;
        // variation = 0;
        enveropeOffset = 1.0;
        startMills = 0;
        setOverallLevel();
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        if (frequency <= 0) {
            return;
        }
        this.amplitude = SoundSourceChannel.SAMPLE_RATE / frequency; // 振幅
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public int getNote() {
        return note;
    }

    private double note2freq(double note) {
        return (double) Math.floor(defaultPitch * Math.pow(2, ((double) note - 69) / 12));
    }

    public void setNote(int note) {
        this.pitch = 0;
        this.note = note;
        this.toneStep = 0;
        setFrequency(note2freq(note));
    }

    public int getOverallLevel() {
        return overallLevel;
    }

    public void setOverallLevel() {
        this.overallLevel = (int) (((double)velocity / (double)8) * ((double)expression / (double)127) * (double)enveropeOffset);

    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
        setOverallLevel();
    }

    public int getExpression() {
        return expression;
    }

    public void setExpression(int expression) {
        this.expression = expression;
        setOverallLevel();
    }

    public int getToneStep() {
        return toneStep;
    }

    public void setToneStep(int toneStep) {
        this.toneStep = toneStep;
    }

    public double getTablePointer() {
        return tablePointer;
    }

    public void setTablePointer(double tablePointer) {
        this.tablePointer = tablePointer;
    }

    public double getStapTimingCounter() {
        return stapTimingCounter;
    }

    public void setStapTimingCounter(double stapTimingCounter) {
        this.stapTimingCounter = stapTimingCounter;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        setFrequency(note2freq(note+pitch));
    }

    public float getVibratoDepth() {
        return vibratoDepth;
    }

    public void setVibratoDepth(float vibratoDepth) {
        this.vibratoDepth = vibratoDepth;
    }

    public double getEnveropeOffset() {
        return this.enveropeOffset;
    }

    public void setEnveropeOffset(double enveropeOffset) {
        this.enveropeOffset = enveropeOffset;
        setOverallLevel();
    }

    public long getStartMills() {
        return startMills;
    }

    public void setStartMills(long startMills) {
        this.startMills = startMills;
        setEnveropeOffset(0.0);
    }
    public void setStartMills() {
        setStartMills(System.currentTimeMillis());
    }

}
