package jmp.skin;

import java.util.HashMap;
import java.util.Map;

import function.Utility;

public class Skin {

    public static final String RSRC_FILE_ICON_OTEHER = "file_icon_other.png";
    public static final String RSRC_FILE_ICON_MIDI = "file_icon_MIDI.png";
    public static final String RSRC_FILE_ICON_WAV = "file_icon_WAV.png";
    public static final String RSRC_FOLDER_ICON = "folder_icon.png";
    public static final String RSRC_BTN_ICON_PLAY = "btn_icon_PLAY.png";
    public static final String RSRC_BTN_ICON_STOP = "btn_icon_STOP.png";
    public static final String RSRC_BTN_ICON_NEXT = "btn_icon_NEXT.png";
    public static final String RSRC_BTN_ICON_NEXT2 = "btn_icon_NEXT2.png";
    public static final String RSRC_BTN_ICON_PREV = "btn_icon_PREV.png";
    public static final String RSRC_BTN_ICON_PREV2 = "btn_icon_PREV2.png";

    private static final String[] LST = { RSRC_FILE_ICON_OTEHER, RSRC_FILE_ICON_MIDI, RSRC_FILE_ICON_WAV,
            RSRC_FOLDER_ICON, RSRC_BTN_ICON_PLAY, RSRC_BTN_ICON_STOP, RSRC_BTN_ICON_NEXT, RSRC_BTN_ICON_NEXT2,
            RSRC_BTN_ICON_PREV, RSRC_BTN_ICON_PREV2, };

    private Map<String, Resource> rsrcMap = new HashMap<String, Resource>();

    private SkinConfig config = null;

    public Skin(SkinConfig config) {
        for (String lstname : LST) {
            rsrcMap.put(lstname, null);
        }
        load(config);
    }

    public void load(SkinConfig config) {
        this.config = config;
        for (String lstname : LST) {
            createResource(lstname);
        }
    }

    private void createResource(String name) {
        String path = Utility.pathCombin(config.getRefPath(), config.getName(), name);
        if (Utility.isExsistFile(path) == true) {
            if (Utility.checkExtension(path, "png") == true) {
                ImageResource rsrc = new ImageResource();
                rsrc.setImage(ResourceUtility.readImage(path, config.isConvertTransparent(), config.getTransparentColor()));
                if (rsrc.getImage() != null) {
                    rsrcMap.put(name, rsrc);
                }
            }
        }
    }

    public Resource getResource(String name) {
        if (rsrcMap.containsKey(name) == false) {
            return null;
        }
        return rsrcMap.get(name);
    }
}
