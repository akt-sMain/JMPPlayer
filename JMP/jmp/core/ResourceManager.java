package jmp.core;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import function.Platform;
import function.Utility;
import jlib.core.IManager;
import jmp.skin.ImageResource;
import jmp.skin.Resource;
import jmp.skin.Skin;
import jmp.skin.SkinGlobalConfig;

public class ResourceManager extends AbstractManager implements IManager {

    public static final String SKIN_FOLDER_NAME = "skin";

    private Skin skin = null;
    private Image jmpImageIcon = null;

    private String skinPath = Utility.pathCombin(Platform.getCurrentPath(false), SKIN_FOLDER_NAME);

    ResourceManager(int pri) {
        super(pri, "resource");
    }

    @Override
    protected boolean initFunc() {
        if (initializeFlag == false) {
            initializeFlag = true;
        }

        // アイコン作成
        jmpImageIcon = createJmpIcon();

        String confPath = Utility.pathCombin(Platform.getCurrentPath(false), "skin.txt");
        File confFile = new File(confPath);
        if (confFile.exists() == false) {
            // Skin.txt作成
            List<String> confFileContent = new LinkedList<String>();
            confFileContent.add("# 使用するSkinフォルダ名");
            confFileContent.add(SkinGlobalConfig.KEY_USE + "=default");
            confFileContent.add("");
            try {
                Utility.outputTextFile(confPath, confFileContent);
            }
            catch (Exception e) {
            }
        }

        // リソース生成
        SkinGlobalConfig gConfig = new SkinGlobalConfig();
        try {
            gConfig.read(new File(confPath));

            String path = Utility.pathCombin(skinPath, gConfig.getName());
            if (Utility.isExsistFile(path) == false) {
                gConfig.initialize();
            }
        }
        catch (IOException e) {
            gConfig.initialize();
        }

        skin = new Skin(gConfig);
        return true;
    }

    @Override
    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

    private Image getResourceImage(String name) {
        Resource r = skin.getResource(name);
        if (r == null) {
            return null;
        }

        if (r instanceof ImageResource) {
            ImageResource imgr = (ImageResource)r;
            return imgr.getImage();
        }
        return null;
    }

    private Image createJmpIcon() {
        URL url = this.getClass().getClassLoader().getResource("iconPNG.png");
        Image img = null;
        try {
            img = ImageIO.read(url);
        }
        catch (Exception e) {
            img = null;
        }
        return img;
    }

    public Image getJmpImageIcon() {
        return jmpImageIcon;
    }

    public Image getFileOtherIcon() {
        return getResourceImage(Skin.RSRC_FILE_ICON_OTEHER);
    }

    public Image getFileMidiIcon() {
        return getResourceImage(Skin.RSRC_FILE_ICON_MIDI);
    }

    public Image getFileWavIcon() {
        return getResourceImage(Skin.RSRC_FILE_ICON_WAV);
    }

    public Image getFileXmlIcon() {
        return getResourceImage(Skin.RSRC_FILE_ICON_XML);
    }

    public Image getFileFolderIcon() {
        return getResourceImage(Skin.RSRC_FOLDER_ICON);
    }

    public Image getBtnPlayIcon() {
        return getResourceImage(Skin.RSRC_BTN_ICON_PLAY);
    }

    public Image getBtnStopIcon() {
        return getResourceImage(Skin.RSRC_BTN_ICON_STOP);
    }

    public Image getBtnNextIcon() {
        return getResourceImage(Skin.RSRC_BTN_ICON_NEXT);
    }

    public Image getBtnNext2Icon() {
        return getResourceImage(Skin.RSRC_BTN_ICON_NEXT2);
    }

    public Image getBtnPrevIcon() {
        return getResourceImage(Skin.RSRC_BTN_ICON_PREV);
    }

    public Image getBtnPrev2Icon() {
        return getResourceImage(Skin.RSRC_BTN_ICON_PREV2);
    }

    public Color getBtnBackgroundColor() {
        if (skin == null) {
            return Color.WHITE;
        }
        return skin.getLocalConfig().getBtnBackgroundColor();
    }

    public Color getAppBackgroundColor() {
        if (skin == null) {
            return Color.WHITE;
        }
        return skin.getLocalConfig().getAppBackgroundColor();
    }

    public String getSkinPath() {
        return skinPath;
    }
}
