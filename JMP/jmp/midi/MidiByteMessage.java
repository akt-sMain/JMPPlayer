package jmp.midi;

import javax.sound.midi.MidiMessage;

/**
 * Byte配列を直接指定可能なMidiMessage
 *
 * @author akt
 *
 */
public class MidiByteMessage extends MidiMessage {

    public MidiByteMessage(byte[] data) {
        super(data);
    }

    @Override
    public Object clone() {
        byte[] newData = new byte[length];
        System.arraycopy(data, 0, newData, 0, newData.length);

        MidiByteMessage msg = new MidiByteMessage(newData);
        return msg;
    }

}
