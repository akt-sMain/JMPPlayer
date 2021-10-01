package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import function.Platform;
import function.Utility;

public class DebugLogConsole extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextPane textPane;
    private String cacheText = "";


    /**
     * Create the dialog.
     */
    public DebugLogConsole() {
        setTitle("Console");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 619, 443);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                textPane = new JTextPane();
                textPane.setEditable(false);
                scrollPane.setViewportView(textPane);
                textPane.setForeground(new Color(51, 204, 204));
                textPane.setBackground(new Color(0, 0, 0));
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                {
                    JButton btnClear = new JButton("Clear");
                    btnClear.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            clearText();
                        }
                    });
                    buttonPane.add(btnClear);
                }
            }
        }
    }

    public void print(String str) {
        cacheText = Utility.stringsCombin(cacheText, str);
        updateText();
    }

    public void println(String str) {
        cacheText = Utility.stringsCombin(cacheText, str, Platform.getNewLine());
        updateText();
    }

    public void updateText() {
        textPane.setText(cacheText);
        if (cacheText.isEmpty() != false && isVisible() == true) {
            textPane.setCaretPosition(textPane.getDocument().getLength());
        }
//        if (isVisible() == true) {
//            repaint();
//        }
    }

    public void clearText() {
        cacheText = "";
        updateText();
    }

}
