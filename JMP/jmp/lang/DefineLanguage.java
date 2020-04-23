package jmp.lang;

public class DefineLanguage {
    public static final int INDEX_LANG_ENGLISH = 0;
    public static final int INDEX_LANG_JAPANESE = 1;
    public static final int NUMBER_OF_INDEX_LANG = 2;
    static final LangID[] titles = new LangID[] { LangID.English, LangID.Japanese, };

    public static enum LangID {
        Language,
        Japanese,
        English,
        File,
        Save,
        Open,
        Reload,
        Version,
        Exit,
        Window,
        Allways_on_top,
        Player,
        Loop_playback,
        Continuous_playback,
        Playlist,
        History,
        Plugin,
        Add_plugin,
        Remove_plugin,
        Close_all_plugin,
        Setting,
        MIDI_settings,
        MIDI_device_settings,
        Open_MIDI_device_settings_on_startup,
        PC_reset,
        MIDI_message_monitor,
        MIDI_message_sender,
        Now_loading,
        Plugin_error,
        FILE_ERROR_1,
        FILE_ERROR_2,
        FILE_ERROR_3,
        FILE_ERROR_4,
        FILE_ERROR_5,
        FILE_ERROR_6,
        FILE_LOAD_SUCCESS,
        PLUGIN_LOAD_SUCCESS,
        PLUGIN_LOAD_ERROR,
        Clear,
        Playback,
        Stop,
        Fast_forward,
        Rewind,
        Next,
        Previous,
        Close,
        Add,
        Remove,
        Open_with_Explorer,
        MIDI_Data_byte,
        Send,
        Invalid_byte_data,
        License,
        Above_conditions,
        Accept,
        Reject,
        Copied_to_clipboard,
        Original_copy,
        Common_settings,
        Apply,
        Error,
        Message,
        Layout_initialization,
        Tool,
        FFmpeg_converter,
        FFmpeg_path,
        Output_directory,
        Input_file,
        Play_after_convert,
        Convert,
        A_is_invalid,
        Conversion_failed,
        Conversion_completed,
        Now_converting,
        Initialize_setting,
    }

    static java.util.HashMap<LangID, LanguageWords> langMap = new java.util.HashMap<LangID, LanguageWords>() {
        {
            put(LangID.Language, new LanguageWords("Language", "言語(Language)"));
            put(LangID.Japanese, new LanguageWords("日本語", "日本語"));
            put(LangID.English, new LanguageWords("English", "English"));
            put(LangID.File, new LanguageWords("File", "ファイル"));
            put(LangID.Save, new LanguageWords("Save", "保存"));
            put(LangID.Open, new LanguageWords("Open", "開く"));
            put(LangID.Reload, new LanguageWords("Reload", "リロード"));
            put(LangID.Version, new LanguageWords("Version", "バージョン情報"));
            put(LangID.Exit, new LanguageWords("Exit", "終了"));
            put(LangID.Window, new LanguageWords("Window", "ウィンドウ"));
            put(LangID.Allways_on_top, new LanguageWords("Allways on top", "常に手前に表示"));
            put(LangID.Player, new LanguageWords("Player", "プレイヤー"));
            put(LangID.Loop_playback, new LanguageWords("Loop playback", "ループ再生"));
            put(LangID.Continuous_playback, new LanguageWords("Continuous playback", "連続再生"));
            put(LangID.Playlist, new LanguageWords("Playlist", "プレイリスト"));
            put(LangID.History, new LanguageWords("History", "履歴"));
            put(LangID.Plugin, new LanguageWords("Plugin", "プラグイン"));
            put(LangID.Add_plugin, new LanguageWords("Add plugin", "プラグインを追加"));
            put(LangID.Remove_plugin, new LanguageWords("Remove plugin", "プラグインを削除"));
            put(LangID.Close_all_plugin, new LanguageWords("Close all plugin", "すべて閉じる"));
            put(LangID.Setting, new LanguageWords("Setting", "設定"));
            put(LangID.MIDI_settings, new LanguageWords("MIDI Settings", "MIDI設定"));
            put(LangID.MIDI_device_settings, new LanguageWords("MIDI Device settings", "MIDIデバイス設定"));
            put(LangID.Open_MIDI_device_settings_on_startup, new LanguageWords("Open MIDI device settings on startup", "起動時にMIDIデバイス設定を開く"));
            put(LangID.PC_reset, new LanguageWords("Program change reset", "プログラムチェンジリセット"));
            put(LangID.MIDI_message_monitor, new LanguageWords("MIDI Message monitor", "MIDIメッセージモニター"));
            put(LangID.MIDI_message_sender, new LanguageWords("MIDI Message sender", "MIDIメッセージ送信"));
            put(LangID.Now_loading, new LanguageWords("Now loading", "ロード中"));
            put(LangID.Plugin_error, new LanguageWords("Plugin error", "プラグインエラー"));
            put(LangID.FILE_ERROR_1, new LanguageWords("※Other files cannot be loaded while loading a file.", "※ファイルロード中に他のファイルをロードできません。"));
            put(LangID.FILE_ERROR_2, new LanguageWords("※File format not supported.", "※サポート外のファイル形式です"));
            put(LangID.FILE_ERROR_3, new LanguageWords("※Files cannot be loaded during playback.", "※再生中はファイルロードできません。"));
            put(LangID.FILE_ERROR_4, new LanguageWords("※The file cannot be opened.", "※ファイルを開くことができません。"));
            put(LangID.FILE_ERROR_5, new LanguageWords("※Failed to load the file.", "※ファイルのロードに失敗しました。"));
            put(LangID.FILE_ERROR_6, new LanguageWords("※There is no data to play.", "※再生するデータがありません。"));
            put(LangID.FILE_LOAD_SUCCESS, new LanguageWords("File loading successful.", "ファイルロード成功"));
            put(LangID.PLUGIN_LOAD_SUCCESS, new LanguageWords("Plugin loading successful.", "プラグインロード成功"));
            put(LangID.PLUGIN_LOAD_ERROR, new LanguageWords("Plugin loading failed.", "プラグインロード失敗"));
            put(LangID.Clear, new LanguageWords("Clear", "クリア"));
            put(LangID.Playback, new LanguageWords("Playback", "再生"));
            put(LangID.Stop, new LanguageWords("Stop", "停止"));
            put(LangID.Fast_forward, new LanguageWords("Fast forward", "早送り"));
            put(LangID.Rewind, new LanguageWords("Rewind", "巻き戻し"));
            put(LangID.Next, new LanguageWords("Next", "次へ"));
            put(LangID.Previous, new LanguageWords("Previous", "前へ"));
            put(LangID.Close, new LanguageWords("Close", "閉じる"));
            put(LangID.Add, new LanguageWords("Add", "追加"));
            put(LangID.Remove, new LanguageWords("Remove", "削除"));
            put(LangID.Open_with_Explorer, new LanguageWords("Open with Explorer", "エクスプローラで開く"));
            put(LangID.MIDI_Data_byte, new LanguageWords("MIDI Data byte（Hex）", "MIDIデータバイト（Hex）"));
            put(LangID.Send, new LanguageWords("Send", "送信"));
            put(LangID.Invalid_byte_data, new LanguageWords("Invalid byte data", "無効なバイトデータ"));
            put(LangID.License, new LanguageWords("License", "ライセンス"));
            put(LangID.Above_conditions, new LanguageWords("Above conditions：", "上記の条件に："));
            put(LangID.Accept, new LanguageWords("Accept", "同意する"));
            put(LangID.Reject, new LanguageWords("Reject", "同意しない"));
            put(LangID.Copied_to_clipboard, new LanguageWords("Copied to clipboard.", "クリップボードにコピーしました。"));
            put(LangID.Original_copy, new LanguageWords("Original copy", "原文のコピー"));
            put(LangID.Common_settings, new LanguageWords("Common settings", "共通設定"));
            put(LangID.Apply, new LanguageWords("Apply", "適用"));
            put(LangID.Error, new LanguageWords("Error", "エラー"));
            put(LangID.Message, new LanguageWords("Message", "メッセージ"));
            put(LangID.Layout_initialization, new LanguageWords("Layout initialization", "レイアウト初期化"));
            put(LangID.Tool, new LanguageWords("Tool", "ツール"));
            put(LangID.FFmpeg_converter, new LanguageWords("FFmpeg converter", "FFmpegコンバータ"));
            put(LangID.FFmpeg_path, new LanguageWords("FFmpeg path(.exe)", "FFmpeg実行ファイル(.exe)"));
            put(LangID.Output_directory, new LanguageWords("Output directory", "出力フォルダ"));
            put(LangID.Input_file, new LanguageWords("Input file", "入力ファイル"));
            put(LangID.Play_after_convert, new LanguageWords("Play after convert", "変換後に再生する"));
            put(LangID.Convert, new LanguageWords("Convert", "変換"));
            put(LangID.A_is_invalid, new LanguageWords("[NAME] is invalid.", "[NAME]が無効です。"));
            put(LangID.Conversion_failed, new LanguageWords("Conversion failed.", "変換失敗。"));
            put(LangID.Conversion_completed, new LanguageWords("Conversion completed.", "変換完了。"));
            put(LangID.Now_converting, new LanguageWords("Now converting.", "変換中。"));
            put(LangID.Initialize_setting, new LanguageWords("Initialize setting", "初期設定に戻す"));
        }
    };
}
