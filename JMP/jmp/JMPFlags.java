package jmp;

/**
 * アプリケーション全体で管理するフラグ
 *
 * @author akt
 *
 */
public class JMPFlags {
    /** ログ出力クラス */
    public static class Log {
        private static void cprintImpl(String msg, boolean ln) {
            if (JMPFlags.CoreConsoleOut == true) {
                if (ln == true) {
                    System.out.println(msg);
                }
                else {
                    System.out.print(msg);
                }
            }
        }

        /** コンソール出力 */
        public static void cprint(String msg) {
            cprintImpl(msg, false);
        }

        /** コンソール出力 */
        public static void cprintln(String msg) {
            cprintImpl(msg, true);
        }
    }

    /** ライブラリモード */
    public static boolean LibraryMode = false;

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

    /** 非同期Midiパケットを使用するか */
    public static boolean UseUnsynchronizedMidiPacket = false;

    /** コアの初期化・終了初期の結果をコンソール出力するか */
    public static boolean CoreConsoleOut = false;

}
