package jlib.manager;

import jlib.IJmpWindow;

public interface IWindowManager {

    /**
     * Windowインスタンス取得
     *
     * @param name
     *            Window識別名
     * @return Windowインスタンス
     */
    abstract IJmpWindow getWindow(String name);

    /**
     * Window識別名の一覧表示
     *
     * @return Window識別名一覧(","区切りで表示)
     */
    abstract String[] getWindowNameList();

    /**
     * Window識別名の一覧表示
     *
     * @return Window識別名一覧配列
     */
    default String getWindowNames() {
        String ret = "";
        for (String name : getWindowNameList()) {
            ret += (name + ",");
        }
        return ret;
    }
}
