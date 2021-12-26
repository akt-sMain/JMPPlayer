package jmp.midi.receiver;

import jmp.core.SoundManager;
import jmp.core.SystemManager;

public class ReceiverFactory {
    public ReceiverFactory() {
    };

    public ReceiverCreator create(String name) {
        if (name.equals("") == true || name.isEmpty() == true) {
            return new AutoSelectSynthReceiverCreator();
        }
        else if (name.equals(SystemManager.JMSYNTH_LIB_NAME) == true) {
            return new BuiltinSynthReceiverCreator();
        }
        else if (name.equals(SoundManager.NULL_RECEIVER_NAME) == true) {
            return new NoneReceiverCreator();
        }
        else {
            return new SelectedSynthReceiverCreator(name);
        }
    }
}
