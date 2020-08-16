package jmp.midi;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * MIDI_IN用Transmitterのラッパークラス
 *
 * @author akkut
 *
 */
public class MITransmitter implements Transmitter {

    private Transmitter abstractTransmitter = null;

    public MITransmitter(Transmitter abstractTransmitter) {
        this.abstractTransmitter = abstractTransmitter;
    }

    public void changeAbsTransmitter(Transmitter transmitter) {
        this.abstractTransmitter = transmitter;
    }

    @Override
    public void setReceiver(Receiver receiver) {
        if (this.abstractTransmitter != null) {
            this.abstractTransmitter.setReceiver(receiver);
        }
    }

    @Override
    public Receiver getReceiver() {
        if (this.abstractTransmitter != null) {
            return this.abstractTransmitter.getReceiver();
        }
        return null;
    }

    @Override
    public void close() {
        if (this.abstractTransmitter != null) {
            this.abstractTransmitter.close();
        }
    }

}
