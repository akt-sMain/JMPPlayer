package jlib.gui;

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

    /*
     * 言語更新イベント
     */
    default void updateLanguage() {
    };

    /**
     * レイアウトの初期化
     */
    default void initializeLayout() {
    }
}
