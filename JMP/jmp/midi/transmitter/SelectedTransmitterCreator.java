package jmp.midi.transmitter;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

import jmp.core.JMPCore;

class SelectedTransmitterCreator extends TransmitterCreator {
    private String selected = "";

    public SelectedTransmitterCreator(String selected) {
        this.selected = selected;
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        MidiDevice outDev = null;
        Transmitter outTransmitter = null;
        MidiDevice.Info transInfo = null;
        for (MidiDevice.Info info : JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(true, false)) {
            if (info.getName().equals(selected) == true) {
                transInfo = info;
                break;
            }
        }
        if (transInfo != null) {
            outDev = MidiSystem.getMidiDevice(transInfo);
            if (outDev.isOpen() == false) {
                outDev.open();
            }
            outTransmitter = outDev.getTransmitter();
        }
        else {
            NoneTransmitterCreator creator = new NoneTransmitterCreator();
            outTransmitter = creator.getTransmitter();
        }
        return outTransmitter;
    }

}
