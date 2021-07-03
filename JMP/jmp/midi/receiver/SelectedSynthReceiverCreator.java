package jmp.midi.receiver;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import jmp.core.JMPCore;

class SelectedSynthReceiverCreator extends ReceiverCreator {

    private String selected = "";

    public SelectedSynthReceiverCreator(String selected) {
        this.selected = selected;
    }

    @Override
    public Receiver getReciever() throws MidiUnavailableException {
        Receiver rec = null;
        MidiDevice.Info recInfo = null;
        MidiDevice inDev = null;
        for (MidiDevice.Info info : JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(false, true)) {
            if (info.getName().equals(selected) == true) {
                recInfo = info;
                break;
            }
        }
        if (recInfo != null) {
            inDev = MidiSystem.getMidiDevice(recInfo);
            if (inDev.isOpen() == false) {
                inDev.open();
            }
            rec = inDev.getReceiver();
        }
        else {
            AutoSerectSynthReceiverCreator cre = new AutoSerectSynthReceiverCreator();
            rec = cre.getReciever();
        }
        return rec;
    }

}
