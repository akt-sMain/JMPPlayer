package jlib.core;

import jlib.plugin.IPlugin;

public interface ISystemManager {

    /**
     * スタンドアロンモードか
     *
     * @return
     */
    abstract boolean isEnableStandAlonePlugin();

    /**
     * データファイル格納ディレクトリパス
     *
     * @return パス
     */
    abstract String getDataFileLocationPath(IPlugin plugin);

    /**
     * データファイル格納ディレクトリパス
     *
     * @return パス
     */
    abstract String getResFileLocationPath(IPlugin plugin);

    /**
     * プラグイン名取得
     *
     * @param plugin
     *            プラグイン
     * @return プラグイン名
     */
    abstract String getPluginName(IPlugin plugin);

    /**
     * 共通レジスタに対してパラメータ設定を行う
     *
     * @param key
     *            キー文字列
     * @param value
     *            パラメータ
     * @return 設定に成功したか?
     */
    abstract boolean setCommonRegisterValue(String key, String value);

    /**
     * 共通レジスタからパラメータを取得する
     *
     * @param key
     *            キー文字列
     * @return パラメータ(取得できなかった場合は、""を返す)
     */
    abstract String getCommonRegisterValue(String key);

    /**
     * 共通レジスタのキーセットを取得する
     *
     * @return キーセット
     */
    abstract String[] getCommonKeySet();
}
