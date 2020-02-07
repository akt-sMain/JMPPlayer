package jmp.gui.ui;

import javax.swing.table.DefaultTableModel;

public class FileListTableModel extends DefaultTableModel {

    public FileListTableModel(Object[] columnNames, int rowCount) {
	super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
	return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
	if (columnIndex == 0) {
	    return getValueAt(0, columnIndex).getClass();
	}
	return super.getColumnClass(columnIndex);
    }

}
