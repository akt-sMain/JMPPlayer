package jmsynth.oscillator;

import java.util.ArrayList;
import java.util.List;

import jmsynth.oscillator.IOscillator.WaveType;

public class OscillatorSet {

    // 複数のオシレータ設計を考慮（出力側は未実装）
    private List<WaveType> oscs = null;

    public OscillatorSet(WaveType... osc) {
        oscs = new ArrayList<WaveType>();
        for (int i = 0; i < osc.length; i++) {
            oscs.add(osc[i]);
        }
    }

    public WaveType getOscillator() {
        return getOscillator(0);
    }

    public WaveType getOscillator(int index) {
        if (size() <= 0) {
            // 例外
            return WaveType.NOISE;
        }
        return oscs.get(index);
    }

    public int size() {
        return oscs.size();
    }
}
