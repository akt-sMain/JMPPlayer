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

    private List<PluginWrapper> accessor = null;

    public PluginObserver() {
        aPlugins = new HashMap<String, PluginWrapper>();
        accessor = new ArrayList<PluginWrapper>();
        aPlugins.clear();
        accessor.clear();
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
            accessor.add(pw);
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

    public int getNumberOfPlugin() {
        return aPlugins.size();
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

    public Collection<PluginWrapper> getPlugins() {
        return accessor;
    }

    @Override
    public void initialize() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().initialize();
        }
    }

    @Override
    public void exit() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().exit();
        }
    }

    @Override
    public void open() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().open();
        }
    }

    @Override
    public void close() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().close();
        }
    }

    @Override
    public void update() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().update();
        }
    }

    @Override
    public void startSequencer() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            IPlugin p = pluginWrap.getPlugin();
            if (p instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) p;
                pi.startSequencer();
            }
        }
    }

    @Override
    public void stopSequencer() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            IPlugin p = pluginWrap.getPlugin();
            if (p instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) p;
                pi.stopSequencer();
            }
        }
    }

    @Override
    public void updateTickPosition(long before, long after) {
        for (PluginWrapper pluginWrap : getPlugins()) {
            IPlugin p = pluginWrap.getPlugin();
            if (p instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) p;
                pi.updateTickPosition(before, after);
            }
        }
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        for (PluginWrapper pluginWrap : getPlugins()) {
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
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().notifyUpdateCommonRegister(key);
        }
    }

    @Override
    public void notifyUpdateConfig(String key) {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.getPlugin().notifyUpdateConfig(key);
        }
    }

}
