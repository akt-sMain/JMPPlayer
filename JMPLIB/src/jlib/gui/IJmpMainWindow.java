package jlib.gui;

import java.io.File;

import jlib.plugin.IPlugin;

public interface IJmpMainWindow extends IJmpWindow {

    /**
     * ファイルオープン処理実行
     */
    abstract void fileOpenFunc();

    /**
     * ファイルロード
     *
     * @param path
     *            ファイルパス
     */
    abstract void loadFile(String path);

    default void loadFile(File file) {
        loadFile(file.getPath());
    }

    /**
     * ステータステキスト設定
     *
     * @param text
     *            テキスト
     * @param status
     *            ステータス
     */
    abstract void setStatusText(String text, boolean status);

    /**
     * プラグインメニュー追加
     *
     * @param name
     *            プラグイン名
     * @param plugin
     *            プラグイン
     */
    abstract void addPluginMenu(String name, IPlugin plugin);

    /**
     * 更新処理
     */
    abstract void update();

    /**
     * 終了
     */
    abstract void exit();
}
