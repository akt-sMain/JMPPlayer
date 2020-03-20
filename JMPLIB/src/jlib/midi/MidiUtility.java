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
        if ((sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF)
                || (sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && sMes.getData2() <= 0)) {
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
        if (sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND) {
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
        int pbValue = MidiByte.mergeLsbMsbValue(lsb, msb);
        return pbValue;
    }
}
