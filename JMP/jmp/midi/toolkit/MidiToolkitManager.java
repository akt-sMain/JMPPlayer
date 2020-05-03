package jmp.midi.toolkit;

import java.util.ArrayList;
import java.util.List;

import jlib.midi.IMidiToolkit;

public class MidiToolkitManager {

    private static DefaultMidiToolkit defaultMidiToolkit = new DefaultMidiToolkit();
    private static List<IMidiToolkit> lst = new ArrayList<IMidiToolkit>() {
        {
            add(defaultMidiToolkit);
        }
    };

    private static MidiToolkitManager instance = new MidiToolkitManager();

    private MidiToolkitManager() {
    }

    public static MidiToolkitManager getInstance() {
        return instance;
    }

    public IMidiToolkit getMidiToolkit(String className) {
        for (IMidiToolkit kit : lst) {
            if (kit.getClass().getSimpleName().equals(className)) {
                return kit;
            }
        }
        return defaultMidiToolkit;
    }

    public IMidiToolkit getDefaultMidiToolkit() {
        return getMidiToolkit(DefaultMidiToolkit.class.getSimpleName());
    }

}
