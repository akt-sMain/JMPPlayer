package jmp.player;

import java.io.File;

import fmp.FlagMediaPlayer;
import function.Utility;
import gui.FlagMediaAccessor;
import gui.FlagMediaAccessor.PlayerStatus;
import jlib.player.Player;

public class MoviePlayer extends Player {
	
	private static final long TIMEOUT_MILLS = 15 * 1000;

	//FlagMediaPlayerWindow win;
	private FlagMediaAccessor mediaAccessor;
	
	public MoviePlayer() {
		super();
	}

	@Override
	public void play() {
		if (isValid() == false) {
			return;
		}
		if (mediaAccessor.isVisibleView() == false) {
			if(mediaAccessor.isAudioOnly() == false) {
				mediaAccessor.setVisibleView(true);
			}
		}
		if (isRunnable() == false) {
			mediaAccessor.play(TIMEOUT_MILLS);
		}
	}

	@Override
	public void stop() {
		if (isValid() == false) {
			return;
		}
		mediaAccessor.pause(TIMEOUT_MILLS);
	}

	@Override
	public boolean isRunnable() {
		if (isValid() == false) {
			return false;
		}
		if (mediaAccessor.getPlayerStatus() == PlayerStatus.PLAYING) {
			return true;
		}
		return false;
	}

	@Override
	public void setPosition(long pos) {
		if (isValid() == false) {
			return;
		}
		mediaAccessor.seek(pos);
	}

	@Override
	public long getPosition() {
		if (isValid() == false) {
			return 0;
		}
		return mediaAccessor.getCurrentMills();
	}

	@Override
	public long getLength() {
		if (isValid() == false) {
			return 0;
		}
		return mediaAccessor.getTotalMills();
	}

	@Override
	public boolean isValid() {
		if (mediaAccessor == null) {
			return false;
		}
		return mediaAccessor.isValid();
	}

	@Override
	public int getPositionSecond() {
		if (isValid() == false) {
			return 0;
		}
		return mediaAccessor.getCurrentSeconds();
	}

	@Override
	public int getLengthSecond() {
		if (isValid() == false) {
			return 0;
		}
		return mediaAccessor.getTotalSeconds();
	}

	@Override
	public void setVolume(float volume) {
	}

	@Override
	public float getVolume() {
		return 0;
	}
	
	private void closeResource() {
		if (mediaAccessor != null) {
			mediaAccessor.closeResource(5000);
		}
	}

	@Override
	public boolean loadFile(File file) throws Exception {
		if (isValid() == true) {
			// 開いていたビューを破棄する
			closeResource();
		}
		// 新しく作成
		mediaAccessor = FlagMediaPlayer.openSingleWindow(file, false);
		if (mediaAccessor == null) {
			return false;
		}
		
		mediaAccessor.setTitle("Movie - " + Utility.getFileNameAndExtension(file));
		
		// 動画ファイルは画面表示する
		if(mediaAccessor.isAudioOnly() == false) {
			// 画面中心に表示
			mediaAccessor.setVisibleView(true, true);
		}
		return true;
	}

	@Override
	public boolean saveFile(File file) throws Exception {
		return false;
	}
	
	@Override
	public boolean close() {
		closeResource();
		return super.close();
	}
	
	@Override
	public void changingPlayer() {
		closeResource();
		super.changingPlayer();
	}

}