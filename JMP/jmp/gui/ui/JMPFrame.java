package jmp.gui.ui;

import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;

import jlib.gui.IJmpWindow;
import jmp.core.JMPCore;

public class JMPFrame extends JFrame implements IJMPComponentUI, IJmpWindow {

    // Window配置の初期化実施
    protected boolean initializeDefaultPositionFrag = false;

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
    public void setVisible(boolean b) {
        if (super.isVisible() == false && b == true) {
            if (initializeDefaultPositionFrag == false) {
                Point p = getDefaultWindowLocation();
                if (p != null) {
                    this.setLocation(p);
                    initializeDefaultPositionFrag = true;
                }
            }
        }
        super.setVisible(b);
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
