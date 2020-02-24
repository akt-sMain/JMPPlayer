package jmp.gui.ui;

import java.awt.Color;

import function.Utility;
import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.core.SystemManager.CommonRegister;

public interface IJMPComponentUI {
    default Color getJmpBackColor() {
        SystemManager sm = JMPCore.getSystemManager();
        String colorStr = sm.getCommonRegisterValue(CommonRegister.COMMON_REGKEY_PLAYER_BACK_COLOR);
        if (colorStr.equalsIgnoreCase("") == false) {
            return Utility.convertCodeToHtmlColor(colorStr);
        }
        else {
            return Color.WHITE;
        }
    }

}
