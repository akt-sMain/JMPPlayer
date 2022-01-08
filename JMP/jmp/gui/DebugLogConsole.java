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
    private JScrollPane scrollPane;

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
            scrollPane = new JScrollPane();
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

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b == false) {
            clearText();
        }
    }

    private static final boolean REVERSE = true;
    private static String LN = Platform.getNewLine();

    public void print(String str) {
        if (isVisible() == false) {
            return;
        }
        addText(str, false, REVERSE);
    }

    public void println(String str) {
        if (isVisible() == false) {
            return;
        }
        addText(str, true, REVERSE);
    }

    private void addText(String str, boolean ln, boolean reverse) {
        String text = textPane.getText();

        String s1, s2, s3;
        if (reverse == true) {
            s1 = str;
            s2 = ln ? LN : "";
            s3 = text;
        }
        else {
            s1 = text;
            s2 = str;
            s3 = ln ? LN : "";
        }
        String newText = Utility.stringsCombin(s1, s2, s3);
        textPane.setText(newText);
        updateText();
    }

    public void clearText() {
        textPane.setText("");
        updateText();
    }

    public void updateText() {
        if (textPane.getText().isEmpty() == false && isVisible() == true) {
            // textPane.setCaretPosition(textPane.getText().length());
            // try {
            // scrollPane.getViewport().scrollRectToVisible(new Rectangle(0,
            // Integer.MAX_VALUE - 1, 1, 1));
            // }
            // catch (Exception e) {
            // }
        }
        // if (isVisible() == true) {
        // repaint();
        // }
    }

}
