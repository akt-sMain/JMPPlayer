package jmsynth.oscillator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jmsynth.oscillator.OscillatorSet.WaveType;
import jmsynth.sound.Tone;

public class OscillatorManager extends AbstractWaveGenOscillator {

    private static Map<WaveType, IOscillator> oscMap = new HashMap<WaveType, IOscillator>() {
        {
            put(WaveType.SINE, new SinWaveOscillator());
            put(WaveType.LOW_SINE, new LowSamplingSinWaveOscillator());
            put(WaveType.SAW, new SawWaveOscillator());
            put(WaveType.SQUARE, new SquareWaveOscillator());
            put(WaveType.TRIANGLE, new TriWaveOscillator());
            put(WaveType.PULSE_25, new PulseWaveOscillator(0.25));
            put(WaveType.PULSE_12_5, new PulseWaveOscillator(0.125));
            put(WaveType.LONG_NOISE, new LongNoisWaveOscillator());
            put(WaveType.SHORT_NOISE, new ShortNoisWaveOscillator());
        }
    };

    private static IOscillator toOscillator(WaveType type) {
        if (oscMap.containsKey(type) == false) {
            return oscMap.get(WaveType.SINE);
        }
        return oscMap.get(type);
    }

    private Object mutex = new Object();
    public static final int NUM_OF_OSCILLATOR = 3;
    private List<WaveType> aWaveType;

    public OscillatorManager() {
        aWaveType = new ArrayList<WaveType>();
        
        synchronized (mutex) {
            aWaveType.clear();
        }
    }

    public WaveType getWaveType(int index) {
        if (aWaveType.size() <= 0) {
            return WaveType.NONE;
        }
        return aWaveType.get(index);
    }
    
    public void clearWave() {
        synchronized (mutex) {
            aWaveType.clear();
        }
    }
    
    public int getWaveCount() {
        return aWaveType.size();
    }

    public void addWaveType(WaveType waveType) {
        synchronized (mutex) {
            aWaveType.add(waveType);
        }
    }

    @Override
    public int makeTone(byte[] data, int sampleRate, Tone tone, OscillatorConfig oscConfig) {
        synchronized (mutex) {
            if (getWaveType(0) == WaveType.SHORT_NOISE || getWaveType(0) == WaveType.LONG_NOISE) {
                IOscillator osc = toOscillator(getWaveType(0));
                return osc.makeTone(data, sampleRate, tone, oscConfig);
            }
            return super.makeTone(data, sampleRate, tone, oscConfig);
        }
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        if (getWaveType(0) == WaveType.SHORT_NOISE || getWaveType(0) == WaveType.LONG_NOISE) {
            return 0;
        }
        
        int count = 0;
        int data = 0;
        for (int i = 0; i < getWaveCount(); i++) {
            if (WaveType.NONE != getWaveType(i)) {
                IOscillator osc = toOscillator(getWaveType(i));
                data += osc.makeWave(f, overallLevel, oscConfig);
                count++;
            }
        }
        if (count <= 0) {
            return 0;
        }
        return (byte) (data / count);
    }

}
