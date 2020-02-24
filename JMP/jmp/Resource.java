package jmp;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import function.Utility;

public class Resource {

    public Image jmpImageIcon = null;
    public Image fileOtherIcon = null;
    public Image fileMidiIcon = null;
    public Image fileWavIcon = null;
    public Image fileFolderIcon = null;
    public Image btnPlayIcon = null;
    public Image btnStopIcon = null;
    public Image btnNextIcon = null;
    public Image btnNext2Icon = null;
    public Image btnPrevIcon = null;
    public Image btnPrev2Icon = null;

    public Resource() {
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

}
