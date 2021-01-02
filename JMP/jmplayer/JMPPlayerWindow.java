package jmplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jlib.player.IPlayer;
import jlib.plugin.IPlugin;
import jmp.FileResult;
import jmp.IFileResultCallback;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.PluginManager;
import jmp.core.SoundManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.JmpPlayerLaunch;
import jmp.gui.VersionInfoDialog;
import jmp.gui.ui.ControlButtonUI;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.IButtonMarkPaint;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.IJMPComponentUI;
import jmp.gui.ui.SequencerSliderUI;
import jmp.lang.DefineLanguage;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;
import lib.MakeJmpConfig;
import lib.MakeJmpLib;

/**
 * MIDIプレイヤーメインウィンドウクラス
 *
 * @author abs
 *
 */
public class JMPPlayerWindow extends JFrame implements WindowListener, IJmpMainWindow, IJMPComponentUI, IDropFileCallback {

    private class JmpMenuListener implements MenuListener {

        @Override
        public void menuSelected(MenuEvent e) {
            updateMenuState();
        }

        @Override
        public void menuDeselected(MenuEvent e) {
        }

        @Override
        public void menuCanceled(MenuEvent e) {
        }

    }

    // ! アプリケーション名
    public static final String APP_TITLE = JMPCore.APPLICATION_NAME;
    // ! タイマーによるカラートグル制御
    public static boolean TimerColorToggleFlag = false;
    // ! ステータスカラー(File)
    public static final Color STATUS_COLOR_FILE = Color.CYAN;
    // ! ステータスカラー(Pass)
    public static final Color STATUS_COLOR_PASS = Color.GREEN;
    // ! ステータスカラー(Fail)
    public static final Color STATUS_COLOR_FAIL = Color.RED;
    // ! コントロールボタン背景色
    // public static final Color CONTROL_BTN_BACKGROUND =
    // Utility.convertCodeToHtmlColor("#888888");

    // ! カレントタイムフォーマット
    public static final String CURRENT_TIME_FORMAT = "%s / %s ";

    private static long s_tmpSliderTick = -1;

    private VersionInfoDialog versionInfoDialog = null;

    private String lyric = "";

    // コンポーネント
    private JMenu pluginMenu;
    private JLabel statusLabel;
    private JSlider slider;
    private JButton playButton;
    private JButton prev2Button;
    private JButton prevButton;
    private JButton nextButton;
    private JButton next2Button;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JCheckBoxMenuItem alwayTopCheckBox;
    private JCheckBoxMenuItem autoPlayCheckBox;
    private JMenu playerMenu;
    private JMenuItem removePluginMenuItem;
    private JCheckBoxMenuItem loopPlayCheckBoxMenuItem;
    private JMenuItem mntmMidiDeviceSetup;
    private JMenuItem zipGenerateMenuItem;
    private JMenuItem removeAllPluginMenuItem;
    private JLabel labelPositionTime;
    private JCheckBoxMenuItem chckbxmntmStartupmidisetup;
    private JLabel lblDebugMenu;
    private JMenuItem allClosePluginMenuItem;
    private JMenu mnExecuteBatFile;
    private JMenuItem mntmMidiMonitor;
    private JMenuItem addPluginMenuItem;
    private JMenuItem mntmMidimessagesender;
    private JMenuItem openItem;
    private JMenuItem mntmReload;
    private JMenuItem mntmVersion;
    private JMenuItem endItem;
    private JMenu windowMenu;
    private JMenuItem playlistItem;
    private JMenuItem menuItemHistory;
    private JLabel lblMidi;
    private JMenuItem mntmPcreset;
    private JMenu configMenu;
    private JLabel lblCommon;
    private JMenuItem menuItemLanguage;
    private JPanel panel_1;
    private JPanel panel_2;
    private JPanel panel_3;
    private JPanel panel_4;
    private JPanel panel_5;
    private JPanel panel_6;
    private JMenuItem mntmInitLayout;
    private JMenuItem mntmFFmpegConverter;
    private JMenu mnTool;
    private JMenuItem mntmInitializeConfig;
    private JMenuItem mntmCallMakeJMP;
    private JMenuItem mntmReloadJmzFolder;
    private JMenuItem mntmMidiExport;
    private JMenuItem mntmDebugDummy;
    private JMenuItem mntmPluginManager;
    private JCheckBoxMenuItem chckbxmntmLyricView;
    private JCheckBoxMenuItem chckbxmntmSendSystemSetupBeforePlayback;
    private JMenuItem mntmJmSynth;
    private JLabel lblVolumeSlider;
    private JSlider mntmVolumeSlider;
    private JMenuItem mntmPlayInit;

    /**
     * コンストラクタ(WindowBuilderによる自動生成)
     *
     *
     * @throws HeadlessException
     */
    public JMPPlayerWindow() throws HeadlessException {
        super();

        versionInfoDialog = new VersionInfoDialog();

        setTitle(APP_TITLE);
        this.addWindowListener(this);
        this.setTransferHandler(new DropFileCallbackHandler(this));
        this.setBounds(WindowManager.DEFAULT_PLAYER_WINDOW_SIZE);

        getContentPane().setLayout(new BorderLayout(0, 0));

        panel_2 = new JPanel();
        panel_2.setBackground(Color.BLACK);
        getContentPane().add(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new GridLayout(0, 5, 15, 0));

        prev2Button = new JButton("←←");
        panel_1.add(prev2Button);
        prev2Button.setToolTipText("前の曲へ");

        prevButton = new JButton("←");
        panel_1.add(prevButton);
        prevButton.setToolTipText("巻き戻し");

        playButton = new JButton("再生");
        panel_1.add(playButton);
        playButton.setToolTipText("再生/停止");

        nextButton = new JButton("→");
        panel_1.add(nextButton);
        nextButton.setToolTipText("早送り");

        next2Button = new JButton("→→");
        panel_1.add(next2Button);
        next2Button.setToolTipText("次の曲へ");

        panel_3 = new JPanel();
        panel_3.setBounds(36, 237, 495, 46);
        panel.add(panel_3, BorderLayout.SOUTH);
        panel_3.setLayout(new BorderLayout(0, 0));

        slider = new JSlider();
        panel_3.add(slider, BorderLayout.CENTER);
        slider.setForeground(new Color(255, 255, 255));
        slider.setValue(0);

        panel_4 = new JPanel();
        panel_3.add(panel_4, BorderLayout.SOUTH);
        panel_4.setLayout(new GridLayout(0, 2, 0, 0));

        if (JMPCore.getWindowManager() == null) {
            /* デザイナーがエラーになるためJPanelに差し替える */
            panel_6 = new JPanel();
        }
        else {
            panel_6 = new JmpPlayerLaunch();
        }
        panel_4.add(panel_6);

        labelPositionTime = new JLabel(String.format(CURRENT_TIME_FORMAT, "00:00", "00:00"));
        labelPositionTime.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPositionTime.setFont(new Font("Dialog", Font.BOLD, 18));
        panel_4.add(labelPositionTime);
        labelPositionTime.setForeground(Color.WHITE);

        panel_5 = new JPanel();
        panel_5.setBorder(new LineBorder(Color.WHITE, 2));
        panel_5.setBackground(Color.BLACK);
        panel_2.add(panel_5, BorderLayout.SOUTH);
        panel_5.setLayout(new BorderLayout(0, 0));

        statusLabel = new JLabel(" ");
        panel_5.add(statusLabel);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                s_tmpSliderTick = slider.getValue();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                long value = s_tmpSliderTick;
                if (s_tmpSliderTick != -1) {
                    JMPCore.getSoundManager().getCurrentPlayer().setPosition(value);
                }
                s_tmpSliderTick = -1;
            }
        });
        slider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                s_tmpSliderTick = slider.getValue();
            }
        });
        next2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().playNextForList();
            }
        });
        nextButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                JMPCore.getSoundManager().fastForward();
            }
        });
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().togglePlayStop();
                repaint();
            }
        });
        prevButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JMPCore.getSoundManager().rewind();
            }
        });
        prev2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().playPrevForList();
            }
        });

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        fileMenu = new JMenu("ファイル");
        fileMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(fileMenu);

        openItem = new JMenuItem("開く");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileOpenFunc();
            }
        });
        fileMenu.add(openItem);

        endItem = new JMenuItem("終了");
        endItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit(true);
            }
        });

        mntmReload = new JMenuItem("リロード");
        mntmReload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getFileManager().reload();
            }
        });
        fileMenu.add(mntmReload);

        JSeparator separator = new JSeparator();
        fileMenu.add(separator);

        mntmVersion = new JMenuItem("バージョン情報");
        mntmVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                versionInfoDialog.start();
            }
        });
        fileMenu.add(mntmVersion);
        fileMenu.add(endItem);

        windowMenu = new JMenu("ウィンドウ");
        windowMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(windowMenu);

        alwayTopCheckBox = new JCheckBoxMenuItem("常に手前で表示");
        alwayTopCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setAlwaysOnTop(alwayTopCheckBox.isSelected());
            }
        });
        windowMenu.add(alwayTopCheckBox);

        mntmInitLayout = new JMenuItem("レイアウト初期化");
        mntmInitLayout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().initializeLayout();
                JMPCore.getWindowManager().setVisibleAll(false);
            }
        });
        windowMenu.add(mntmInitLayout);

        playerMenu = new JMenu("プレイヤー");
        playerMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(playerMenu);

        loopPlayCheckBoxMenuItem = new JCheckBoxMenuItem("ループ再生");
        loopPlayCheckBoxMenuItem.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JMPCore.getDataManager().setLoopPlay(loopPlayCheckBoxMenuItem.isSelected());
            }
        });

        mntmPlayInit = new JMenuItem("最初から再生");
        mntmPlayInit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().initPlay();
            }
        });
        playerMenu.add(mntmPlayInit);
        playerMenu.add(loopPlayCheckBoxMenuItem);

        autoPlayCheckBox = new JCheckBoxMenuItem("連続再生");
        playerMenu.add(autoPlayCheckBox);
        autoPlayCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JMPCore.getDataManager().setAutoPlay(autoPlayCheckBox.isSelected());
            }
        });

        playlistItem = new JMenuItem("プレイリスト");
        playerMenu.add(playlistItem);

        menuItemHistory = new JMenuItem("履歴");
        menuItemHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_HISTORY).showWindow();
            }
        });
        playerMenu.add(menuItemHistory);
        playlistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FILE_LIST);
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
        });

        pluginMenu = new JMenu("プラグイン");
        pluginMenu.addMenuListener(new JmpMenuListener());

        mnTool = new JMenu("Tool");
        mnTool.addMenuListener(new JmpMenuListener());
        menuBar.add(mnTool);

        mntmFFmpegConverter = new JMenuItem("FFmpeg converter");
        mnTool.add(mntmFFmpegConverter);
        mntmFFmpegConverter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FFMPEG).showWindow();
            }
        });

        mntmMidiMonitor = new JMenuItem("MIDIメッセージモニタ");
        mnTool.add(mntmMidiMonitor);

        mntmMidimessagesender = new JMenuItem("MIDIメッセージセンダー");
        mnTool.add(mntmMidimessagesender);
        mntmMidimessagesender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_SENDER);
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
        });
        mntmMidiMonitor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
        });
        menuBar.add(pluginMenu);

        addPluginMenuItem = new JMenuItem("プラグイン追加");
        addPluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                // FileNameExtensionFilter filter = new
                // FileNameExtensionFilter("PLUGIN SETUP Files(*.JMS)",
                // PluginManager.SETUP_FILE_EX);
                filechooser.setFileFilter(JmpUtil.createFileFilter("PLUGIN Files", PluginManager.PLUGIN_ZIP_EX));
                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        if (file.isDirectory() == false) {
                            if (Utility.checkExtension(file, PluginManager.SETUP_FILE_EX) == true) {
                                catchDropFile(file);
                            }
                            else if (Utility.checkExtension(file, PluginManager.PLUGIN_ZIP_EX) == true) {
                                catchDropFile(file);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        pluginMenu.add(addPluginMenuItem);

        removePluginMenuItem = new JMenuItem("プラグイン削除");
        removePluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(JmpUtil.createFileFilter("PLUGIN SETUP Files", PluginManager.SETUP_FILE_EX));

                File dir = new File(JMPCore.getSystemManager().getJmsDirPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            if (Utility.checkExtension(path, PluginManager.SETUP_FILE_EX) == true) {
                                File jmsFile = new File(path);
                                JMPCore.getPluginManager().reserveRemovePlugin(jmsFile);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        pluginMenu.add(removePluginMenuItem);

        mntmPluginManager = new JMenuItem("プラグイン管理");
        mntmPluginManager.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_PLUGIN_MANAGER).showWindow();
            }
        });
        pluginMenu.add(mntmPluginManager);

        allClosePluginMenuItem = new JMenuItem("すべて閉じる");
        allClosePluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getPluginManager().closeAllPlugins();
            }
        });
        pluginMenu.add(allClosePluginMenuItem);
        pluginMenu.addSeparator();
        pluginMenu.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                updatePluginEnable();
            }
        });

        configMenu = new JMenu("設定");
        configMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(configMenu);

        mntmMidiDeviceSetup = new JMenuItem("MIDIデバイス設定");
        mntmMidiDeviceSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.SMidiPlayer.getSelectSynthsizerDialog().start();
            }
        });

        lblCommon = new JLabel("-- 共通設定 --");
        lblCommon.setForeground(Color.DARK_GRAY);
        lblCommon.setFont(new Font("Dialog", Font.BOLD, 12));
        configMenu.add(lblCommon);

        menuItemLanguage = new JMenuItem("言語設定");
        menuItemLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LANGUAGE).showWindow();
            }
        });
        configMenu.add(menuItemLanguage);

        mntmInitializeConfig = new JMenuItem("InitializeConfig");
        mntmInitializeConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = Utility.openInfomationDialog(JMPPlayerWindow.this, "", JMPCore.getLanguageManager().getLanguageStr(LangID.Initialize_setting));
                if (res == Utility.CONFIRM_RESULT_YES) {
                    JMPCore.getSystemManager().initializeAllSetting();
                }
            }
        });
        configMenu.add(mntmInitializeConfig);

        lblMidi = new JLabel("-- MIDI設定 --");
        lblMidi.setForeground(Color.DARK_GRAY);
        lblMidi.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMidi.setHorizontalAlignment(SwingConstants.LEFT);
        configMenu.add(lblMidi);
        configMenu.add(mntmMidiDeviceSetup);

        chckbxmntmStartupmidisetup = new JCheckBoxMenuItem("起動時にMIDIデバイス設定を開く");
        chckbxmntmStartupmidisetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().setShowStartupDeviceSetup(chckbxmntmStartupmidisetup.isSelected());
            }
        });
        configMenu.add(chckbxmntmStartupmidisetup);

        mntmPcreset = new JMenuItem("PCリセット");
        mntmPcreset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().resetMidiEvent();
            }
        });
        configMenu.add(mntmPcreset);

        chckbxmntmLyricView = new JCheckBoxMenuItem("Lyric");
        chckbxmntmLyricView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().setLyricView(chckbxmntmLyricView.isSelected());
            }
        });
        configMenu.add(chckbxmntmLyricView);

        chckbxmntmSendSystemSetupBeforePlayback = new JCheckBoxMenuItem("Send system setup before playback");
        chckbxmntmSendSystemSetupBeforePlayback.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().setSendMidiSystemSetup(chckbxmntmSendSystemSetupBeforePlayback.isSelected());
            }
        });
        chckbxmntmSendSystemSetupBeforePlayback.setSelected(JMPCore.getDataManager().isSendMidiSystemSetup());
        configMenu.add(chckbxmntmSendSystemSetupBeforePlayback);

        mntmJmSynth = new JMenuItem("Built-in synth setup");
        mntmJmSynth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JMPCore.getWindowManager().isValidBuiltinSynthFrame() == false) {
                    return;
                }
                JMPCore.getWindowManager().openBuiltinSynthFrame();
            }
        });
        configMenu.add(mntmJmSynth);

        lblDebugMenu = new JLabel("-- Developer menu --");
        lblDebugMenu.setForeground(Color.DARK_GRAY);
        lblDebugMenu.setFont(new Font("Dialog", Font.BOLD, 12));
        lblDebugMenu.setHorizontalAlignment(SwingConstants.LEFT);
        configMenu.add(lblDebugMenu);

        removeAllPluginMenuItem = new JMenuItem("Remove all plugins");
        removeAllPluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = Utility.openInfomationDialog(JMPPlayerWindow.this, "", "Are you sure you want to remove all plugins?");
                if (res != Utility.CONFIRM_RESULT_YES) {
                    return;
                }

                File dir = new File(JMPCore.getSystemManager().getJmsDirPath());
                for (File f : dir.listFiles()) {

                    if (Utility.checkExtension(f.getPath(), PluginManager.SETUP_FILE_EX) == false) {
                        continue;
                    }
                    JMPCore.getPluginManager().reserveRemovePlugin(f, false);
                }
            }
        });
        configMenu.add(removeAllPluginMenuItem);

        mntmCallMakeJMP = new JMenuItem("Invoke \"mkJMP\"");
        mntmCallMakeJMP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MakeJmpLib.call(MakeJmpLib._CMD_WIN, MakeJmpLib._CMD_EXP);
            }
        });
        configMenu.add(mntmCallMakeJMP);

        zipGenerateMenuItem = new JMenuItem("Generate importing \"jmz\"");
        zipGenerateMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = Utility.pathCombin(Platform.getCurrentPath(false), "_jmz");
                File dir = new File(path);
                if (dir.exists() == false) {
                    dir.mkdir();
                }
                if (dir.exists() == true) {
                    JMPCore.getPluginManager().generatePluginZipPackage(dir.getPath());
                    try {
                        Utility.openExproler(dir);
                    }
                    catch (IOException e1) {
                    }
                }
                else {
                    System.out.println("Not make zip dir.");
                }
            }
        });
        configMenu.add(zipGenerateMenuItem);

        mnExecuteBatFile = new JMenu("Execute \"mkj\" file");
        mnExecuteBatFile.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                File current = new File(Platform.getCurrentPath(false));

                mnExecuteBatFile.removeAll();

                JMenuItem execAllItem = new JMenuItem("ExecuteAll");
                execAllItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (File f : current.listFiles()) {
                            if (Utility.checkExtension(f, MakeJmpLib.PKG_PROJECT_CFG_EX) == true) {
                                MakeJmpConfig mkjConfig = null;
                                try {
                                    mkjConfig = new MakeJmpConfig(f);
                                    MakeJmpLib.exportPackage(mkjConfig);
                                }
                                catch (Exception ioe) {
                                }
                            }
                        }
                    }
                });
                mnExecuteBatFile.add(execAllItem);
                mnExecuteBatFile.addSeparator();

                for (File f : current.listFiles()) {
                    if (Utility.checkExtension(f, MakeJmpLib.PKG_PROJECT_CFG_EX) == true) {
                        JMenuItem item = new JMenuItem(Utility.getFileNameAndExtension(f));
                        item.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                MakeJmpConfig mkjConfig = null;
                                try {
                                    mkjConfig = new MakeJmpConfig(f);
                                    MakeJmpLib.exportPackage(mkjConfig);
                                }
                                catch (Exception ioe) {
                                }
                            }
                        });
                        mnExecuteBatFile.add(item);
                    }
                }
            }
        });
        configMenu.add(mnExecuteBatFile);

        mntmReloadJmzFolder = new JMenuItem("Reload \"jmz\" Folder");
        mntmReloadJmzFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getPluginManager().readingJmzDirectoryConfirm();
            }
        });
        configMenu.add(mntmReloadJmzFolder);

        mntmMidiExport = new JMenuItem("Midi export");
        mntmMidiExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SystemManager system = JMPCore.getSystemManager();
                String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
                String[] exMUSICXML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSICXML));

                File defaultDir = new File(Platform.getCurrentPath());
                String loadedFile = JMPCore.getDataManager().getLoadedFile();
                if (Utility.checkExtensions(loadedFile, exMIDI) == true || Utility.checkExtensions(loadedFile, exMUSICXML) == true) {
                    String defaultFileName = Utility.getFileNameAndExtension(loadedFile);
                    defaultFileName = Utility.getFileNameNotExtension(defaultFileName) + ".mid";
                    File dst = Utility.openSaveFileDialog(null, defaultDir, defaultFileName);
                    if (dst != null) {
                        try {
                            SoundManager.SMidiPlayer.saveFile(dst);
                            Utility.openExproler(dst);
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        configMenu.add(mntmMidiExport);

        mntmDebugDummy = new JMenuItem("Dummy");
        mntmDebugDummy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Long> data = SoundManager.SWavPlayer.sampling(100);
                    for (long value : data) {
                        System.out.println(value);
                    }
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        configMenu.add(mntmDebugDummy);

        lblVolumeSlider = new JLabel("-- Volume --");
        lblVolumeSlider.setForeground(Color.DARK_GRAY);
        lblVolumeSlider.setFont(new Font("Dialog", Font.BOLD, 12));
        lblVolumeSlider.setHorizontalAlignment(SwingConstants.LEADING);
        playerMenu.add(lblVolumeSlider);
        mntmVolumeSlider = new JSlider(0, 100);
        mntmVolumeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int value = mntmVolumeSlider.getValue();
                float vol = (float)value / (float)mntmVolumeSlider.getMaximum();
                JMPCore.getSoundManager().setLineVolume(vol);
            }
        });
        mntmVolumeSlider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int value = mntmVolumeSlider.getValue();
                float vol = (float)value / (float)mntmVolumeSlider.getMaximum();
                JMPCore.getSoundManager().setLineVolume(vol);
            }
        });
        playerMenu.add(mntmVolumeSlider);

    }

    @Override
    public void updateDebugMenu() {
        /* 開発者用用メニュー表示 */
        boolean enable;
        if (JMPFlags.DebugMode == true) {
            enable = true;
        }
        else {
            enable = false;
        }

        lblDebugMenu.setVisible(enable);
        zipGenerateMenuItem.setVisible(enable);
        removeAllPluginMenuItem.setVisible(enable);
        mnExecuteBatFile.setVisible(enable);
        mntmCallMakeJMP.setVisible(enable);
        mntmReloadJmzFolder.setVisible(enable);
        mntmMidiExport.setVisible(enable);
        mntmDebugDummy.setVisible(enable);
    }

    @Override
    public void initializeLayout() {
        IJmpMainWindow.super.initializeLayout();

        updateMenuState();
        setAlwaysOnTop(false);
        setSize(WindowManager.DEFAULT_PLAYER_WINDOW_SIZE.width, WindowManager.DEFAULT_PLAYER_WINDOW_SIZE.height);
    }

    @Override
    public void initializeSetting() {
        DataManager dm = JMPCore.getDataManager();

        // アイコン設定
        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        // 色設定
        updateBackColor();

        setInitializeStatusText();

        // プラグイン追加
        for (String name : JMPCore.getPluginManager().getPluginsNameSet()) {
            IPlugin plugin = JMPCore.getPluginManager().getPlugin(name);
            if (plugin == null) {
                continue;
            }

            addPluginMenu(name, plugin);
        }

        if (JMPFlags.NonPluginLoadFlag == true) {
            pluginMenu.setEnabled(false);
        }

        /* Windows用の処理 */
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            mnExecuteBatFile.setVisible(false);
            // mntmFFmpegConverter.setVisible(false);
        }

        /* スタンドアロンモード用メニュー表示 */
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            pluginMenu.setVisible(false);
            mnTool.setVisible(false);
        }

        setUI();
        alwayTopCheckBox.setSelected(false);
        autoPlayCheckBox.setSelected(dm.isAutoPlay());
        loopPlayCheckBoxMenuItem.setSelected(dm.isLoopPlay());
        chckbxmntmStartupmidisetup.setSelected(dm.isShowStartupDeviceSetup());

        int mntmVolumeSliderValue = (int)(JMPCore.getSoundManager().getLineVolume() * (float)mntmVolumeSlider.getMaximum());
        mntmVolumeSlider.setValue(mntmVolumeSliderValue);

        // シーケンスバーのトグル用コールバック関数を登録
        JMPCore.getTaskManager().addCallbackPackage((long) 500, new ICallbackFunction() {

            @Override
            public void callback() {
                IPlayer player = JMPCore.getSoundManager().getCurrentPlayer();
                if (player != null) {
                    if (player.isRunnable() == true) {
                        // 再生バーのトグル
                        TimerColorToggleFlag = !(TimerColorToggleFlag);
                    }
                }
            }
        });

        // ロード後のコールバックを登録
        JMPCore.getFileManager().addLoadResultCallback(new IFileResultCallback() {

            @Override
            public void end(FileResult result) {
                setStatusTextForFile();
                setStatusText(result.statusMsg, result.status);
            }

            @Override
            public void begin(FileResult result) {
                if (result.status == false) {
                    setStatusText(result.statusMsg, result.status);
                }
                else {
                    setStatusText(result.statusMsg, Color.LIGHT_GRAY);
                }
            }
        });

        // 開発者メニュー
        updateDebugMenu();
    }

    public void setInitializeStatusText() {
        setStatusText(JMPCore.getLanguageManager().getLanguageStr(LangID.D_and_D_the_playback_file_here), Color.ORANGE);
    }

    /**
     * カスタムUI設定<br>
     * （コンストラクタでコールするとデザイナーがエラーになるため、mainでコールする。）
     */
    private void setUI() {
        slider.setUI(new SequencerSliderUI(slider));

        // 再生ボタンUI
        ControlButtonUI playUI = new ControlButtonUI();
        playUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                if (JMPCore.getSoundManager().getCurrentPlayer().isValid() == true && JMPCore.getSoundManager().getCurrentPlayer().isRunnable() == true) {
                    // 停止ボタン
                    Image img = JMPCore.getResourceManager().getBtnStopIcon();
                    if (img != null) {
                        int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                        int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                        imgX = (imgX < 0) ? 0 : imgX;
                        imgY = (imgY < 0) ? 0 : imgY;
                        g.drawImage(img, imgX, imgY, null);
                        return;
                    }
                }
                else {
                    // 再生ボタン
                    Image img = JMPCore.getResourceManager().getBtnPlayIcon();
                    if (img != null) {
                        int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                        int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                        imgX = (imgX < 0) ? 0 : imgX;
                        imgY = (imgY < 0) ? 0 : imgY;
                        g.drawImage(img, imgX, imgY, null);
                        return;
                    }
                }
            }
        });
        playButton.setUI(playUI);

        // 早送りUI
        ControlButtonUI nextUI = new ControlButtonUI();
        nextUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnNextIcon();
                if (img != null) {
                    int imgX = (nextButton.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (nextButton.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }
            }
        });
        nextButton.setUI(nextUI);

        // 巻き戻しUI
        ControlButtonUI prevUI = new ControlButtonUI();
        prevUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnPrevIcon();
                if (img != null) {
                    int imgX = (prevButton.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (prevButton.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }
            }
        });
        prevButton.setUI(prevUI);

        // 次へUI
        ControlButtonUI next2UI = new ControlButtonUI();
        next2UI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnNext2Icon();
                if (img != null) {
                    int imgX = (next2Button.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (next2Button.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }
            }
        });
        next2Button.setUI(next2UI);

        // 前へUI
        ControlButtonUI prev2UI = new ControlButtonUI();
        prev2UI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnPrev2Icon();
                if (img != null) {
                    int imgX = (prev2Button.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (prev2Button.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }
            }
        });
        prev2Button.setUI(prev2UI);

    }

    @Override
    public void update() {
        String posStr = "00:00";
        String lengthStr = "00:00";
        if (JMPCore.getSoundManager().getCurrentPlayer().isValid() == true) {
            slider.setMaximum((int) JMPCore.getSoundManager().getCurrentPlayer().getLength());
            if (s_tmpSliderTick == -1) {
                slider.setValue((int) JMPCore.getSoundManager().getCurrentPlayer().getPosition());
            }

            posStr = JMPCore.getSoundManager().getPositionTimeString();
            lengthStr = JMPCore.getSoundManager().getLengthTimeString();
        }
        else {
            s_tmpSliderTick = -1;
            slider.setMaximum(100);
            slider.setValue(0);

            posStr = JMPCore.getSoundManager().getPlayerTimeString(0);
            lengthStr = JMPCore.getSoundManager().getPlayerTimeString(0);
        }
        labelPositionTime.setText(String.format(CURRENT_TIME_FORMAT, posStr, lengthStr));

        // slider.setToolTipText(String.valueOf(slider.getValue()));
    }

    /**
     * アプリケーション終了
     */
    public void exit() {
        exit(JMPCore.isEnableStandAlonePlugin() == false);
    }

    public void exit(boolean forcedExit) {
        setVisible(false);

        if (forcedExit == true) {
            JMPCore.getSoundManager().stop();

            // JMPリソースの終了処理
            JMPLoader.exit();
        }
    }

    /**
     * プラグイン追加
     *
     * @param plugin
     *            プラグイン
     */
    public void addPluginMenu(String name, IPlugin plugin) {
        JMPCore.getWindowManager().addPluginMenuItem(name, plugin);
        pluginMenu.removeAll();

        pluginMenu.add(addPluginMenuItem);
        pluginMenu.add(removePluginMenuItem);
        pluginMenu.add(mntmPluginManager);
        pluginMenu.add(allClosePluginMenuItem);
        pluginMenu.addSeparator();
        for (JMenuItem item : JMPCore.getWindowManager().getPluginMenuItems()) {
            pluginMenu.add(item);
        }
    }

    private void updatePluginEnable() {
        for (int i = 0; i < pluginMenu.getItemCount(); i++) {
            JMenuItem item = pluginMenu.getItem(i);
            if (item != null) {
                String name = item.getText();
                IPlugin plg = JMPCore.getPluginManager().getPlugin(name);
                if (plg == null) {
                    continue;
                }

                if (plg.isEnable() == true) {
                    item.setEnabled(true);
                }
                else {
                    item.setEnabled(false);
                }

                // 何もロードされていない場合は、常に有効状態にする
                if (JMPCore.getDataManager().getLoadedFile().isEmpty() == true) {
                    item.setEnabled(true);
                }
            }
        }
        boolean isPlay = JMPCore.getSoundManager().isPlay();
        addPluginMenuItem.setEnabled(!isPlay);
        removePluginMenuItem.setEnabled(!isPlay);
        allClosePluginMenuItem.setEnabled(!isPlay);
    }

    private void updateMenuState() {
        // 設定値とメニューの同期
        DataManager dm = JMPCore.getDataManager();
        chckbxmntmStartupmidisetup.setSelected(dm.isShowStartupDeviceSetup());
        loopPlayCheckBoxMenuItem.setSelected(dm.isLoopPlay());
        autoPlayCheckBox.setSelected(dm.isAutoPlay());
        alwayTopCheckBox.setSelected(isAlwaysOnTop());
        chckbxmntmLyricView.setSelected(dm.isLyricView());
        mntmJmSynth.setEnabled(JMPCore.getWindowManager().isValidBuiltinSynthFrame());

        // 音量バー同期
        JMPCore.getSoundManager().syncLineVolume();
        int mntmVolumeSliderValue = (int)(JMPCore.getSoundManager().getLineVolume() * (float)mntmVolumeSlider.getMaximum());
        mntmVolumeSlider.setValue(mntmVolumeSliderValue);

        if (JMPCore.getSoundManager().getSequencer() != null) {
            // Sequencerが実行中の場合は設定不可
            boolean isRunning = JMPCore.getSoundManager().getSequencer().isRunning();
            mntmMidiDeviceSetup.setEnabled(!isRunning);
        }
    }

    /**
     * ファイルオープン処理
     */
    public void fileOpenFunc() {
        SystemManager system = JMPCore.getSystemManager();
        String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
        String[] exWAV = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV));
        String[] exMUSICXML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSICXML));

        // ファイルフィルター
        JFileChooser filechooser = new JFileChooser();
        filechooser.addChoosableFileFilter(JmpUtil.createFileFilter("MIDI Files", exMIDI));
        filechooser.addChoosableFileFilter(JmpUtil.createFileFilter("WAV Files", exWAV));
        filechooser.addChoosableFileFilter(JmpUtil.createFileFilter("MusicXML Files", exMUSICXML));

        File dir = new File(JMPCore.getDataManager().getPlayListPath());
        filechooser.setCurrentDirectory(dir);
        int selected = filechooser.showOpenDialog(getParent());
        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                File file = filechooser.getSelectedFile();
                String path = file.getPath();
                if (file.isDirectory() == false) {
                    // ファイルロード
                    JMPCore.getFileManager().loadFileToPlay(path);
                }
                break;
            default:
                break;
        }
    }

    /**
     * ステータスメッセージ設定
     *
     * @param text
     *            メッセージ
     */
    public void setStatusText(String text) {
        setStatusText(text, true);
    }

    public void setStatusText(String text, boolean isPassColor) {
        if (isPassColor == true) {
            setStatusText(text, STATUS_COLOR_PASS);
        }
        else {
            setStatusText(text, STATUS_COLOR_FAIL);
        }

        JMPCore.getTaskManager().addCallbackPackage((long) 3000, new ICallbackFunction() {

            @Override
            public void callback() {
                setStatusTextForFile();
                repaint();
            }

            @Override
            public boolean isDeleteConditions(int count) {
                return true;
            }
        });
    }

    /**
     * ステータスメッセージ設定
     *
     * @param text
     *            メッセージ
     * @param color
     *            テキストカラー
     */
    public void setStatusText(String text, Color color) {
        if (text.isEmpty() == true) {
            // ブランクの禁止
            text = " ";
        }

        statusLabel.setForeground(color);
        statusLabel.setText(text);
        statusLabel.repaint();
    }

    /**
     * ファイル用ステータスメッセージ設定
     *
     * @param text
     *            メッセージ
     * @param color
     *            テキストカラー
     */
    public void setStatusTextForFile() {
        DataManager dm = JMPCore.getDataManager();
        if (dm.getLoadedFile().equals("") == false) {
            String name = Utility.getFileNameAndExtension(dm.getLoadedFile());
            setStatusText(name, STATUS_COLOR_FILE);
        }
        else {
            setStatusText(" ", STATUS_COLOR_FILE);
        }
    }

    private void setStatusTextForLyric() {
        if (lyric.isEmpty() == true) {
            setStatusTextForFile();
        }
        else {
            setStatusText(lyric, Color.PINK);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        exit();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void showWindow() {
        setVisible(true);
    }

    @Override
    public void hideWindow() {
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            setVisible(false);
        }
    }

    @Override
    public boolean isWindowVisible() {
        return isVisible();
    }

    @Override
    public void updateLanguage() {
        LanguageManager lm = JMPCore.getLanguageManager();

        playButton.setToolTipText(lm.getLanguageStr(LangID.Playback) + "/" + lm.getLanguageStr(LangID.Stop));
        prevButton.setToolTipText(lm.getLanguageStr(LangID.Rewind));
        nextButton.setToolTipText(lm.getLanguageStr(LangID.Fast_forward));
        prev2Button.setToolTipText(lm.getLanguageStr(LangID.Previous));
        next2Button.setToolTipText(lm.getLanguageStr(LangID.Next));

        fileMenu.setText(lm.getLanguageStr(LangID.File));
        openItem.setText(lm.getLanguageStr(LangID.Open));
        mntmReload.setText(lm.getLanguageStr(LangID.Reload));
        mntmVersion.setText(lm.getLanguageStr(LangID.Version));
        endItem.setText(lm.getLanguageStr(LangID.Exit));

        windowMenu.setText(lm.getLanguageStr(LangID.Window));
        alwayTopCheckBox.setText(lm.getLanguageStr(LangID.Allways_on_top));

        playerMenu.setText(lm.getLanguageStr(LangID.Player));
        loopPlayCheckBoxMenuItem.setText(lm.getLanguageStr(LangID.Loop_playback));
        autoPlayCheckBox.setText(lm.getLanguageStr(LangID.Continuous_playback));
        playlistItem.setText(lm.getLanguageStr(LangID.Playlist));
        menuItemHistory.setText(lm.getLanguageStr(LangID.History));
        mntmPlayInit.setText(lm.getLanguageStr(LangID.Play_from_the_beginning));

        pluginMenu.setText(lm.getLanguageStr(LangID.Plugin));
        addPluginMenuItem.setText(lm.getLanguageStr(LangID.Add_plugin));
        removePluginMenuItem.setText(lm.getLanguageStr(LangID.Remove_plugin));
        allClosePluginMenuItem.setText(lm.getLanguageStr(LangID.Close_all_plugin));

        configMenu.setText(lm.getLanguageStr(LangID.Setting));
        lblMidi.setText("-- " + lm.getLanguageStr(LangID.MIDI_settings) + " --");
        mntmMidiDeviceSetup.setText(lm.getLanguageStr(LangID.MIDI_device_settings));
        chckbxmntmStartupmidisetup.setText(lm.getLanguageStr(LangID.Open_MIDI_device_settings_on_startup));
        mntmPcreset.setText(lm.getLanguageStr(LangID.PC_reset));
        mntmMidiMonitor.setText(lm.getLanguageStr(LangID.MIDI_message_monitor));
        mntmMidimessagesender.setText(lm.getLanguageStr(LangID.MIDI_message_sender));
        lblCommon.setText("-- " + lm.getLanguageStr(LangID.Common_settings) + " --");
        if (JMPCore.getDataManager().getLanguage() == DefineLanguage.INDEX_LANG_ENGLISH) {
            menuItemLanguage.setText(lm.getLanguageStr(LangID.Language));
        }
        else {
            menuItemLanguage.setText(lm.getLanguageStr(LangID.Language) + "(Language)");
        }
        mntmInitLayout.setText(lm.getLanguageStr(LangID.Layout_initialization));

        mnTool.setText(lm.getLanguageStr(LangID.Tool));
        mntmFFmpegConverter.setText(lm.getLanguageStr(LangID.FFmpeg_converter));
        mntmInitializeConfig.setText(lm.getLanguageStr(LangID.Initialize_setting));
        mntmPluginManager.setText(lm.getLanguageStr(LangID.Plugin_manager));

        chckbxmntmLyricView.setText(lm.getLanguageStr(LangID.Lyrics_display));
        chckbxmntmSendSystemSetupBeforePlayback.setText(lm.getLanguageStr(LangID.Send_system_setup_before_playback));

        if (JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_LOADED_FILE).isEmpty() == true) {
            setInitializeStatusText();
        }
    }

    @Override
    public void catchDropFile(File file) {
        WindowManager wm = JMPCore.getWindowManager();
        LanguageManager lm = JMPCore.getLanguageManager();

        String path = file.getPath();
        if (Utility.checkExtension(path, PluginManager.PLUGIN_ZIP_EX) == true) {
            // プラグインロード
            if (JMPCore.getPluginManager().readingJmzPackageConfirm(path) == true) {
                wm.showInformationMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_SUCCESS));
            }
            else {
                wm.showErrorMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_ERROR));
            }
        }
        else if (Utility.checkExtension(path, PluginManager.SETUP_FILE_EX) == true) {
            if (JMPCore.getPluginManager().readingJmsFile(path) == true) {
                wm.showInformationMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_SUCCESS));
            }
            else {
                wm.showErrorMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_ERROR));
            }
        }
        else {
            JMPCore.getFileManager().loadFileToPlay(path);
        }
    }

    @Override
    public void updateConfig(String key) {
        IJmpMainWindow.super.updateConfig(key);
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_LYRIC_VIEW) == true) {
            lyric = "";
            setStatusTextForLyric();
            if (JMPCore.getDataManager().getLoadedFile().equals("") == true) {
                setInitializeStatusText();
            }
            else {
                setStatusTextForFile();
            }
        }
    }

    @Override
    public void updateBackColor() {
        getContentPane().setBackground(getJmpBackColor());
        panel_1.setBackground(getJmpBackColor());
        slider.setBackground(getJmpBackColor());
        panel_4.setBackground(getJmpBackColor());
        panel_6.setBackground(getJmpBackColor());
        prev2Button.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        prevButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        playButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        nextButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        next2Button.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
    }

    @Override
    public void setLyric(String text) {
        if (JMPCore.getDataManager().isLyricView() == true) {
            lyric = text;
            setStatusTextForLyric();
        }
    }
}
