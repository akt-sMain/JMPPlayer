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
import jmp.JMPLoader;
import jmp.skin.DefaultImageMaker;
import jmp.skin.ImageResource;
import jmp.skin.Resource;
import jmp.skin.Skin;
import jmp.skin.SkinGlobalConfig;

public class ResourceManager extends AbstractManager implements IManager {

    public static final String SKIN_FOLDER_NAME = "skin";
    public static final String GROBAL_SKIN_FILE_NAME = "skin.txt";

    private Skin skin = null;
    private Image jmpImageIcon = null;

    private String skinPath = Utility.pathCombin(Platform.getCurrentPath(false), SKIN_FOLDER_NAME);

    ResourceManager(int pri) {
        super(pri, "resource");
    }

    @Override
    protected boolean initFunc() {
        super.initFunc();

        // アイコン作成
        jmpImageIcon = createJmpIcon();

        // リソース生成
        SkinGlobalConfig gConfig = new SkinGlobalConfig();

        if (JMPLoader.UseSkinFile == true) {
            try {
                String confPath = Utility.pathCombin(Platform.getCurrentPath(false), GROBAL_SKIN_FILE_NAME);
                outputSkinConfig(confPath);

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
        }
        else {
            skin = new Skin();
        }

        return true;
    }

    @Override
    protected boolean endFunc() {
        super.endFunc();
        return true;
    }

    private void outputSkinConfig(String confPath) {
        File confFile = new File(confPath);
        if (confFile.exists() == false) {
            // Skin.txt作成
            List<String> confFileContent = new LinkedList<String>();
            confFileContent.add("# Skin folder name to use.");
            confFileContent.add(SkinGlobalConfig.KEY_USE + "=default");
            confFileContent.add("");
            try {
                Utility.outputTextFile(confPath, confFileContent);
            }
            catch (Exception e) {
            }
        }
    }

    private Image getResourceImage(String name) {
        Resource r = skin.getResource(name);
        if (r == null) {
            return null;
        }

        if (r instanceof ImageResource) {
            ImageResource imgr = (ImageResource) r;
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
        Image ret = getResourceImage(Skin.RSRC_FILE_ICON_OTEHER);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultFileOtherIcon();
        }
        return ret;
    }

    public Image getFileMidiIcon() {
        Image ret = getResourceImage(Skin.RSRC_FILE_ICON_MIDI);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultFileMidiIcon();
        }
        return ret;
    }

    public Image getFileWavIcon() {
        Image ret = getResourceImage(Skin.RSRC_FILE_ICON_WAV);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultFileWavIcon();
        }
        return ret;
    }

    public Image getFileXmlIcon() {
        Image ret = getResourceImage(Skin.RSRC_FILE_ICON_XML);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultFileXmlIcon();
        }
        return ret;
    }

    public Image getFileFolderIcon() {
        Image ret = getResourceImage(Skin.RSRC_FOLDER_ICON);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultFileFolderIcon();
        }
        return ret;
    }

    public Image getBtnPlayIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_PLAY);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnPlayIcon();
        }
        return ret;
    }

    public Image getBtnStopIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_STOP);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnStopIcon();
        }
        return ret;
    }

    public Image getBtnNextIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_NEXT);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnNextIcon();
        }
        return ret;
    }

    public Image getBtnNext2Icon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_NEXT2);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnNext2Icon();
        }
        return ret;
    }

    public Image getBtnPrevIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_PREV);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnPrevIcon();
        }
        return ret;
    }

    public Image getBtnPrev2Icon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_PREV2);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnPrev2Icon();
        }
        return ret;
    }

    public Image getBtnAutoIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_AUTO);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnAutoIcon();
        }
        return ret;
    }

    public Image getBtnLoopIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_LOOP);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnLoopIcon();
        }
        return ret;
    }

    public Image getBtnListIcon() {
        Image ret = getResourceImage(Skin.RSRC_BTN_ICON_LIST);
        if (ret == null) {
            ret = DefaultImageMaker.makeDefaultBtnListIcon();
        }
        return ret;
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
