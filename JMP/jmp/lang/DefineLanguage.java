package jmp.lang;

public class DefineLanguage {
    public static final int INDEX_LANG_JAPANESE = 0;
    public static final int INDEX_LANG_ENGLISH = 1;
    public static final int NUMBER_OF_INDEX_LANG = 2;

    public static enum LangID {
        Language, Japanese, English, File, Save, Open, Reload, Version, Exit, Window, Allways_on_top, Player, Loop_playback, Continuous_playback, Playlist, History, Plugin, Add_plugin, Remove_plugin, Close_all_plugin, Setting, MIDI_settings, MIDI_device_settings, Open_MIDI_device_settings_on_startup, PC_reset, MIDI_message_monitor, MIDI_message_sender, Now_loading, Plugin_error, FILE_ERROR_1, FILE_ERROR_2, FILE_ERROR_3, FILE_ERROR_4, FILE_ERROR_5, FILE_LOAD_SUCCESS, PLUGIN_LOAD_SUCCESS, PLUGIN_LOAD_ERROR, Clear, Playback, Stop, Fast_forward, Rewind, Next, Previous, Close, Add, Remove, Open_with_Explorer, MIDI_Data_byte, Send, Invalid_byte_data, License, Above_conditions, Accept, Reject, Copied_to_clipboard, Original_copy, Common_setting, Apply,
    }

    static java.util.HashMap<LangID, LanguageWords> langMap = new java.util.HashMap<LangID, LanguageWords>() {
        {
            put(LangID.Language, new LanguageWords("言語(Language)", "Language"));
            put(LangID.Japanese, new LanguageWords("日本語", "日本語"));
            put(LangID.English, new LanguageWords("English", "English"));
            put(LangID.File, new LanguageWords("ファイル", "File"));
            put(LangID.Save, new LanguageWords("保存", "Save"));
            put(LangID.Open, new LanguageWords("開く", "Open"));
            put(LangID.Reload, new LanguageWords("リロード", "Reload"));
            put(LangID.Version, new LanguageWords("バージョン情報", "Version"));
            put(LangID.Exit, new LanguageWords("終了", "Exit"));
            put(LangID.Window, new LanguageWords("ウィンドウ", "Window"));
            put(LangID.Allways_on_top, new LanguageWords("常に手前に表示", "Allways on top"));
            put(LangID.Player, new LanguageWords("プレイヤー", "Player"));
            put(LangID.Loop_playback, new LanguageWords("ループ再生", "Loop playback"));
            put(LangID.Continuous_playback, new LanguageWords("連続再生", "Continuous playback"));
            put(LangID.Playlist, new LanguageWords("プレイリスト", "Playlist"));
            put(LangID.History, new LanguageWords("履歴", "History"));
            put(LangID.Plugin, new LanguageWords("プラグイン", "Plugin"));
            put(LangID.Add_plugin, new LanguageWords("プラグインを追加", "Add plugin"));
            put(LangID.Remove_plugin, new LanguageWords("プラグインを削除", "Remove plugin"));
            put(LangID.Close_all_plugin, new LanguageWords("すべて閉じる", "Close all plugin"));
            put(LangID.Setting, new LanguageWords("設定", "Setting"));
            put(LangID.MIDI_settings, new LanguageWords("MIDI設定", "MIDI settings"));
            put(LangID.MIDI_device_settings, new LanguageWords("MIDIデバイス設定", "MIDI device settings"));
            put(LangID.Open_MIDI_device_settings_on_startup,
                    new LanguageWords("起動時にMIDIデバイス設定を開く", "Open MIDI device settings on startup"));
            put(LangID.PC_reset, new LanguageWords("プログラムチェンジリセット", "Program change reset"));
            put(LangID.MIDI_message_monitor, new LanguageWords("MIDIメッセージモニター", "MIDI message monitor"));
            put(LangID.MIDI_message_sender, new LanguageWords("MIDIメッセージ送信", "MIDI message sender"));
            put(LangID.Now_loading, new LanguageWords("ロード中", "Now loading"));
            put(LangID.Plugin_error, new LanguageWords("プラグインエラー", "Plugin error"));
            put(LangID.FILE_ERROR_1, new LanguageWords("※ファイルロード中に他のファイルをロードできません。",
                    "※Other files cannot be loaded while loading a file."));
            put(LangID.FILE_ERROR_2, new LanguageWords("※サポート外のファイル形式です", "※File format not supported."));
            put(LangID.FILE_ERROR_3,
                    new LanguageWords("※再生中はファイルロードできません。", "※Files cannot be loaded during playback."));
            put(LangID.FILE_ERROR_4, new LanguageWords("※ファイルを開くことができません。", "※The file cannot be opened."));
            put(LangID.FILE_ERROR_5, new LanguageWords("※ファイルのロードに失敗しました。", "※Failed to load the file."));
            put(LangID.FILE_LOAD_SUCCESS, new LanguageWords("ファイルロード成功", "File loading successful."));
            put(LangID.PLUGIN_LOAD_SUCCESS, new LanguageWords("プラグインロード成功", "Plugin loading successful."));
            put(LangID.PLUGIN_LOAD_ERROR, new LanguageWords("プラグインロード失敗", "Plugin loading failed."));
            put(LangID.Clear, new LanguageWords("クリア", "Clear"));
            put(LangID.Playback, new LanguageWords("再生", "Playback"));
            put(LangID.Stop, new LanguageWords("停止", "Stop"));
            put(LangID.Fast_forward, new LanguageWords("早送り", "Fast forward"));
            put(LangID.Rewind, new LanguageWords("巻き戻し", "Rewind"));
            put(LangID.Next, new LanguageWords("次へ", "Next"));
            put(LangID.Previous, new LanguageWords("前へ", "Previous"));
            put(LangID.Close, new LanguageWords("閉じる", "Close"));
            put(LangID.Add, new LanguageWords("追加", "Add"));
            put(LangID.Remove, new LanguageWords("削除", "Remove"));
            put(LangID.Open_with_Explorer, new LanguageWords("エクスプローラで開く", "Open with Explorer"));
            put(LangID.MIDI_Data_byte, new LanguageWords("MIDIデータバイト（Hex）", "MIDI Data byte（Hex）"));
            put(LangID.Send, new LanguageWords("送信", "Send"));
            put(LangID.Invalid_byte_data, new LanguageWords("無効なバイトデータ", "Invalid byte data"));
            put(LangID.License, new LanguageWords("ライセンス", "License"));
            put(LangID.Above_conditions, new LanguageWords("上記の条件に：", "Above_conditions："));
            put(LangID.Accept, new LanguageWords("同意する", "Accept"));
            put(LangID.Reject, new LanguageWords("同意しない", "Reject"));
            put(LangID.Copied_to_clipboard, new LanguageWords("クリップボードにコピーしました。", "Copied to clipboard."));
            put(LangID.Original_copy, new LanguageWords("原文のコピー", "Original copy"));
            put(LangID.Common_setting, new LanguageWords("共通設定", "Common setting"));
            put(LangID.Apply, new LanguageWords("適用", "Apply"));
        }
    };
}
