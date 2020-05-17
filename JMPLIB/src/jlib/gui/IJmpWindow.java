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
    }

    /**
     * レイアウトの初期化
     */
    default void initializeLayout() {
    }

    /**
     * 設定変更イベント
     *
     * @param key 設定キー
     */
    default void updateConfig(String key) {
    }
}
