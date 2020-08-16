package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jlib.midi.IMidiEventListener;
import jmp.core.JMPCore;

/**
 * MIDI_IN用Receiverのラッパークラス
 *
 * @author akkut
 *
 */
public class MIReceiver implements Receiver {

    Receiver abstractReciever = null;

    public MIReceiver(Receiver abstractReciever) {
        this.abstractReciever = abstractReciever;
    };

    public void changeAbsReceiver(Receiver reciever) {
        this.abstractReciever = reciever;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (abstractReciever == null) {
            return;
        }

        if (JMPCore.getSoundManager().filter(message, IMidiEventListener.SENDER_MIDI_IN) == false) {
            return;
        }
        abstractReciever.send(message, timeStamp);

        JMPCore.getPluginManager().catchMidiEvent(message, timeStamp, IMidiEventListener.SENDER_MIDI_IN);
    }

    @Override
    public void close() {
        if (abstractReciever != null) {
            abstractReciever.close();
        }
    }

}
