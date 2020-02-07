package jmp;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import function.Utility;

public class ResourceManager {

    private Image jmpImageIcon = null;
    private Image fileOtherIcon = null;
    private Image fileMidiIcon = null;
    private Image fileWavIcon = null;
    private Image fileFolderIcon = null;
    private Image btnPlayIcon = null;
    private Image btnStopIcon = null;
    private Image btnNextIcon = null;
    private Image btnNext2Icon = null;
    private Image btnPrevIcon = null;
    private Image btnPrev2Icon = null;

    private static ResourceManager instance = new ResourceManager();
    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
	return instance;
    }

    public void make() {
	jmpImageIcon = readImage("iconPNG.png");
	fileOtherIcon = readImage("file_icon_other.png", true);
	fileMidiIcon = readImage("file_icon_MIDI.png", true);
	fileWavIcon = readImage("file_icon_WAV.png", true);
	fileFolderIcon = readImage("folder_icon.png", true);

	btnPlayIcon = readImage("btn_icon_PLAY.png");
	btnStopIcon = readImage("btn_icon_STOP.png");
	btnNextIcon = readImage("btn_icon_NEXT.png");
	btnNext2Icon = readImage("btn_icon_NEXT2.png");
	btnPrevIcon = readImage("btn_icon_PREV.png");
	btnPrev2Icon = readImage("btn_icon_PREV2.png");
    }

    private URL getResource(String name) {
	return this.getClass().getClassLoader().getResource(name);
    }

    private Image readImage(String name) {
	return readImage(name, false);
    }

    private Image readImage(String name, boolean toTransparencyBlack) {
	URL url = getResource(name);
	Image img = null;
	try {
	    img = ImageIO.read(url);

	    if (toTransparencyBlack == true) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage bi = Utility.imageToBufferedImage(img, w, h);
		img = Utility.transformImageToTransparencyForBlack(bi);
	    }
	}
	catch (Exception e) {
	    img = null;
		System.out.println("null");
	}
	return img;
    }

    public Image getJmpImageIcon() {
        return jmpImageIcon;
    }

    public Image getFileOtherIcon() {
        return fileOtherIcon;
    }

    public Image getFileMidiIcon() {
        return fileMidiIcon;
    }

    public Image getFileWavIcon() {
        return fileWavIcon;
    }

    public Image getFileFolderIcon() {
        return fileFolderIcon;
    }

    public Image getBtnPlayIcon() {
        return btnPlayIcon;
    }

    public Image getBtnStopIcon() {
        return btnStopIcon;
    }

    public Image getBtnNextIcon() {
        return btnNextIcon;
    }

    public Image getBtnNext2Icon() {
        return btnNext2Icon;
    }

    public Image getBtnPrevIcon() {
        return btnPrevIcon;
    }

    public Image getBtnPrev2Icon() {
        return btnPrev2Icon;
    }
}
