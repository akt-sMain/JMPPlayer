package jmp.player;

import java.io.File;

import fmp.FlagMediaPlayer;
import function.Utility;
import gui.FlagMediaPlayerWindow;
import javafx.scene.media.MediaPlayer;
import jlib.player.Player;

public class MoviePlayer extends Player {
	
	private static final long TIMEOUT_MILLS = 15 * 1000;

	FlagMediaPlayerWindow win;
	
	public MoviePlayer() {
		super();
	}

	@Override
	public void play() {
		if (isValid() == false) {
			return;
		}
		if (win.isVisible() == false) {
			if(win.getMediaPanel().isAudioOnly() == false) {
				win.setVisible(true);
			}
		}
		if (isRunnable() == false) {
			win.getMediaPanel().getPlayer().play();
			waitForChangeState(MediaPlayer.Status.PLAYING, TIMEOUT_MILLS);
		}
	}
	
	private void waitForChangeState(MediaPlayer.Status state, long maxSleepMills) {
		final long waitTime = 500;
		long cnt = maxSleepMills / waitTime;
		for (int i = 0; win.getMediaPanel().getPlayer().getStatus() != state; i++) {
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

	@Override
	public void stop() {
		if (isValid() == false) {
			return;
		}
		win.getMediaPanel().getPlayer().pause();
		waitForChangeState(MediaPlayer.Status.PAUSED, TIMEOUT_MILLS);
	}

	@Override
	public boolean isRunnable() {
		if (isValid() == false) {
			return false;
		}
		return win.getMediaPanel().getPlayer().getStatus() == MediaPlayer.Status.PLAYING;
	}

	@Override
	public void setPosition(long pos) {
		if (isValid() == false) {
			return;
		}
		win.getMediaPanel().getPlayer().seek(javafx.util.Duration.millis(pos));
	}

	@Override
	public long getPosition() {
		if (isValid() == false) {
			return 0;
		}
        MediaPlayer player = win.getMediaPanel().getPlayer();
        long curTime = (long) player.getCurrentTime().toMillis();
		return curTime;
	}

	@Override
	public long getLength() {
		if (isValid() == false) {
			return 0;
		}
		MediaPlayer player = win.getMediaPanel().getPlayer();
		return (long) player.getTotalDuration().toMillis();
	}

	@Override
	public boolean isValid() {
		if (win == null) {
			return false;
		}
		if (win.getMediaPanel() == null) {
			return false;
		}
		return true;
	}

	@Override
	public int getPositionSecond() {
		if (isValid() == false) {
			return 0;
		}
		MediaPlayer player = win.getMediaPanel().getPlayer();
        int curTime = (int) player.getCurrentTime().toSeconds();
		return curTime;
	}

	@Override
	public int getLengthSecond() {
		if (isValid() == false) {
			return 0;
		}
		MediaPlayer player = win.getMediaPanel().getPlayer();
		return (int) player.getTotalDuration().toSeconds();
	}

	@Override
	public void setVolume(float volume) {
	}

	@Override
	public float getVolume() {
		return 0;
	}

	@Override
	public boolean loadFile(File file) throws Exception {
		if (win != null) {
			// 開いていたビューを破棄する
			win.setVisible(false);
			win.getMediaPanel().getPlayer().stop();
			waitForChangeState(MediaPlayer.Status.STOPPED, TIMEOUT_MILLS);
			win.exitResource();
		}
		// 新しく作成
		win = FlagMediaPlayer.openSingleWindow(file, false);
		if (win == null) {
			return false;
		}
		
		win.setTitle("Movie - " + Utility.getFileNameAndExtension(file));
		
		// 動画ファイルは画面表示する
		if(win.getMediaPanel().isAudioOnly() == false) {
			// 画面中心に表示
	        win.setLocationRelativeTo(null);
	        win.setVisible(true);
		}
		return true;
	}

	@Override
	public boolean saveFile(File file) throws Exception {
		return false;
	}
	
	@Override
	public boolean close() {
		changingPlayer();
		return super.close();
	}
	
	@Override
	public void changingPlayer() {
		win.setVisible(false);
		win.getMediaPanel().getPlayer().stop();
		waitForChangeState(MediaPlayer.Status.STOPPED, 15000);
		win.exitResource();
		super.changingPlayer();
	}

}
