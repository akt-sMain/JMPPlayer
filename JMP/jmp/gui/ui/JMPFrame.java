package jmp.gui.ui;

import javax.swing.JFrame;

import jlib.IJmpWindow;

public class JMPFrame extends JFrame implements IJMPComponentUI, IJmpWindow {

    public JMPFrame() {
        super();

        _init();
    }

    private void _init() {
        getContentPane().setBackground(getJmpBackColor());
    }

    @Override
    public void showWindow() {
        setVisible(true);
    }

    @Override
    public void hideWindow() {
        setVisible(false);
    }

    @Override
    public boolean isWindowVisible() {
        return isVisible();
    }

}
