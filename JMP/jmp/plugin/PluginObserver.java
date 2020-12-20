package jmp.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiMessage;

import jlib.midi.IMidiEventListener;
import jlib.player.IPlayerListener;
import jlib.plugin.IPlugin;
import jmp.plugin.PluginWrapper.PluginState;

public class PluginObserver implements IPlugin, IPlayerListener, IMidiEventListener {

    /** プラグイン格納用コレクション */
    private Map<String, PluginWrapper> aPlugins = null;

    private List<IPlugin> accessor = null;

    public PluginObserver() {
        aPlugins = new HashMap<String, PluginWrapper>();
        accessor = new ArrayList<IPlugin>();
        aPlugins.clear();
    }

    public boolean addPlugin(String name, IPlugin plugin) {
        return addPlugin(name, plugin, false);
    }

    public boolean addPlugin(String name, IPlugin plugin, boolean isOverwrite) {
        if (aPlugins.containsKey(name) == true) {
            if (isOverwrite == true) {
                PluginWrapper rem = aPlugins.remove(name);
                if (rem != null) {
                    rem.getPlugin().close();
                    rem.getPlugin().exit();
                }
            }
            else {
                return false;
            }
        }
        aPlugins.put(name, new PluginWrapper(plugin));

        accessor.clear();
        for (PluginWrapper pw : aPlugins.values()) {
            accessor.add(pw.getPlugin());
        }
        return true;
    }

    public PluginWrapper getPluginWrapper(String name) {
        if (aPlugins.containsKey(name) == false) {
            return null;
        }
        return aPlugins.get(name);
    }

    public PluginWrapper getPluginWrapper(IPlugin plugin) {
        String name = getPluginName(plugin);
        return getPluginWrapper(name);
    }

    public String getPluginName(IPlugin plugin) {
        String name = "";
        for (String key : aPlugins.keySet()) {
            if (plugin == aPlugins.get(key).getPlugin()) {
                name = key;
            }
        }
        return name;
    }

    public Set<String> getPluginsNameSet() {
        return aPlugins.keySet();
    }

    public IPlugin getPlugin(String name) {
        PluginWrapper pw = getPluginWrapper(name);
        if (pw == null) {
            return null;
        }
        return pw.getPlugin();
    }

    public Collection<IPlugin> getPlugins() {
        return accessor;
    }

    @Override
    public void initialize() {
        for (IPlugin plugin : getPlugins()) {
            plugin.initialize();
        }
    }

    @Override
    public void exit() {
        for (IPlugin plugin : getPlugins()) {
            plugin.exit();
        }
    }

    @Override
    public void open() {
        for (IPlugin plugin : getPlugins()) {
            plugin.open();
        }
    }

    @Override
    public void close() {
        for (IPlugin plugin : getPlugins()) {
            plugin.close();
        }
    }

    @Override
    public void update() {
        for (IPlugin plugin : getPlugins()) {
            plugin.update();
        }
    }

    @Override
    public void startSequencer() {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) plugin;
                pi.startSequencer();
            }
        }
    }

    @Override
    public void stopSequencer() {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) plugin;
                pi.stopSequencer();
            }
        }
    }

    @Override
    public void updateTickPosition(long before, long after) {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) plugin;
                pi.updateTickPosition(before, after);
            }
        }
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        for (PluginWrapper pluginWrap : aPlugins.values()) {
            if (pluginWrap.getState() != PluginState.CONNECTED) {
                continue;
            }

            if (pluginWrap.getPlugin() instanceof IMidiEventListener) {
                IMidiEventListener mi = (IMidiEventListener) pluginWrap.getPlugin();
                mi.catchMidiEvent(message, timeStamp, senderType);
            }
        }
    }

    @Override
    public void notifyUpdateCommonRegister(String key) {
        for (IPlugin plugin : getPlugins()) {
            plugin.notifyUpdateCommonRegister(key);
        }
    }

    @Override
    public void notifyUpdateConfig(String key) {
        for (IPlugin plugin : getPlugins()) {
            plugin.notifyUpdateConfig(key);
        }
    }

}
