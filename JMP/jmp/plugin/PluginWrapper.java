package jmp.plugin;

import jlib.plugin.IPlugin;

public class PluginWrapper {
    public static enum PluginState {
        CONNECTED,
        DISCONNECTED,
        INVALID
    }

    private IPlugin plugin = null;
    private PluginState state = PluginState.CONNECTED;

    public PluginWrapper(IPlugin plg) {
        this.plugin = plg;
        this.state = PluginState.CONNECTED;
    }

    public IPlugin getPlugin() {
        return plugin;
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }
}
