package jmp.plugin;

import java.io.File;

import javax.sound.midi.MidiMessage;

import function.Utility;
import jlib.midi.IMidiEventListener;
import jlib.player.IPlayerListener;
import jlib.plugin.IPlugin;
import jlib.plugin.ISupportExtensionConstraints;

public class PluginWrapper implements IPlugin, IPlayerListener, IMidiEventListener {
    private final static IPlugin DUMMY_PLG = new IPlugin() {

        @Override
        public void open() {
        }

        @Override
        public void initialize() {
        }

        @Override
        public void exit() {
        }

        @Override
        public void close() {
        }

        @Override
        public boolean isEnable() {
            return false;
        }
    };
    private final static IPlayerListener DUMMY_PLAYER_LISTENER = new IPlayerListener() {

        @Override
        public void startSequencer() {
        }

        @Override
        public void stopSequencer() {
        }

        @Override
        public void updateTickPosition(long before, long after) {
        }
    };
    private final static IMidiEventListener DUMMY_MIDI_EVENT_LISTENER = new IMidiEventListener() {

        @Override
        public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        }
    };

    public static enum PluginState {
        CONNECTED,
        DISCONNECTED,
        INVALID;
    }

    public static PluginState toPluginState(String str) {
        if (str.equalsIgnoreCase("INVALID") == true) {
            return PluginState.INVALID;
        }
        else if (str.equalsIgnoreCase("DISCONNECTED") == true) {
            return PluginState.DISCONNECTED;
        }
        else {
            return PluginState.CONNECTED;
        }
    }
    public static String toString(PluginState pStat) {
        switch (pStat) {
            case DISCONNECTED:
                return "DISCONNECTED";
            case INVALID:
                return "INVALID";
            case CONNECTED:
            default:
                return "CONNECTED";
        }
    }

    /* ファイルがサポートする拡張子か判定する */
    public static boolean isSupportExtension(File file, ISupportExtensionConstraints sec) {
        boolean ret = false;
        if (sec != null) {
            String[] allowsEx = sec.allowedExtensionsArray();
            for (String ae : allowsEx) {
                if (Utility.checkExtension(file, ae) == true) {
                    ret = true;
                    break;
                }
            }
        }
        else {
            ret = true;
        }
        return ret;
    }

    private IPlugin plugin = null;
    private IPlayerListener playerListener = null;
    private IMidiEventListener midiEventListener = null;
    private ISupportExtensionConstraints supportExtensionConstraints = null;
    private PluginState state = PluginState.CONNECTED;

    public PluginWrapper() {
        toInvalidPlugin();
        this.state = PluginState.INVALID;
    }

    public PluginWrapper(IPlugin plg) {
        setInterface(plg);
        this.state = PluginState.CONNECTED;
    }

    public void setInterface(IPlugin plg) {
        this.plugin = plg;

        if (plg instanceof IPlayerListener) {
            this.playerListener = (IPlayerListener)plg;
        }
        else {
            this.playerListener = null;
        }

        if (plg instanceof IMidiEventListener) {
            this.midiEventListener = (IMidiEventListener)plg;
        }
        else {
            this.midiEventListener = null;
        }

        if (plg instanceof ISupportExtensionConstraints) {
            this.supportExtensionConstraints = (ISupportExtensionConstraints)plg;
        }
        else {
            this.supportExtensionConstraints = null;
        }
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }

    public boolean equalsPlugin(IPlugin plg) {
        if (plg == this) {
            // 自分自身をさすか？
            return true;
        }
        else if (plugin == plg) {
            // Pluginと比較
            return true;
        }
        return false;
    }

    public void toInvalidPlugin() {
        this.plugin = DUMMY_PLG;
        this.playerListener = DUMMY_PLAYER_LISTENER;
        this.midiEventListener = DUMMY_MIDI_EVENT_LISTENER;
        this.supportExtensionConstraints = null;
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        if (midiEventListener != null) {
            midiEventListener.catchMidiEvent(message, timeStamp, senderType);
        }
    }
    @Override
    public void startSequencer() {
        if (playerListener != null) {
            playerListener.startSequencer();
        }
    }
    @Override
    public void stopSequencer() {
        if (playerListener != null) {
            playerListener.stopSequencer();
        }
    }
    @Override
    public void updateTickPosition(long before, long after) {
        if (playerListener != null) {
            playerListener.updateTickPosition(before, after);
        }
    }
    @Override
    public void initialize() {
        plugin.initialize();
    }
    @Override
    public void exit() {
        plugin.exit();
    }
    @Override
    public void open() {
        plugin.open();
    }
    @Override
    public void close() {
        plugin.close();
    }
    @Override
    public boolean isEnable() {
        return plugin.isEnable();
    }
    @Override
    public boolean isOpen() {
        return plugin.isOpen();
    }
    @Override
    public void loadFile(File file) {
        plugin.loadFile(file);
    }
    @Override
    public void notifyUpdateCommonRegister(String key) {
        plugin.notifyUpdateCommonRegister(key);
    }
    @Override
    public void notifyUpdateConfig(String key) {
        plugin.notifyUpdateConfig(key);
    }
    @Override
    public void update() {
        plugin.update();
    }

    public final ISupportExtensionConstraints getSupportExtensionConstraints() {
        return this.supportExtensionConstraints;
    }
}
