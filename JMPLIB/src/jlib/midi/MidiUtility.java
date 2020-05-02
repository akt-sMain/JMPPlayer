package jlib.midi;

import javax.sound.midi.ShortMessage;

/**
 * MIDI解析系の補助メソッド群
 *
 * @author abs
 *
 */
public class MidiUtility {
    /**
     * PitchBendイベントをシフト量に変換する
     *
     * @param sMes
     * @return
     */
    public static int convertPitchBendValue(ShortMessage sMes) {
        int lsb = sMes.getData1();
        int msb = sMes.getData2();
        int pbValue = MidiByte.mergeLsbMsbValue(lsb, msb);
        return pbValue;
    }
}
