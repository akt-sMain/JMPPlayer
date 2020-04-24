package jlib.core;

import java.io.File;

public interface IFileManager {

    /**
     * ファイルロード処理
     *
     * @param f
     *            ファイル
     */
    abstract void loadFile(File f);

    /**
     * ファイルロード処理
     *
     * @param path
     *            ファイルパス
     */
    default void loadFile(String path) {
        loadFile(new File(path));
    }
}
