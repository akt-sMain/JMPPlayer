package jmp.gui.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

public class TextFieldDropFileHandler extends TransferHandler {

    private JTextComponent field = null;

    public TextFieldDropFileHandler(JTextComponent field) {
        super();
        this.field = field;
    }

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
            Object item = t.getTransferData(DataFlavor.javaFileListFlavor);

            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) item;

            // 一番先頭のファイルを取得
            if ((files != null) && (files.size() > 0)) {
                String path = files.get(0).getPath();
                File file = new File(path);
                field.setText(file.getPath());
            }
            return true;
        }
        catch (Exception e) {
            /* 受け取らない */
        }
        return false;
    }
}
