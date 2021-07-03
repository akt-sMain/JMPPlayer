package jmp.midi.receiver;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import jmp.core.JMPCore;

class AutoSerectSynthReceiverCreator extends ReceiverCreator {

    @Override
    public Receiver getReciever() {

        MidiDevice.Info[] infosOfRecv = JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(false, true);

        // デフォルト
        int defIndex = -1;
        for (int i = 0; i < infosOfRecv.length; i++) {
            if (infosOfRecv[i].getName().contains("Gervill") == true) {
                defIndex = i;
                break;
            }
        }

        /* デフォルト使用 */
        Receiver reciever = null;
        if (defIndex != -1) {
            // "Gervill"を優先的に使用
            try {
                MidiDevice outDev;
                outDev = MidiSystem.getMidiDevice(infosOfRecv[defIndex]);
                if (outDev.isOpen() == false) {
                    outDev.open();
                }
                reciever = outDev.getReceiver();
            }
            catch (MidiUnavailableException e) {
                reciever = null;
            }
        }
        else {
            // SoundAPIの自動選択に従う
            try {
                reciever = MidiSystem.getReceiver();
            }
            catch (Exception e3) {
                reciever = null;
            }
        }

        // ない場合は内蔵シンセを採用する
        if (reciever == null) {
            BuiltinSynthReceiverCreator builtin = new BuiltinSynthReceiverCreator();
            reciever = builtin.getReciever();
        }
        return reciever;
    }

}
