package jmp.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import function.Utility;
import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.gui.ui.JMPFrame;
import jmp.lang.DefineLanguage.LangID;

public class PluginManagerDialog extends JMPFrame {
    private static final String[] columnNames = new String[] { "Name", "Opened", "Connection" };

    private DefaultTableModel model;
    private JTable table;
    private JPanel panel;

    /**
     * Create the frame.
     */
    public PluginManagerDialog() {
        super();
        setTitle("Plugin manager");
        setBounds(100, 100, 485, 375);
        setJmpIcon();

        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        scrollPane.setViewportView(table);

        panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    pm.setPluginConnection(name, true);
                }

                updateTable();
            }
        });
        panel.add(btnConnect);

        JButton btnDisconnect = new JButton("Disconnect");
        btnDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    pm.setPluginConnection(name, false);
                }

                updateTable();
            }
        });
        panel.add(btnDisconnect);

        JButton btnEnable = new JButton("Open");
        btnEnable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    IPlugin plg = pm.getPlugin(name);
                    if (plg != null) {
                        if (plg.isEnable() == true) {
                            plg.open();
                        }
                    }
                }

                updateTable();
            }
        });
        panel.add(btnEnable);

        JButton btnDisable = new JButton("Close");
        btnDisable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    IPlugin plg = pm.getPlugin(name);
                    if (plg != null) {
                        plg.close();
                    }
                }

                updateTable();
            }
        });
        panel.add(btnDisable);

        JButton btnReflesh = new JButton("Reflesh");
        btnReflesh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTable(true);
            }
        });
        panel.add(btnReflesh);

        TableColumn col2 = table.getColumnModel().getColumn(1);
        col2.setMaxWidth(80);
        col2.setMinWidth(80);
        TableColumn col3 = table.getColumnModel().getColumn(2);
        col3.setMaxWidth(80);
        col3.setMinWidth(80);

        updateBackColor();
    }

    public void updateTable(boolean clear) {
        PluginManager pm = JMPCore.getPluginManager();
        if (clear == true) {
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }
            for (String name : pm.getPluginsNameSet()) {
                String[] row = new String[model.getColumnCount()];
                row[0] = name;
                model.addRow(row);
            }
        }
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String name = model.getValueAt(i, 0).toString();
            String state = pm.getPlugin(name).isOpen() == true ? "Open" : "Close";
            String connection = pm.isPluginConnection(name) == true ? "Connected" : "Disconnected";
            model.setValueAt(state, i, 1);
            model.setValueAt(connection, i, 2);
        }
    }

    public void updateTable() {
        updateTable(false);
    }

    @Override
    public void showWindow() {
        updateTable(true);
        super.showWindow();
    }

    @Override
    public void updateBackColor() {
        super.updateBackColor();
        getContentPane().setBackground(getJmpBackColor());
        panel.setBackground(getJmpBackColor());
        table.getParent().setBackground(Utility.convertHighLightColor(getJmpBackColor(), 100));
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
        setTitle(JMPCore.getLanguageManager().getLanguageStr(LangID.Plugin_manager));
    }

}
