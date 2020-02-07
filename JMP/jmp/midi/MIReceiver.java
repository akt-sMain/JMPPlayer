package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jlib.IMidiEventListener;
import jmp.JMPCore;

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

	abstractReciever.send(message, timeStamp);

	JMPCore.getPluginManager().catchMidiEvent(message, timeStamp, IMidiEventListener.SENDER_MIDI_IN);
    }

    @Override
    public void close() {
	abstractReciever.close();
    }

}
