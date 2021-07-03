package jmp.midi.receiver;

import jmp.core.SystemManager;

public class ReceiverFactory {
    public ReceiverFactory() {
    };

    public ReceiverCreator create(String name) {
        if (name.equals("") == true || name.isEmpty() == true) {
            return new AutoSerectSynthReceiverCreator();
        }
        else if (name.equals(SystemManager.JMSYNTH_LIB_NAME) == true) {
            return new BuiltinSynthReceiverCreator();
        }
        else {
            return new SelectedSynthReceiverCreator(name);
        }
    }
}
