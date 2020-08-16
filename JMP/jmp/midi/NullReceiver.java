package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jlib.midi.IMidiEventListener;
import jmp.core.JMPCore;

/**
 * プラグインのみにMIDIメッセージを転送する
 *
 * @author akkut
 *
 */
public class NullReceiver implements Receiver {

    public NullReceiver() {
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (JMPCore.getSoundManager().filter(message, IMidiEventListener.SENDER_MIDI_OUT) == false) {
            return;
        }

        JMPCore.getPluginManager().catchMidiEvent(message, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
    }

    @Override
    public void close() {
    }

}
