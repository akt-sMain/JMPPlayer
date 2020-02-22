package jlib.manager;

import jlib.IJmpMainWindow;
import jlib.IPlugin;

public interface ISystemManager extends IManager {

	/**
	 * データファイル格納ディレクトリパス
	 *
	 * @return パス
	 */
	abstract String getDataFileLocationPath();

	/**
	 * データファイル格納ディレクトリパス
	 *
	 * @return パス
	 */
	abstract String getResFileLocationPath();

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
	 * メインウィンドウ取得
	 *
	 * @return メインウィンドウ
	 */
	abstract IJmpMainWindow getMainWindow();

	/**
	 * ファイルロード(MainWindowのラッパー)
	 *
	 * @param path
	 *            ファイルパス
	 */
	public default void loadFile(String path) {
		getMainWindow().loadFile(path);
	}
}
