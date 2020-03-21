package jmp.gui.ui;

import java.awt.Image;

import javax.swing.JFrame;

import jlib.gui.IJmpWindow;
import jmp.core.JMPCore;

public class JMPFrame extends JFrame implements IJMPComponentUI, IJmpWindow {

    public JMPFrame() {
        super();

        _init();
    }

    private void _init() {
        getContentPane().setBackground(getJmpBackColor());
    }

    protected void setJmpIcon() {
        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }
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
