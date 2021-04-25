package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import function.Platform;
import function.Utility;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SoundManager;
import jmp.core.SystemManager;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.FileListTableModel;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.JMPFrame;
import jmp.lang.DefineLanguage.LangID;
import jmp.util.JmpUtil;

public class MidiFileListDialog extends JMPFrame {

    // private DefaultListModel<String> model;
    // private JList<String> midiFileList;
    private JTable midiFileList;
    private DefaultTableModel model;
    private final JPanel contentPanel = new JPanel();

    private Map<String, File> midiFileMap = null;

    private JList<String> playList;

    // 固有設定
    private static final String[] columnNames = new String[] { "", "Name" };
    public static final int COLUMN_TYPE = 0;
    public static final int COLUMN_NAME = 1;

    private static final int COL_SIZE = 25;
    private static final int ROW_SIZE = 20;
    private JLabel lblPath;

    private static final int WINDOW_WIDTH = 870;
    private static final int WINDOW_HEIGHT = 510;

    /**
     * Create the dialog.
     */
    public MidiFileListDialog() {
        super();
        setResizable(false);
        setTitle("再生リスト");

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        this.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (Utility.checkExtension(file, SoundManager.PLAYLIST_FILE_EXTENTION) == true) {
                    try {
                        JMPCore.getSoundManager().loadPlayList(file.getPath());
                    }
                    catch (IOException e) {
                    }
                }
                else {
                    updateList(file);
                }
            }
        }));

        model = new FileListTableModel(columnNames, 0);
        midiFileList = new JTable(model);
        midiFileList.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 12));
        midiFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (e.getClickCount() == 2) {
                    int row = midiFileList.rowAtPoint(p);
                    if (row >= 0) {
                        executeCell(row);
                    }
                }
                else {
                    int row = midiFileList.rowAtPoint(p);
                    if (row >= 0) {
                        updateCellInfo(row);
                    }
                }
            }
        });
        midiFileList.setLocation(462, 0);
        midiFileList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        midiFileList.setForeground(Color.BLACK);
        midiFileList.setBackground(Color.WHITE);
        midiFileList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        setBounds(100, 100, WINDOW_WIDTH, WINDOW_HEIGHT);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBackground(getJmpBackColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            scrollPane = new JScrollPane();
            scrollPane.setBounds(5, 39, 391, 390);
            contentPanel.add(scrollPane);
            {
                scrollPane.setViewportView(midiFileList);
            }
        }

        scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(478, 39, 364, 388);
        contentPanel.add(scrollPane_1);

        playList = JMPCore.getSoundManager().getPlayList();
        playList.setForeground(Color.WHITE);
        playList.setBackground(Color.BLACK);
        scrollPane_1.setViewportView(playList);
        btnExproler = new JButton("エクスプローラで開く");
        btnExproler.setBounds(5, 439, 146, 21);
        contentPanel.add(btnExproler);
        {
            addButton = new JButton("追加");
            addButton.setBounds(408, 210, 58, 21);
            contentPanel.add(addButton);
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (midiFileList != null) {
                        int[] selected = midiFileList.getSelectedRows();
                        if (selected.length > 0) {
                            for (int i = 0; i < selected.length; i++) {
                                String name = (String) midiFileList.getModel().getValueAt(selected[i], COLUMN_NAME);
                                if (midiFileMap.containsKey(name) == true) {
                                    File midiFile = midiFileMap.get(name);
                                    DefaultListModel<String> playList = (DefaultListModel<String>) JMPCore.getSoundManager().getPlayList().getModel();
                                    playList.addElement(midiFile.getPath());
                                }
                            }
                        }
                    }
                }
            });
            addButton.setActionCommand("");
            getRootPane().setDefaultButton(addButton);
        }

        btnLoad = new JButton("再生");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = JMPCore.getSoundManager().getPlayList().getSelectedIndex();
                if (index >= 0) {
                    if (JMPCore.getDataManager().isAutoPlay() == false) {
                        JMPCore.getDataManager().setAutoPlay(true);
                    }
                }
                else {
                    JMPCore.getSoundManager().getPlayList().setSelectedIndex(0);
                }

                JMPCore.getSoundManager().playCurrent();
            }
        });
        btnLoad.setBounds(751, 439, 91, 21);
        contentPanel.add(btnLoad);

        buttonClear = new JButton("クリア");
        buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> m = (DefaultListModel<String>) playList.getModel();
                m.clear();
            }
        });
        buttonClear.setBounds(545, 439, 91, 21);
        contentPanel.add(buttonClear);

        buttonDelete = new JButton("削除");
        buttonDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JList<String> list = JMPCore.getSoundManager().getPlayList();
                DefaultListModel<String> m = (DefaultListModel<String>) list.getModel();
                int[] selected = list.getSelectedIndices();
                if (selected.length > 0) {
                    for (int i = selected.length - 1; i >= 0; i--) {
                        m.remove(selected[i]);
                    }
                }
            }
        });
        buttonDelete.setBounds(648, 439, 91, 21);
        contentPanel.add(buttonDelete);

        btnDirectLoad = new JButton("開く or 再生");
        btnDirectLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeSelected();
            }
        });
        btnDirectLoad.setBounds(305, 439, 91, 21);
        contentPanel.add(btnDirectLoad);

        lblPath = new JLabel("PATH");
        lblPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openPath();
            }
        });
        lblPath.setBackground(Color.BLACK);
        lblPath.setForeground(Color.WHITE);
        lblPath.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        lblPath.setBounds(5, 8, 422, 21);
        lblPath.setOpaque(true);
        LineBorder border = new LineBorder(Color.LIGHT_GRAY, 1, false);
        lblPath.setBorder(border);
        contentPanel.add(lblPath);

        JButton btnBack = new JButton("<");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String curPath = JMPCore.getDataManager().getPlayListPath();
                File cur = new File(curPath);

                cur = cur.getParentFile();
                if (cur != null && cur.getPath().equalsIgnoreCase("") == false) {
                    updateList(cur);
                }
                else {
                    openPath();
                }
            }
        });
        btnBack.setBounds(427, 8, 39, 21);
        contentPanel.add(btnBack);

        labelContinuePlayback = new JLabel("連続再生リスト");
        labelContinuePlayback.setForeground(Color.WHITE);
        labelContinuePlayback.setFont(new Font("MS UI Gothic", Font.BOLD, 15));
        labelContinuePlayback.setBounds(478, 16, 252, 21);
        contentPanel.add(labelContinuePlayback);

        JPanel panel = new JmpQuickLaunch();
        panel.setBounds(742, 8, 100, 20);
        contentPanel.add(panel);
        btnExproler.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = JMPCore.getDataManager().getPlayListPath();
                try {
                    Utility.openExproler(path);
                }
                catch (IOException e1) {
                }
            }
        });
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(Color.DARK_GRAY);
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                buttonPane.setLayout(null);
            }
        }

        TableColumn colIco = midiFileList.getColumnModel().getColumn(COLUMN_TYPE);
        colIco.setMaxWidth(COL_SIZE);
        colIco.setMinWidth(COL_SIZE);
        // TableColumn colName =
        // midiFileList.getColumnModel().getColumn(COLUMN_NAME);
        // colName.setMaxWidth(150);
        // colName.setMinWidth(150);

        String listPath = JMPCore.getDataManager().getPlayListPath();
        if ((listPath.isEmpty() == true) || (Utility.isExsistFile(listPath) == false)) {
            updateList(Platform.getCurrentPath());
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            String path = JMPCore.getDataManager().getPlayListPath();
            if (path != null && path.isEmpty() == false) {
                updateList(path);
            }

            updateGUI();
        }
        super.setVisible(b);
    }

    public void updateGUI() {
        if (JMPCore.getDataManager().isAutoPlay() == true) {
            setVisibleExtendGUI(true);
        }
        else {
            setVisibleExtendGUI(false);
        }
    }

    public void setVisibleExtendGUI(boolean visible) {
        scrollPane_1.setVisible(visible);
        labelContinuePlayback.setVisible(visible);
        buttonClear.setVisible(visible);
        buttonDelete.setVisible(visible);
        btnLoad.setVisible(visible);
        addButton.setVisible(visible);

        if (visible == true) {
            this.setSize(WINDOW_WIDTH, this.getHeight());
            scrollPane.setSize(391, scrollPane.getHeight());
            btnDirectLoad.setLocation(305, btnDirectLoad.getY());
        }
        else {
            this.setSize(540, this.getHeight());
            scrollPane.setSize(510, scrollPane.getHeight());
            btnDirectLoad.setLocation(406, btnDirectLoad.getY());
        }
    }

    public void updateCellInfo(int row) {
        if (midiFileList != null) {
            if (row >= 0) {
                String name = (String) midiFileList.getModel().getValueAt(row, COLUMN_NAME);
                if (midiFileMap.containsKey(name) == true) {
                    LanguageManager lm = JMPCore.getLanguageManager();
                    File midiFile = midiFileMap.get(name);
                    if (midiFile.isDirectory() == true) {
                        btnDirectLoad.setText(lm.getLanguageStr(LangID.Open));
                    }
                    else {
                        btnDirectLoad.setText(lm.getLanguageStr(LangID.Playback));
                    }
                }
            }
        }
    }

    public void executeCell(int row) {
        if (midiFileList != null) {
            if (row >= 0) {
                String name = (String) midiFileList.getModel().getValueAt(row, COLUMN_NAME);
                if (midiFileMap.containsKey(name) == true) {
                    File midiFile = midiFileMap.get(name);
                    if (midiFile.isDirectory() == true) {
                        if (midiFile.exists() == true) {
                            updateList(midiFile);
                        }
                    }
                    else {
                        SoundManager sm = JMPCore.getSoundManager();
                        if (sm.isSupportedExtensionAccessor(Utility.getExtension(midiFile))) {
                            if (sm.isPlay() == true) {
                                sm.stop();
                            }
                        }

                        // 自動再生フラグ
                        JMPFlags.LoadToPlayFlag = true;
                        JMPCore.getFileManager().loadFile(midiFile);
                    }
                }
            }
        }
    }

    public void executeSelected() {
        if (midiFileList != null) {
            int selected = midiFileList.getSelectedRow();
            executeCell(selected);
        }
    }

    private void openPath() {
        String path = JMPCore.getDataManager().getPlayListPath();
        openPath(path);
    }

    private void openPath(String path) {
        JFileChooser filechooser = new JFileChooser();
        // filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (path != null && path.isEmpty() == false) {
            filechooser.setCurrentDirectory(new File(path));
        }

        int selected = filechooser.showOpenDialog(getParent());
        if (selected == JFileChooser.APPROVE_OPTION) {
            File file = filechooser.getSelectedFile();
            if (file.exists() == true && file.canRead() == true) {
                updateList(file);
            }
        }
    }

    private void removeAllRows() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    public void updateList(String path) {
        File f = new File(path);
        updateList(f);
    }

    public void updateList(File file) {
        if (file.canRead() == false) {
            return;
        }
        if (file.isDirectory() == false) {
            file = file.getParentFile();
        }
        if (file.canRead() == false) {
            return;
        }

        setCurrentPathText(file.getPath());
        JMPCore.getDataManager().setPlayListPath(file.getPath());

        midiFileMap = JMPCore.getFileManager().getFileList(file);
        removeAllRows();

        boolean validFFmpegPlayer = false;
        if (JMPCore.getDataManager().isUseFFmpegPlayer() == true && JMPCore.getSystemManager().isValidFFmpegWrapper() == true) {
            validFFmpegPlayer = true;
        }

        SystemManager system = JMPCore.getSystemManager();
        String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
        String[] exWAV = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV));
        String[] exMUSICXML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSICXML));
        String[] exMUSIC = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSIC));
        String[] exMML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MML));

        /* リスト構築 */
        if (midiFileMap != null) {
            // ファイル名ソート
            Object[] keys = midiFileMap.keySet().toArray();
            Arrays.sort(keys);

            List<File> list = new LinkedList<File>();
            // Directory
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isDirectory() == true) {
                    if (midiFileMap.containsKey(sKey) == true) {
                        list.add(f);
                    }
                }
            }
            // MIDI
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMIDI) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // WAV
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exWAV) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // MusicXML
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMUSICXML) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // MML
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMML) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // Music
            if (validFFmpegPlayer == true) {
                for (Object key : keys) {
                    String sKey = key.toString();
                    File f = midiFileMap.get(sKey);
                    if (f.isFile() == true) {
                        if (Utility.checkExtensions(sKey, exMUSIC) == true) {
                            if (midiFileMap.containsKey(sKey) == true) {
                                list.add(f);
                            }
                        }
                    }
                }
            }
            // Other
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (list.contains(f) == false) {
                    list.add(f);
                }
            }

            // 除外ケース
            for (int i = list.size() - 1; i >= 0; i--) {
                File f = list.get(i);
                if (f.exists() == false || f.canRead() == false || f.isHidden() == true) {
                    list.remove(i);
                }
            }

            ImageIcon folderIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileFolderIcon());
            ImageIcon midiIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileMidiIcon());
            ImageIcon wavIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileWavIcon());
            ImageIcon xmlIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileXmlIcon());
            ImageIcon musicIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileMusicIcon());
            ImageIcon otherIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileOtherIcon());

            for (File f : list) {
                String name = "";
                for (String key : midiFileMap.keySet()) {
                    if (f == midiFileMap.get(key)) {
                        /* 合致するキーを表示する */
                        name = key;
                        break;
                    }
                }

                if (f.isDirectory() == true) {
                    Object[] row = createFileListRows(folderIcon, "DI", name);
                    model.addRow(row);
                }
                else {
                    if (Utility.checkExtensions(name, exMIDI) == true) {
                        Object[] row = createFileListRows(midiIcon, "MI", name);
                        model.addRow(row);
                    }
                    else if (Utility.checkExtensions(name, exWAV) == true) {
                        Object[] row = createFileListRows(wavIcon, "WA", name);
                        model.addRow(row);
                    }
                    else if (Utility.checkExtensions(name, exMUSICXML) == true) {
                        Object[] row = createFileListRows(xmlIcon, "XM", name);
                        model.addRow(row);
                    }
                    else if (Utility.checkExtensions(name, exMML) == true) {
                        Object[] row = createFileListRows(xmlIcon, "MM", name);
                        model.addRow(row);
                    }
                    else if ((Utility.checkExtensions(name, exMUSIC) == true) && (validFFmpegPlayer == true)) {
                        Object[] row = createFileListRows(musicIcon, "MU", name);
                        model.addRow(row);
                    }
                    else {
                        Object[] row = createFileListRows(otherIcon, "#", name);
                        model.addRow(row);
                    }
                }
            }

            midiFileList.setRowHeight(ROW_SIZE);
        }
    }

    private Object[] createFileListRows(ImageIcon icon, String ex, String fileName) {
        if (icon != null) {
            Object[] row = { icon, fileName };
            return row;
        }
        else {
            Object[] row = { ex, fileName };
            return row;
        }
    }

    public void loadPlayList(String path) {
        if (path.endsWith(".jmpl") == true) {
            BufferedReader reader;
            String line = "";
            String result = "";

            File file = new File(path);
            if (file.isFile() == false) {
                return;
            }
            if (file.exists() == false) {
                return;
            }

            DefaultListModel<String> m = (DefaultListModel<String>) playList.getModel();
            m.removeAllElements();

            try {
                FileInputStream fs = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
                reader = new BufferedReader(isr);

                // ファイルを読み込む
                while ((line = reader.readLine()) != null) {
                    result = line.trim();
                    m.addElement(result);
                }

                reader.close();
            }
            catch (Exception e) {
            }
            finally {
                reader = null;
            }
        }
    }

    private static final String NODE_COLOR = "yellow";
    private static final String SEP_COLOR = "gray";
    private JLabel labelContinuePlayback;
    private JButton addButton;
    private JButton btnExproler;
    private JButton btnDirectLoad;
    private JButton buttonClear;
    private JButton buttonDelete;
    private JButton btnLoad;
    private JScrollPane scrollPane_1;
    private JScrollPane scrollPane;

    protected void setCurrentPathText(String path) {
        File file = new File(path);

        final String htmlFormat = "<html>%s</html>";
        final String fontFormat = "<font color=%s> %s </font>";

        String text = "";
        String[] nodes = Utility.getFileNodeNames(file);
        int start = nodes.length - 2;
        if (start < 0) {
            start = 0;
        }
        for (int i = start; i < nodes.length; i++) {
            if (i > 0) {
                text += String.format(fontFormat, SEP_COLOR, ">");
            }
            String node = nodes[i];
            if (node.equals("") == true) {
                node = String.format(fontFormat, NODE_COLOR, "~");
            }
            text += String.format(fontFormat, NODE_COLOR, node);
        }
        lblPath.setText(String.format(htmlFormat, text));
    }

    @Override
    public void updateLanguage() {
        LanguageManager lm = JMPCore.getLanguageManager();

        super.updateLanguage();
        setTitle(lm.getLanguageStr(LangID.Playlist));
        labelContinuePlayback.setText(lm.getLanguageStr(LangID.Continuous_playback));
        btnDirectLoad.setText(lm.getLanguageStr(LangID.Playback));
        btnExproler.setText(lm.getLanguageStr(LangID.Open_with_Explorer));
        addButton.setText(lm.getLanguageStr(LangID.Add));

        buttonClear.setText(lm.getLanguageStr(LangID.Clear));
        buttonDelete.setText(lm.getLanguageStr(LangID.Remove));
        btnLoad.setText(lm.getLanguageStr(LangID.Continuous_playback));
    }

    @Override
    public void updateConfig(String key) {
        if (key.equalsIgnoreCase(DataManager.CFG_KEY_AUTOPLAY) == true) {
            updateGUI();
        }
    }
}
