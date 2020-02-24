package jmp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;

public class HistoryDialog extends JMPDialog {

    private final JPanel contentPanel = new JPanel();
    private JList<String> list;

    /**
     * Create the dialog.
     */
    public HistoryDialog() {
        super();
        setTitle("履歴");
        setBounds(100, 100, 600, 480);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        contentPanel.setBackground(getJmpBackColor());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_HISTORY, this);

        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                list = JMPCore.getDataManager().getHistory();
                scrollPane.setViewportView(list);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            buttonPane.setBackground(getJmpBackColor());
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton playButton = new JButton("再生");
                playButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String item = list.getSelectedValue();
                        if (item != null) {
                            // ロード→再生フラグを立てる
                            JMPFlags.LoadToPlayFlag = true;

                            // 履歴に残さずロードする
                            JMPFlags.NoneHIstoryLoadFlag = true;
                            JMPCore.getSystemManager().getMainWindow().loadFile(item);
                        }
                    }
                });
                {
                    JButton buttonClear = new JButton("履歴クリア");
                    buttonClear.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JMPCore.getDataManager().clearHistory();
                        }
                    });
                    buttonPane.add(buttonClear);
                }
                playButton.setActionCommand("okCommand");
                buttonPane.add(playButton);
                getRootPane().setDefaultButton(playButton);
            }
            {
                JButton cancelButton = new JButton("閉じる");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    public void open() {
        update();
        setVisible(true);
    }

    public void update() {
        repaint();
    }

}
