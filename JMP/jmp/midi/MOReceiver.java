package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jlib.IMidiEventListener;
import jlib.IMidiFilter;
import jlib.Player;
import jlib.manager.PlayerAccessor;
import jmp.JMPCore;

public class MOReceiver implements Receiver {
    Receiver abstractReciever = null;

    public MOReceiver(Receiver abstractReciever) {
	this.abstractReciever = abstractReciever;
    };

    public void changeAbsReceiver(Receiver reciever) {
	this.abstractReciever = reciever;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
	int transpose = JMPCore.getDataManager().getTranspose();
	if (transpose != 0) {
	    Player p = PlayerAccessor.getInstance().getCurrent();
	    if (p instanceof IMidiFilter) {
		if (message instanceof ShortMessage) {
		    IMidiFilter filter = (IMidiFilter) p;
		    ShortMessage sMes = (ShortMessage) message;
		    filter.transpose(sMes, transpose);
		}
	    }
	}

	if (abstractReciever != null) {
	    abstractReciever.send(message, timeStamp);
	}

	JMPCore.getPluginManager().catchMidiEvent(message, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
    }

    @Override
    public void close() {
	if (abstractReciever != null) {
	    abstractReciever.close();
	}
    }

}
