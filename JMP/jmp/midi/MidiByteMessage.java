package jmp.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

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

    public ShortMessage convertShortMessage() throws InvalidMidiDataException {
        int status = this.data[0];
        int data1 = this.data[1];
        int data2 = this.data[2];
        return new ShortMessage(status, data1, data2);
    }

    @Override
    public Object clone() {
        byte[] newData = new byte[length];
        System.arraycopy(data, 0, newData, 0, newData.length);

        MidiByteMessage msg = new MidiByteMessage(newData);
        return msg;
    }

}
