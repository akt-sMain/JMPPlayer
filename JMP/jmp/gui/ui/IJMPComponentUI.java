package jmp.gui.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Window;

import function.Utility;
import jlib.gui.IJmpMainWindow;
import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.core.SystemManager.CommonRegister;

public interface IJMPComponentUI {
    public static final int DEFAULT_WINDOW_OFFSET = 50;

    abstract void updateBackColor();

    default Color getJmpBackColor() {
        SystemManager sm = JMPCore.getSystemManager();
        String colorStr = sm.getCommonRegisterValue(CommonRegister.COMMON_REGKEY_PLAYER_BACK_COLOR);
        if (colorStr.equalsIgnoreCase("") == false) {
            return Utility.convertCodeToHtmlColor(colorStr);
        }
        else {
            return Utility.convertCodeToHtmlColor("#404040");
        }
    }

    default Point getDefaultWindowLocation() {
        Point p = new Point(50, 50);

        SystemManager system = JMPCore.getSystemManager();
        if (system != null) {
            IJmpMainWindow mainWindow = system.getMainWindow();
            if (mainWindow instanceof Window) {
                Window root = (Window) mainWindow;
                if (root != null) {
                    int offsetX, offsetY;
                    if (root.isAlwaysOnTop() == true) {
                        offsetX = root.getWidth();
                        offsetY = 0;
                    }
                    else {
                        offsetX = DEFAULT_WINDOW_OFFSET;
                        offsetY = DEFAULT_WINDOW_OFFSET;
                    }
                    p = root.getLocation();
                    p.setLocation(p.getX() + offsetX, p.getY() + offsetY);
                }
            }
        }
        return p;
    }

}
