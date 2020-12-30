package jmsynth.sound;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import jmsynth.app.component.IWaveRepaintListener;
import jmsynth.enverope.Envelope;
import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.IOscillator.WaveType;
import jmsynth.oscillator.NoisWaveOscillator;
import jmsynth.oscillator.PulseWaveOscillator;
import jmsynth.oscillator.RectWaveOscillator;
import jmsynth.oscillator.SawWaveOscillator;
import jmsynth.oscillator.SinWaveOscillator;
import jmsynth.oscillator.TriWaveOscillator;

public class SoundSourceChannel extends Thread implements ISynthController {
    // static int SAMPLE_RATE = 11025; // サンプルレート
    // static int SAMPLE_RATE = 22050; // サンプルレート
    // static int SAMPLE_RATE = 44100; // サンプルレート
    public static final float SAMPLE_RATE = 44100.0f;

    public static final boolean SAMPLE_16BITS = true;
    public static final int SAMPLE_SIZE = SAMPLE_16BITS ? 16 : 8;
    public static final int CHANNEL = 2;
    public static final int FRAME_SIZE = CHANNEL * (SAMPLE_SIZE / 8);
    public static AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNEL, true, true); // 再生フォーマット

    // 1フレームで再生するバイト数
    public static int BUF_SIZE = ((int) SAMPLE_RATE / 50 * FRAME_SIZE);

    // 出力ライン
    public SourceDataLine line = null;

    private byte[] displayBuf = null;

    private int channel;
    public boolean isRunnable;

    /* オシレータ */
    private IOscillator oscillator;

    private int NRPN = 0;
    private float pitch_sc = 2;

    /** アクティブな音声を管理 */
    public Vector<Tone> activeTones = null;

    /** 発声中の音色を管理するためのテーブル */
    public Tone[] tones = null;

    /** 利用可能な音色をプールするためのクラス */
    public Stack<Tone> tonePool = null;

    private IWaveRepaintListener waveRepaintListener = null;

    private WaveType waveType = WaveType.SINE;

    HashMap<WaveType, IOscillator> oscMap = null;

    protected Envelope envelope = null;

    public SoundSourceChannel(int channel, WaveType oscType, int polyphony, Envelope envelope) {
        init(channel, oscType, polyphony, envelope);
    }
    public SoundSourceChannel(int channel, WaveType oscType, int polyphony) {
        init(channel, oscType, polyphony, null);
    }

    private void init(int channel, WaveType oscType, int polyphony, Envelope envelope) {
        oscMap = new HashMap<IOscillator.WaveType, IOscillator>(){
            {
                put(WaveType.SINE, new SinWaveOscillator());
                put(WaveType.SAW, new SawWaveOscillator());
                put(WaveType.SQUARE, new RectWaveOscillator());
                put(WaveType.TRIANGLE, new TriWaveOscillator());
                put(WaveType.PULSE, new PulseWaveOscillator());
                put(WaveType.NOISE, new NoisWaveOscillator());
            }
        };

        this.channel = channel;
        setOscillator(oscType);

        // ライン情報取得
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, FORMAT, BUF_SIZE);
        try {
            // 出力ライン取得
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open();
            line.flush();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }
        this.setPriority(MAX_PRIORITY);

        activeTones = new Vector<Tone>();
        tonePool = new Stack<Tone>();
        tones = new Tone[128];

        this.envelope = envelope;
        if (this.envelope != null) {
            this.envelope.setTargetTones(activeTones);
        }

        /* 音階データ生成 */
        for (int i = 0; i < polyphony; i++) {
            try {
                Tone rw = new Tone();
                rw.setFrequency(523.3);// 523.3
                rw.setVelocity(0);
                tonePool.add(rw);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void start() {
        if (isRunnable == true) {
            return;
        }
        super.start();
    }

    public synchronized int makeTone(byte[] data, int bufSize) {
        int length = Math.min(bufSize, data.length);

        Arrays.fill(data, (byte) 0x00);

        for (int i = 0; i < activeTones.size(); i++) {
            try {
                Tone tone = (Tone) activeTones.get(i);
                oscillator.makeTone(data, length, tone);

                if (envelope.getReleaseTime() > 0.0) {
                    // リリース処理
                    if (tone.isReleaseFlag() == true && tone.getEnveropeOffset() <= 0.0) {
                        int note = tone.getNote();
                        noteOff(0, note);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException aiobe) {
                // ArrayIndexOutOfBoundsExceptionは起きがち
                // スレッドのタイミング次第なためとりあえず無視...
                //aiobe.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return length;
    }

    public int calcSampleRate(long milliseconds) {
        return (int) (milliseconds / 1000 * BUF_SIZE);
    }

    /**
     * バッファ再生処理
     */
    private static int REPAINT_CYCLE = 2;
    private int repWait = 0;

    public void run() {
        //long current = System.currentTimeMillis();
        isRunnable = true;
        line.start();
        byte[] waveData = new byte[BUF_SIZE]; // 波形データ
        displayBuf = new byte[BUF_SIZE]; // 波形データ

        int sampleRate = BUF_SIZE;
        while (isRunnable) {
            try {
                //current = System.currentTimeMillis();
                int length = makeTone(waveData, sampleRate); // 再生するたびに作り直す

                // System.out.println(length);
                if (repWait % REPAINT_CYCLE == 0) {
                    callWaveRepaint(waveData);
                    repWait = 0;
                }
                repWait++;

                line.write(waveData, 0, length);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        line.close();
    }

    // float input[] …入力信号の格納されたバッファ。
    // flaot output[] …フィルタ処理した値を書き出す出力信号のバッファ。
    // int size …入力信号・出力信号のバッファのサイズ。
    // float samplerate … サンプリング周波数。
    // float freq … カットオフ周波数。
    // float q … フィルタのQ値。
//    private void lowpass(byte input[], byte output[], int size, double samplerate, double freq, double q) {
//        // フィルタ係数を計算する
//        double omega = 2.0f * 3.14159265f * freq / samplerate;
//        double alpha = Math.sin(omega) / (2.0f * q);
//
//        double a0 = 1.0f + alpha;
//        double a1 = -2.0f * Math.cos(omega);
//        double a2 = 1.0f - alpha;
//        double b0 = (1.0f - Math.cos(omega)) / 2.0f;
//        double b1 = 1.0f - Math.cos(omega);
//        double b2 = (1.0f - Math.cos(omega)) / 2.0f;
//
//        // フィルタ計算用のバッファ変数。
//        double in1 = 0.0f;
//        double in2 = 0.0f;
//        double out1 = 0.0f;
//        double out2 = 0.0f;
//
//        // フィルタを適用
//        for (int i = 0; i < size; i+=2) {
//            // 入力信号にフィルタを適用し、出力信号として書き出す。
//            output[i] = (byte) (b0 / a0 * input[i] + b1 / a0 * in1 + b2 / a0 * in2 - a1 / a0 * out1 - a2 / a0 * out2);
//
//            in2 = in1; // 2つ前の入力信号を更新
//            in1 = input[i]; // 1つ前の入力信号を更新
//
//            out2 = out1; // 2つ前の出力信号を更新
//            out1 = output[i]; // 1つ前の出力信号を更新
//        }
//    }

    public void callWaveRepaint(byte[] data) {
        if (waveRepaintListener == null) {
            return;
        }

        try {
            System.arraycopy(data, 0, displayBuf, 0, data.length);
            waveRepaintListener.repaintWave(displayBuf);
        }
        catch (Exception e) {
        }
    }

    protected void exit() { // 再生終了
        isRunnable = false;
    }

    /**
     * ボリュームの設定
     *
     * @param volume
     *            0.0 ~ 1.0
     */
    public void setVolume(float volume) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }

            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            double max = Math.pow(10.0, control.getMaximum() / 20.0);
            double min = Math.pow(10.0, control.getMinimum() / 20.0);
            double newValue = (max - min) * (volume * volume) + min;
            newValue = 20 * Math.log(newValue) / Math.log(10);
            control.setValue((float) newValue);
        }
    }

    /**
     * pan設定処理
     *
     * @param pan
     *            設定する pan 0～127
     */
    public void setPan(int pan) {
        try {
            // 0 ~ 64 ~ 127
            // -1 ~ 0 ~ 1

            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.PAN);
            float _pan = ((float) pan - 64) / 127;
            control.setValue(_pan);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * リバーブ設定処理
     *
     * @param reverb
     *            リバーブの設定をします 0～127
     *
     */
    public void setReverb_ret(int reverb) {
        try {
            // 0 ~ 64 ~ 127
            // -1 ~ 0 ~ 1

            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.REVERB_RETURN);

            float _reverb = ((float) reverb - 64) / 127;

            // System.out.println("REVERB_RETURN "+_reverb +" " + reverb +" ");
            control.setValue(_reverb);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getChannel() {
        return channel;
    }

    public boolean checkChannel(int ch) {
        int channel = ch;
        if (channel > tones.length) {
            return false;
        }
        if (channel != this.channel) {
            return false;
        }
        return true;
    }

    public void noteOn(int ch, int note, int velocity) {
        try {
            if (velocity > 0) {
                if (tones[note] != null && tones[note].isReleaseFlag() == true) {
                    // リリースの途中破棄
                    Tone tone = tones[note];
                    activeTones.remove(tone);
                    tonePool.push(tone);
                    tone.setReleaseFlag(false);
                    tone.setVelocity(0);
                    tones[note] = null;
                    if (!oscillator.isToneSync()) {
                        tone.setTablePointer(0);
                    }
                }
                if (!tonePool.empty() && tones[note] == null) {
                    Tone t = tonePool.pop();
                    t.setNote(note);
                    t.setReleaseFlag(false);
                    t.resetEnveropeOffset();
                    t.setVelocity(velocity);
                    t.setStartMills();
                    activeTones.add(t);
                    tones[note] = t;
                }
            }
            else {
                noteOff(ch, note);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void noteOff(int ch, int note) {
        Tone tone = tones[note];
        if (tone != null) {
            if ((envelope.getReleaseTime() > 0.0) && (tone.isReleaseFlag() == false)) {
                tone.setReleaseFlag(true);
                tone.setStartMills();
            }
            else {
                tone.setVelocity(0);
                tone.setReleaseFlag(false);
                activeTones.remove(tone);
                tonePool.push(tone);
                tones[note] = null;
                if (!oscillator.isToneSync()) {
                    tone.setTablePointer(0);
                }
            }
        }
    }

    public void pitchBend(int ch, int pitch) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            tone.setPitch((float) (pitch * pitch_sc) / 8191);//
            // 16382で半音あがる
        }
    }

    public void setExpression(int ch, int exp) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null)
                continue;
            tone.setExpression(exp);
        }
        for (int i = 0; i < tonePool.size(); i++) {
            Tone tone = tonePool.get(i);
            if (tone == null)
                continue;
            tone.setExpression(exp);
        }
    }

    public void pitchBendSenc(int ch, int sc) {
        pitch_sc = sc;
    }

    public void setNRPN(int ch, int nRPN) {
        NRPN = nRPN;
    }

    public int getNRPN(int ch) {
        return NRPN;
    }

    public void resetAllController(int ch) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            tone.reset();
        }
        for (int i = 0; i < tonePool.size(); i++) {
            Tone tone = tonePool.get(i);
            if (tone == null)
                continue;
            tone.reset();
        }
        this.setVolume(1);
    }

    public void allNoteOff(int ch) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            tone.reset();
        }
    }

    public void setVibratoRate(int ch, int rate) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            // tone.setVibratoRate((rate - 64) / 64);
        }
    }

    public void setVibratoDepth(int ch, int depth) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            tone.setVibratoDepth(depth - 64);
        }
    }

    public void setVibratoDelay(int ch, int delay) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            // tone.setVibratoDelay(delay - 64);
        }
    }

    public void setVariation(int ch, int val) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            // tone.setVariation(val);
        }
        for (int i = 0; i < tonePool.size(); i++) {
            Tone tone = tonePool.get(i);
            if (tone == null) {
                continue;
            }
            // tone.setVariation(val);
        }
    }

    public IWaveRepaintListener getWaveRepaintListener() {
        return waveRepaintListener;
    }

    public void setWaveRepaintListener(IWaveRepaintListener waveRepaintListener) {
        this.waveRepaintListener = waveRepaintListener;
    }

    public void setOscillator(IOscillator oscillator) {
        this.oscillator = oscillator;
    }

    public WaveType getWaveType() {
        return getWaveType(0);
    }

    public WaveType getWaveType(int ch) {
        return waveType;
    }

    public void setOscillator(WaveType oscType) {
        this.waveType = oscType;
        setOscillator(oscMap.get(oscType));
    }

    @Override
    public void setOscillator(int ch, WaveType oscType) {
        setOscillator(oscType);
    }

    @Override
    public void setPan(int ch, int pan) {
        setPan(pan);
    }

    @Override
    public void setVolume(int ch, float volume) {
        setVolume(volume);
    }

    @Override
    public void openDevice() {
        start();
    }

    @Override
    public void closeDevice() {
        exit();
    }

    @Override
    public Envelope getEnvelope(int ch) {
        return envelope;
    }

    public Envelope getEnvelope() {
        return getEnvelope(0);
    }
    @Override
    public void systemReset() {
        pitch_sc = 2;
        allNoteOff(0);
        resetAllController(0);
    }

}
