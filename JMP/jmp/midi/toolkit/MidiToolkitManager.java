package jmp.midi.toolkit;

import java.util.ArrayList;
import java.util.List;

import jlib.midi.IMidiToolkit;

public class MidiToolkitManager {

    public static final String DEFAULT_MIDI_TOOLKIT_NAME = DefaultMidiToolkit.class.getSimpleName();

    private static DefaultMidiToolkit DefaultMidiToolkit = new DefaultMidiToolkit();
    private static List<IMidiToolkit> lst = new ArrayList<IMidiToolkit>() {
        {
            add(DefaultMidiToolkit);
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
        return DefaultMidiToolkit;
    }

}
