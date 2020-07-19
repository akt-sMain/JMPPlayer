package jmp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import function.Utility;
import jmp.midi.toolkit.MidiToolkitManager;

public class CommonRegister {
    public class CommonRegisterINI {
        public String key, value;

        public CommonRegisterINI(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /** デフォルトMidiToolkit名 */
    public static final String USE_MIDI_TOOLKIT_CLASSNAME = MidiToolkitManager.DEFAULT_MIDI_TOOLKIT_NAME;

    /** デフォルトプレイヤーカラー */
    public static final Color DEFAULT_PLAYER_BACK_COLOR = Color.DARK_GRAY;

    public static final String COMMON_REGKEY_CH_COLOR_FORMAT = "ch_color_%d";
    public static final String COMMON_REGKEY_PLAYER_BACK_COLOR = "player_back_color";
    public static final String COMMON_REGKEY_EXTENSION_MIDI = "extension_midi";
    public static final String COMMON_REGKEY_EXTENSION_WAV = "extension_wav";
    public static final String COMMON_REGKEY_EXTENSION_MUSICXML = "extension_musicxml";
    public static final String COMMON_REGKEY_USE_MIDI_TOOLKIT = "use_midi_toolkit";

    private List<CommonRegisterINI> iniList = null;

    public CommonRegister() {
        iniList = new ArrayList<CommonRegisterINI>();
        init();
    }

    public void load() {
    }

    public void init() {
        add(COMMON_REGKEY_EXTENSION_MIDI, JmpUtil.genExtensions2Str("mid", "midi"));
        add(COMMON_REGKEY_EXTENSION_WAV, JmpUtil.genExtensions2Str("wav"));
        add(COMMON_REGKEY_EXTENSION_MUSICXML, JmpUtil.genExtensions2Str("xml", "musicxml", "mxl"));
        add(COMMON_REGKEY_USE_MIDI_TOOLKIT, USE_MIDI_TOOLKIT_CLASSNAME);
        add(COMMON_REGKEY_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(DEFAULT_PLAYER_BACK_COLOR));
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 1), "#8ec21f");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 2), "#3dc21f");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 3), "#1fc253");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 4), "#1fc2a4");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 5), "#1f8ec2");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 6), "#1f3dc2");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 7), "#531fc2");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 8), "#a41fc2");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 9), "#ffc0cb");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 10), "#c21f3d");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 11), "#c2531f");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 12), "#c2a41f");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 13), "#3d00c2");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 14), "#ffff29");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 15), "#bbff29");
        add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 16), "#f98608");
    }

    private CommonRegisterINI getIni(String key) {
        for (CommonRegisterINI ini : iniList) {
            if (ini.key.equalsIgnoreCase(key) == true) {
                return ini;
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        for (CommonRegisterINI ini : iniList) {
            if (ini.key.equalsIgnoreCase(key) == true) {
                return true;
            }
        }
        return false;
    }

    public void add(String key, String value) {
        if (containsKey(key) == true) {
            setValue(key, value);
            return;
        }
        iniList.add(new CommonRegisterINI(key, value));
    }

    public boolean setValue(String key, String value) {
        if (containsKey(key) == true) {
            getIni(key).value = value;
            return true;
        }
        return false;
    }

    public String getValue(String key) {
        if (containsKey(key) == true) {
            return getIni(key).value;
        }
        return "";
    }

    public String[] getKeySet() {
        int len = iniList.size();
        String[] lst = new String[len];
        for (int i = 0; i < len; i++) {
            lst[i] = iniList.get(i).key;
        }
        return lst;
    }
}
