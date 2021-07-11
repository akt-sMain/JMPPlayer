package jmp.midi.receiver;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

class NoneReceiverCreator extends ReceiverCreator {

    class NoneReceiver implements Receiver {

        @Override
        public void send(MidiMessage message, long timeStamp) {
        }

        @Override
        public void close() {
        }

    }

    @Override
    public Receiver getReciever() throws MidiUnavailableException {
        return new NoneReceiver();
    }

}
