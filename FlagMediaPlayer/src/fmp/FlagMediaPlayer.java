package fmp;

import java.awt.Dimension;
import java.io.File;

import gui.FMPMainWindow;
import gui.FlagMediaPlayerWindow;
import gui.MediaPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class FlagMediaPlayer {

	private static final long TIMEOUT_MILLS = 20 * 1000;
	private static void waitForChangeState(MediaPlayer player, MediaPlayer.Status state, long maxSleepMills) {
		final long waitTime = 500;
		long cnt = maxSleepMills / waitTime;
		for (int i = 0; player.getStatus() != state; i++) {
            try {
                Thread.sleep(waitTime);
            }
            catch (Exception e) {
            }
            if (i > cnt) {
            	break;
            }
        }
	}
	
    public static FlagMediaPlayerWindow ActiveWindow = null;
    public static FMPMainWindow MainWindow = null;

    public static void main(String[] args) throws Exception {
    	//openMainWindow();
    	openSingleWindow(new File("sample.mp4"), true);
    }
    
    public static FlagMediaPlayerWindow openSingleWindow(File file, boolean playAfterExit) throws Exception {
        MediaPanel panel = new MediaPanel(file, null);

        FlagMediaPlayerWindow win = new FlagMediaPlayerWindow(panel);

        Media media = panel.getMedia();
        MediaPlayer player = panel.getPlayer();

        // 読み込み待ち
        waitForChangeState(player, MediaPlayer.Status.READY, TIMEOUT_MILLS);

        // 動画サイズを取得
        int videoW = media.getWidth();
        int videoH = media.getHeight();
        if (videoW <= 0 && videoH <= 0) {
        	panel.setAudioOnly(true);
        }

        // MoviePanelのサイズを動画に合わせる
        panel.setPreferredSize(new Dimension(videoW, videoH));
        win.getContentPane().add(panel);

        // Frameのサイズ調整
        win.getContentPane().setPreferredSize(new Dimension(videoW, videoH));
        win.pack();

        panel.threadStart();
        
        if (playAfterExit == true) {
        	win.setVisible(true);
	        panel.getPlayer().play();
	        
	        while(true) {
	        	if (ActiveWindow == null) {
	        		break;
	        	}
	        	Thread.sleep(1000);
	        }
	        
	        win.exitResource();
	        win.dispose();
	        
	        System.exit(0);
        }
        return win;
    }
    public static void openMainWindow() {
        ActiveWindow = null;

        MainWindow = new FMPMainWindow();
        MainWindow.setVisible(true);
    }

    public static void exit() {
        if (MainWindow != null) {
            MainWindow.setVisible(false);
            MainWindow.dispose();
        }

        System.exit(0);
    }

}
