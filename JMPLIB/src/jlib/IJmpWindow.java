package jlib;

public interface IJmpWindow {
	/**
	 * ウィンドウ表示化
	 */
	abstract void showWindow();

	/**
	 * ウィンドウ非表示化
	 */
	abstract void hideWindow();

	/**
	 * ウィンドウ表示/非表示化
	 */
	abstract boolean isWindowVisible();
}
