package jmp.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import jmp.core.JMPCore;
import jmp.gui.ui.JMPDialog;

public class VersionInfoDialog extends JMPDialog {

    private LicenseReaderDialog licenseDialog = null;

    public VersionInfoDialog() {
        super();
        setResizable(false);
        setBounds(50, 60, 361, 165);
        setTitle("Version");
        getContentPane().setLayout(null);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnClose.setBounds(252, 105, 91, 21);
        getContentPane().add(btnClose);

        JLabel lblVersionname = new JLabel("Version ");
        lblVersionname.setForeground(Color.WHITE);
        lblVersionname.setFont(UIManager.getFont("Menu.font"));
        lblVersionname.setHorizontalAlignment(SwingConstants.RIGHT);
        lblVersionname.setBounds(99, 33, 64, 13);
        getContentPane().add(lblVersionname);

        JLabel lblAppname = new JLabel("AppName");
        lblAppname.setForeground(Color.WHITE);
        lblAppname.setFont(UIManager.getFont("Menu.font"));
        lblAppname.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAppname.setBounds(99, 10, 64, 13);
        getContentPane().add(lblAppname);

        JLabel lblAppDetail = new JLabel();
        lblAppDetail.setForeground(Color.WHITE);
        lblAppDetail.setFont(UIManager.getFont("Menu.font"));
        lblAppDetail.setBounds(175, 10, 152, 13);
        getContentPane().add(lblAppDetail);
        String appStr = String.format(": %s", JMPCore.APPLICATION_NAME);
        lblAppDetail.setText(appStr);

        JLabel lblVerDetail = new JLabel();
        lblVerDetail.setForeground(Color.WHITE);
        lblVerDetail.setFont(UIManager.getFont("Menu.font"));
        lblVerDetail.setBounds(175, 33, 152, 13);
        getContentPane().add(lblVerDetail);
        String verStr = String.format(": %s", JMPCore.APPLICATION_VERSION);
        lblVerDetail.setText(verStr);

        JButton btnLicense = new JButton("License");
        btnLicense.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                licenseDialog.start();
            }
        });
        btnLicense.setBounds(149, 105, 91, 21);
        getContentPane().add(btnLicense);

        JLabel iconLabel = new JLabel("");
        iconLabel.setBounds(23, 10, 64, 64);

        Image image = JMPCore.getResourceManager().getJmpImageIcon();
        if (image != null) {
            iconLabel.setIcon(new ImageIcon(image));
        }
        getContentPane().add(iconLabel);

        JLabel lblJmplib = new JLabel("JMPLIB");
        lblJmplib.setForeground(Color.WHITE);
        lblJmplib.setFont(UIManager.getFont("Menu.font"));
        lblJmplib.setBounds(175, 61, 152, 13);
        lblJmplib.setText(String.format(": %s", JMPCore.LIBRALY_VERSION));
        getContentPane().add(lblJmplib);

        JLabel labelBuild = new JLabel("LIB Version ");
        labelBuild.setHorizontalAlignment(SwingConstants.RIGHT);
        labelBuild.setForeground(Color.WHITE);
        labelBuild.setFont(UIManager.getFont("Menu.font"));
        labelBuild.setBounds(99, 61, 64, 13);
        getContentPane().add(labelBuild);

        licenseDialog = JMPCore.getSystemManager().getLicenseDialog();
    }

    public void start() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }
}
