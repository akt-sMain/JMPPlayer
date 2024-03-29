package jmsynth.sound;

import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import jmsynth.app.component.IWaveRepaintListener;
import jmsynth.envelope.Envelope;
import jmsynth.modulate.Modulator;
import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorConfig;
import jmsynth.oscillator.OscillatorManager;
import jmsynth.oscillator.OscillatorSet;

public class SoundSourceChannel extends Thread implements ISynthController {
    public static final float SAMPLE_RATE = 44100.0f; // サンプルレート
    // public static final float SAMPLE_RATE = 22050.0f; // サンプルレート
    // public static final float SAMPLE_RATE = 11025.0f; // サンプルレート

    public static final boolean SAMPLE_16BITS = true;
    public static final int SAMPLE_SIZE = SAMPLE_16BITS ? 16 : 8;
    public static final int CHANNEL = 2;
    public static final int FRAME_SIZE = CHANNEL * (SAMPLE_SIZE / 8);
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;

    // 1フレームで再生するバイト数
    public final static int BUF_SIZE = ((int) SAMPLE_RATE / 50 * FRAME_SIZE);

    // 再生フォーマット
    private AudioFormat audioFormat;

    // 出力ライン
    public SourceDataLine line = null;

    private byte[] displayBuf = null;

    private int channel;
    public boolean isRunnable;

    private OscillatorConfig oscConfig;

    private int NRPN = 0;
    private double pitch_sc = 2.0;
    
    /** 音声オブジェクトのインスタンス管理 */
    protected Tone[] toneInstances = null;

    /** アクティブな音声を管理 */
    protected Vector<Tone> activeTones = null;

    /** 発声中の音色を管理するためのテーブル */
    protected Tone[] playingTones = null;

    /** 利用可能な音色をプールするためのクラス */
    protected Stack<Tone> tonePool = null;

    private IWaveRepaintListener waveRepaintListener = null;

    private OscillatorManager oscManager = null;

    protected Envelope envelope = null;
    protected Modulator modulator = null;

    public SoundSourceChannel(int channel, IOscillator oscType, int polyphony, Envelope envelope, Modulator modulator) {
        init(channel, oscType, polyphony, envelope, modulator);
    }

    public SoundSourceChannel(int channel, IOscillator oscType, int polyphony, Envelope envelope) {
        init(channel, oscType, polyphony, envelope, null);
    }

    public SoundSourceChannel(int channel, IOscillator oscType, int polyphony) {
        init(channel, oscType, polyphony, null, null);
    }

    private void init(int channel, IOscillator oscType, int polyphony, Envelope envelope, Modulator modulator) {
        this.audioFormat = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNEL, SIGNED, BIG_ENDIAN);

        this.channel = channel;

        this.oscConfig = new OscillatorConfig();
        
        this.oscManager = new OscillatorManager();
        this.oscManager.addOscillator(oscType);

        // ライン情報取得
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, this.audioFormat, BUF_SIZE);
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
        toneInstances = new Tone[polyphony];
        playingTones = new Tone[128];
        
        // Toneインスタンス作成
        for (int i = 0; i < polyphony; i++) {
            toneInstances[i] = new Tone();
        }

        this.envelope = envelope;
        if (this.envelope != null) {
            this.envelope.setTargetTones(toneInstances);
        }

        this.modulator = modulator;
        if (this.modulator != null) {
            this.modulator.setTargetTones(toneInstances);
        }

        /* 音階データ生成 */
        for (int i = 0; i < polyphony; i++) {
            try {
                Tone rw = toneInstances[i];
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
                oscManager.makeTone(data, length, tone, this.oscConfig);

                if (envelope.getReleaseTime() > 0.0) {
                    // リリース処理
                    if (tone.isReleaseFlag() == true && tone.getEnvelopeOffset() <= 0.0) {
                        int note = tone.getNote();
                        noteOffImpl(note);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException aiobe) {
                // ArrayIndexOutOfBoundsExceptionは起きがち
                // スレッドのタイミング次第なためとりあえず無視...
                // aiobe.printStackTrace();
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

    // int[] samples = new int[BUF_SIZE]; // 波形データ
    // static final int AVERAGE_SAMPLE_COUNT = 10;
    //
    // public int samplesOfAverage(byte[] data, int length) {
    // int newLength = length;
    // for (int i = 0; i < samples.length; i++) {
    // int avgSample = 0;
    // int sampleCount = 0;
    // for (int a = 0; a < AVERAGE_SAMPLE_COUNT; a++) {
    // if ((i + a) >= data.length) {
    // break;
    // }
    // avgSample += data[i + a];
    // sampleCount++;
    // }
    // avgSample /= sampleCount;
    // samples[i] = avgSample;
    // }
    //
    // for (int i = 0; i < newLength; i++) {
    // data[i] = (byte) (samples[i] & 0xff);
    // }
    // return newLength;
    // }

    /**
     * バッファ再生処理
     */
    private static int REPAINT_CYCLE = 2;
    private int repWait = 0;

    public void run() {
        isRunnable = true;
        line.start();
        byte[] waveData = new byte[BUF_SIZE]; // 波形データ
        displayBuf = new byte[BUF_SIZE]; // 波形データ

        int sampleRate = BUF_SIZE;
        while (isRunnable) {
            try {
                int length = makeTone(waveData, sampleRate); // 再生するたびに作り直す
                // samplesOfAverage(waveData, length);

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
    
    public FloatControl getFloatControl(FloatControl.Type type) {
        FloatControl control = (FloatControl) line.getControl(type);
        return control;
    }
    
    public double convertFloatControlValue(float volume, float maximum, float minimum) {
        if (volume > 1.0f)
            volume = 1.0f;
        else if (volume < 0.0f)
            volume = 0.0f;
        double max = Math.pow(10.0, maximum / 20.0);
        double min = Math.pow(10.0, minimum / 20.0);
        double newValue = (max - min) * (volume * volume) + min;
        newValue = 20 * Math.log(newValue) / Math.log(10);
        return newValue;
    }

    /**
     * ボリュームの設定
     *
     * @param volume
     *            0.0 ~ 1.0
     */
    public void setVolume(float volume) {
        FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        double newValue = convertFloatControlValue(volume, control.getMaximum(), control.getMinimum());
        control.setValue((float) newValue);
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
            if (pan < 0) {
                pan = 0;
            }
            else if (pan > 127) {
                pan = 127;
            }

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
            if (reverb < 0) {
                reverb = 0;
            }
            else if (reverb > 127) {
                reverb = 127;
            }

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
        if (channel > playingTones.length) {
            return false;
        }
        if (channel != this.channel) {
            return false;
        }
        return true;
    }

    public void noteOn(int ch, int note, int velocity) {
        if (note < 0 || 127 < note) {
            return;
        }
        
        try {
            if (velocity > 0) {
                if (playingTones[note] != null) {
                    // 重複音声
                    if (playingTones[note].isReleaseFlag() == true) {
                        // リリースの途中破棄
                        Tone tone = playingTones[note];
                        activeTones.remove(tone);
                        tonePool.push(tone);
                        tone.setReleaseFlag(false);
                        tone.setVelocity(0);
                        playingTones[note] = null;
                        if (!oscManager.isToneSync()) {
                            tone.setTablePointer(0);
                        }
                    }
                    else {
                        // 単純にNoteOffを送る
                        noteOffImpl(note);
                    }
                }
                if (!tonePool.empty() && playingTones[note] == null) {
                    Tone t = tonePool.pop();
                    t.setNote(note);
                    t.setReleaseFlag(false);
                    t.resetEnvelopeOffset();
                    t.setVelocity(velocity);
                    t.setStartMills();
                    activeTones.add(t);
                    playingTones[note] = t;
                }
            }
            else {
                noteOffImpl(note);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void noteOff(int ch, int note) {
        noteOffImpl(note);
    }

    protected void noteOffImpl(int note) {
        if (note < 0 || 127 < note) {
            return;
        }
        
        Tone tone = playingTones[note];
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
                playingTones[note] = null;
                if (!oscManager.isToneSync()) {
                    tone.setTablePointer(0);
                }
            }
        }
    }

    public void pitchBend(int ch, int pitch) {
        for (int i = 0; i < toneInstances.length; i++) {
            Tone tone = toneInstances[i];
            tone.setPitch((double) ((double)pitch * pitch_sc) / 8191.0);//
            // 16382で半音あがる
        }
    }

    public void setExpression(int ch, int exp) {
        for (int i = 0; i < toneInstances.length; i++) {
            Tone tone = toneInstances[i];
            tone.setExpression(exp);
        }
    }

    public void pitchBendSenc(int ch, int sc) {
        pitch_sc = (double)sc;
    }
    
    public double getPitchBendSenc() {
        return pitch_sc;
    }

    public void setNRPN(int ch, int nRPN) {
        NRPN = nRPN;
    }

    public int getNRPN(int ch) {
        return NRPN;
    }

    public void resetAllController(int ch) {
        // 発音中の音源を元に戻す  
        for (int i = 0; i < toneInstances.length; i++) {
            Tone tone = toneInstances[i];
            tone.reset();
        }
        
        setModulationDepth(ch, 0);
        setNRPN(ch, 0);
    }

    @Override
    public void allSoundOff(int ch) {
        /* 音源を強制的に破棄する */
        for (int i = 0; i < playingTones.length; i++) {
            Tone tone = playingTones[i];
            if (tone != null) {
                tone.setVelocity(0);
                tone.setReleaseFlag(false);
                activeTones.remove(tone);
                tonePool.push(tone);
                playingTones[i] = null;
                if (!oscManager.isToneSync()) {
                    tone.setTablePointer(0);
                }
            }
        }
    }

    public void allNoteOff(int ch) {
        for (int i = 0; i < activeTones.size(); i++) {
            Tone tone = activeTones.get(i);
            if (tone == null) {
                continue;
            }
            if (playingTones[tone.getNote()] != null) {
                // 発声中の音声を止める
                if ((envelope.getReleaseTime() > 0.0) && (tone.isReleaseFlag() == false)) {
                    tone.setReleaseFlag(true);
                }
                noteOffImpl(tone.getNote());
            }
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
            //tone.setVibratoDepth(depth - 64);
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

    public IOscillator getOscillator(int index) {
        return getOscillator(0, index);
    }

    public IOscillator getOscillator(int ch, int index) {
        return oscManager.getOscillator(index);
    }

    @Override
    public void addOscillator(int ch, IOscillator osc) {
        oscManager.addOscillator(osc);
    }
    
    @Override
    public void clearOscillator(int ch) {
        oscManager.clearWave();
    }
    
    @Override
    public OscillatorSet getOscillatorSet(int ch) {
        OscillatorSet oscSet = new OscillatorSet();
        for (int i = 0; i < oscManager.getOscillatorCount(); i++) {
            oscSet.addOscillators(oscManager.getOscillator(i));
        }
        return oscSet;
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
        // Expressionを0にすることで初期化中の音声レベルを0にする
        setExpression(0, 0);

        pitch_sc = 2;
        pitchBend(0, 0);
        allSoundOff(0);
        resetAllController(0);
        setNRPN(0, 0);
        setPan(0, 64);
        setModulationDepth(0, 0);
        setVolume(0, 1.0f);

        // Expressionは最後に初期化
        setExpression(0, 127);
    }

    @Override
    public void setModulationDepth(int ch, int depth) {
        if (modulator != null) {
            modulator.setDepth(depth);
        }
    }

    public Modulator getModulator() {
        return getModulator(0);
    }

    @Override
    public Modulator getModulator(int ch) {
        return modulator;
    }

    public void setWaveReverse(boolean isReverse) {
        setWaveReverse(0, isReverse);
    }

    @Override
    public void setWaveReverse(int ch, boolean isReverse) {
        this.oscConfig.setWaveReverse(isReverse);
    }

    public boolean isWaveReverse() {
        return isWaveReverse(0);
    }

    @Override
    public boolean isWaveReverse(int ch) {
        return this.oscConfig.isWaveReverse();
    }

    public void setValidNesSimulate(boolean isValidNesSimulate) {
        setValidNesSimulate(0, isValidNesSimulate);
    }

    @Override
    public void setValidNesSimulate(int ch, boolean isValidNesSimulate) {
        this.oscConfig.setValidNesSimulate(isValidNesSimulate);
    }

    public boolean isValidNesSimulate() {
        return isValidNesSimulate(0);
    }

    @Override
    public boolean isValidNesSimulate(int ch) {
        return this.oscConfig.isValidNesSimulate();
    }
    
    public int getNumOfTones() {
        int cnt = 0;
        for (int i=0; i<playingTones.length; i++) {
            if (playingTones[i] != null) {
                cnt++;
            }
        }
        return cnt;
    }
    
    public int getNumOfActiveTone() {
        return activeTones.size();
    }
    
    public int getNumOfTonePool() {
        return tonePool.size();
    }
    
    public double getTonePitch() {
        double val = 0.0;
        try {
            Tone t = toneInstances[0];
            val = t.getPitch();
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public int getToneExpression() {
        int val = 0;
        try {
            Tone t = toneInstances[0];
            val = t.getExpression();
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public int getToneVelocity() {
        int val = 0;
        try {
            for (int i=0; i<playingTones.length; i++) {
                Tone t = playingTones[i];
                if (t != null) {
                    val = t.getVelocity();
                    break;
                }
            }
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public double getToneEnvelopeOffset() {
        double val = 0;
        try {
            for (int i=0; i<playingTones.length; i++) {
                Tone t = playingTones[i];
                if (t != null) {
                    val = t.getEnvelopeOffset();
                    break;
                }
            }
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public int getToneOverallLevel() {
        int val = 0;
        try {
            for (int i=0; i<playingTones.length; i++) {
                Tone t = playingTones[i];
                if (t != null) {
                    val = t.getOverallLevel();
                    break;
                }
            }
        }
        catch(Exception e) {
        }
        return val;
    }

}
