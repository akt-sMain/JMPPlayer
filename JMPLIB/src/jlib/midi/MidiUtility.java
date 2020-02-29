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
     * NoteOnイベントか判定
     *
     * @param sMes
     * @return
     */
    public static boolean isNoteOn(ShortMessage sMes) {
        if ((sMes.getCommand() == ShortMessage.NOTE_ON) && (sMes.getData2() > 0)) {
            return true;
        }
        return false;
    }

    /**
     * NoteOffイベントか判定
     *
     * @param sMes
     * @return
     */
    public static boolean isNoteOff(ShortMessage sMes) {
        if ((sMes.getCommand() == ShortMessage.NOTE_OFF)
                || (sMes.getCommand() == ShortMessage.NOTE_ON && sMes.getData2() <= 0)) {
            return true;
        }
        return false;
    }

    /**
     * PitchBendイベントか判定
     *
     * @param sMes
     * @return
     */
    public static boolean isPitchBend(ShortMessage sMes) {
        if (sMes.getCommand() == ShortMessage.PITCH_BEND) {
            return true;
        }
        return false;
    }

    /**
     * PitchBendイベントをシフト量に変換する
     *
     * @param sMes
     * @return
     */
    public static int convertPitchBendValue(ShortMessage sMes) {
        int lsb = sMes.getData1();
        int msb = sMes.getData2();
        int ret = 0x00;
        int bit = 7;
        // 先にMSBをシフトする
        ret = msb << bit;

        int mask = (8 - bit) >> 0xff;
        int pbValue = (ret | (lsb & mask));
        return pbValue;
    }
}
