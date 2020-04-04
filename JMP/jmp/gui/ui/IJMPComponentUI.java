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
        if (JMPCore.getSystemManager() != null) {
            IJmpMainWindow mainWindow = JMPCore.getSystemManager().getMainWindow();
            if (mainWindow instanceof Window) {
                Window root = (Window) mainWindow;
                if (root != null) {
                    p = root.getLocation();
                    p.setLocation(p.getX() + DEFAULT_WINDOW_OFFSET, p.getY() + DEFAULT_WINDOW_OFFSET);
                }
            }
        }
        return p;
    }

}
