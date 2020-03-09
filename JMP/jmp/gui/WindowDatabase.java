package jmp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import jlib.gui.IJmpWindow;
import jlib.plugin.IPlugin;

public class WindowDatabase {

    // 設定データベース
    private Map<String, IJmpWindow> database = null;

    private List<JMenuItem> pluginMenuItems = null;

    public WindowDatabase() {
        database = new HashMap<String, IJmpWindow>();
        database.clear();

        pluginMenuItems = new ArrayList<JMenuItem>();
        pluginMenuItems.clear();
    }

    public boolean setWindow(String name, IJmpWindow window) {
        boolean ret = true;
        if (database.containsKey(name) == false) {
            database.put(name, window);
        }
        else {
            ret = false;
        }
        return ret;
    }

    public IJmpWindow getWindow(String name) {
        if (database.containsKey(name) == true) {
            return database.get(name);
        }
        else {
            return null;
        }
    }

    public void addPluginMenuItem(String name, IPlugin plugin) {
        for (int i = pluginMenuItems.size() - 1; i >= 0; i--) {
            JMenuItem item = pluginMenuItems.get(i);
            if (item != null) {
                if (item.getText().equalsIgnoreCase(name) == true) {
                    pluginMenuItems.remove(item);
                }
            }
        }

        JMenuItem item = new JMenuItem(name);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (plugin.isOpen() == true) {
                    plugin.close();
                }
                else {
                    plugin.open();
                }
            }
        });
        pluginMenuItems.add(item);
    }

    public List<JMenuItem> getPluginMenuItems() {
        return pluginMenuItems;
    }

}
