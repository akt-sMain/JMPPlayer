package jmp;

/**
 * アプリケーション全体で管理するフラグ
 *
 * @author akkut
 *
 */
public class JMPFlags {
    /** デバッグモード */
    public static boolean DebugMode = false;

    /** アクティベートフラグ */
    public static boolean ActivateFlag = false;

    /** 自動シンセ接続 */
    public static boolean StartupAutoConectSynth = false;

    /** ロード後に自動再生するか？ */
    public static boolean LoadToPlayFlag = false;

    /** 履歴に残さずロードする */
    public static boolean NoneHIstoryLoadFlag = false;

    /** ロード中フラグ */
    public static boolean NowLoadingFlag = false;

    /** プラグインをロードしない */
    public static boolean NonPluginLoadFlag = false;

    /** 起動時にファイルロードするか */
    public static boolean RequestFileLoadFlag = false;

}
