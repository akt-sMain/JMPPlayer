package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jlib.gui.IJmpMainWindow;
import jmp.ConfigDatabase;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.gui.ui.FileListTableModel;
import jmp.gui.ui.JMPFrame;
import jmp.lang.DefineLanguage.LangID;
import jmp.player.PlayerAccessor;

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

        this.setTransferHandler(new DropFileHandler());

        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_FILE_LIST, this);

        model = new FileListTableModel(columnNames, 0);
        midiFileList = new JTable(model);
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

        setBounds(100, 100, 870, 510);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBackground(getJmpBackColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(5, 39, 391, 390);
            contentPanel.add(scrollPane);
            {
                scrollPane.setViewportView(midiFileList);
            }
        }

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(478, 39, 364, 388);
        contentPanel.add(scrollPane);

        playList = JMPCore.getSoundManager().getPlayList();
        playList.setForeground(Color.WHITE);
        playList.setBackground(Color.BLACK);
        scrollPane.setViewportView(playList);
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
        lblPath.setFont(new Font("MS UI Gothic", Font.BOLD, 14));
        lblPath.setBounds(5, 8, 391, 21);
        lblPath.setOpaque(true);
        LineBorder border = new LineBorder(Color.LIGHT_GRAY, 1, false);
        lblPath.setBorder(border);
        contentPanel.add(lblPath);

        JButton btnBack = new JButton("<");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String curPath = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDILIST);
                File cur = new File(curPath);

                cur = cur.getParentFile();
                if (cur != null) {
                    updateList(cur);
                }
            }
        });
        btnBack.setBounds(398, 8, 39, 21);
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
                String path = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDILIST);
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

        String listPath = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDILIST);
        if ((listPath.isEmpty() == true) || (Utility.isExsistFile(listPath) == false)) {
            updateList(Platform.getCurrentPath());
        }

        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            btnExproler.setVisible(false);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            String path = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDILIST);
            if (path != null && path.isEmpty() == false) {
                updateList(path);
            }
        }
        super.setVisible(b);
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
                    IJmpMainWindow mainWindow = JMPCore.getSystemManager().getMainWindow();

                    File midiFile = midiFileMap.get(name);
                    if (midiFile.isDirectory() == true) {
                        if (midiFile.exists() == true) {
                            updateList(midiFile);
                        }
                    }
                    else {
                        PlayerAccessor accessor = PlayerAccessor.getInstance();
                        if (accessor.isSupportedExtension(Utility.getExtension(midiFile))) {
                            if (accessor.getCurrent().isRunnable() == true) {
                                accessor.getCurrent().stop();
                            }
                        }

                        // 自動再生フラグ
                        JMPFlags.LoadToPlayFlag = true;
                        mainWindow.loadFile(midiFile);
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
        JFileChooser filechooser = new JFileChooser();
        // filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        String path = JMPCore.getDataManager().getConfigParam(ConfigDatabase.CFG_KEY_MIDILIST);
        if (path != null && path.isEmpty() == false) {
            filechooser.setCurrentDirectory(new File(path));
        }

        int selected = filechooser.showSaveDialog(getParent());
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
        if (file.isDirectory() == false) {
            file = file.getParentFile();
        }

        setCurrentPathText(file.getPath());
        JMPCore.getDataManager().setConfigParam(ConfigDatabase.CFG_KEY_MIDILIST, file.getPath());

        midiFileMap = JMPCore.getSoundManager().getMidiFileList(file);
        removeAllRows();

        if (midiFileMap != null) {
            // ファイル名ソート
            Object[] keys = midiFileMap.keySet().toArray();
            Arrays.sort(keys);

            List<String> list = new LinkedList<String>();
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isDirectory() == true) {
                    if (midiFileMap.containsKey(sKey) == true) {
                        list.add(sKey);
                    }
                }
            }
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, DataManager.ExtentionForMIDI) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(sKey);
                        }
                    }
                }
            }
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, DataManager.ExtentionForWAV) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(sKey);
                        }
                    }
                }
            }
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, DataManager.ExtentionForMusicXML) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(sKey);
                        }
                    }
                }
            }
            for (Object key : keys) {
                String sKey = key.toString();
                if (list.contains(sKey) == false) {
                    list.add(sKey);
                }
            }

            ImageIcon folderIcon = convertImageIcon(JMPCore.getResourceManager().getFileFolderIcon());
            ImageIcon midiIcon = convertImageIcon(JMPCore.getResourceManager().getFileMidiIcon());
            ImageIcon wavIcon = convertImageIcon(JMPCore.getResourceManager().getFileWavIcon());
            ImageIcon xmlIcon = convertImageIcon(JMPCore.getResourceManager().getFileXmlIcon());
            ImageIcon otherIcon = convertImageIcon(JMPCore.getResourceManager().getFileOtherIcon());

            for (String name : list) {
                File f = midiFileMap.get(name);
                if (f.isDirectory() == true) {
                    Object[] row = { folderIcon, name };
                    model.addRow(row);
                }
                else {
                    if (Utility.checkExtensions(name, DataManager.ExtentionForMIDI) == true) {
                        Object[] row = { midiIcon, name };
                        model.addRow(row);
                    }
                    else if (Utility.checkExtensions(name, DataManager.ExtentionForWAV) == true) {
                        Object[] row = { wavIcon, name };
                        model.addRow(row);
                    }
                    else if (Utility.checkExtensions(name, DataManager.ExtentionForMusicXML) == true) {
                        Object[] row = { xmlIcon, name };
                        model.addRow(row);
                    }
                    else {
                        Object[] row = { otherIcon, name };
                        model.addRow(row);
                    }
                }
            }

            midiFileList.setRowHeight(ROW_SIZE);
        }
    }

    private void catchItem(Object obj) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) obj;

        // 一番先頭のファイルを取得
        if (files != null) {
            if (files.size() > 0) {
                File file = files.get(0);
                updateList(file);
            }
        }
    }

    private ImageIcon convertImageIcon(Image img) {
        return img == null ? null : new ImageIcon(img);
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

    protected void setCurrentPathText(String path) {
        File file = new File(path);

        final String htmlFormat = "<html>%s</html>";
        final String fontFormat = "<font color=%s> %s </font>";

        String text = "";
        String[] nodes = Utility.getFileNodeNames(file);
        for (int i = 0; i < nodes.length; i++) {
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

    /**
     *
     * ドラッグ＆ドロップハンドラー
     *
     */
    public class DropFileHandler extends TransferHandler {
        /**
         * ドロップされたものを受け取るか判断 (アイテムのときだけ受け取る)
         */
        @Override
        public boolean canImport(TransferSupport support) {
            if (support.isDrop() == false) {
                // ドロップ操作でない場合は受け取らない
                return false;
            }

            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
                // ファイルでない場合は受け取らない
                return false;
            }

            return true;
        }

        /**
         * ドロップされたアイテムを受け取る
         */
        @Override
        public boolean importData(TransferSupport support) {
            // ドロップアイテム受理の確認
            if (canImport(support) == false) {
                return false;
            }

            // ドロップ処理
            Transferable t = support.getTransferable();
            try {
                // ドロップアイテム取得
                catchItem(t.getTransferData(DataFlavor.javaFileListFlavor));
                return true;
            }
            catch (Exception e) {
                /* 受け取らない */
            }
            return false;
        }
    }
}
