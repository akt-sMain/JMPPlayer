package jmp.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import function.Utility;
import jlib.gui.IJmpWindow;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.WindowManager;
import jmp.gui.ui.IJMPComponentUI;

public class JmpPlayerLaunch extends JPanel implements MouseListener, IJMPComponentUI {

    public static final Color BORDER_COLOR = Color.WHITE;
    public static final Color BACK_COLOR = Color.BLACK;
    public static final Color BACK_COLOR2 = Utility.convertCodeToHtmlColor("#778899");
    public static final int SIZE = 16;
    public static final int MERGIN = 2;
    public static final int TOTAL_WIDTH = ((SIZE + (MERGIN * 2))) * 3;

    /**
     * Create the panel.
     */
    public JmpPlayerLaunch() {
        setLayout(null);

        setBackground(getJmpBackColor());

        addMouseListener(this);
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int x = startX();
        int y = MERGIN;
        boolean checked = false;

        checked = checkLoopMark();
        g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
        g.fillRect(x, y, SIZE, SIZE);
        paintLoopMark(g, x, y, SIZE, SIZE);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        checked = checkAutoMark();
        g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
        g.fillRect(x, y, SIZE, SIZE);
        paintAutoMark(g, x, y, SIZE, SIZE);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        checked = checkPlaylistMark();
        g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
        g.fillRect(x, y, SIZE, SIZE);
        paintPlaylistMark(g, x, y, SIZE, SIZE);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);
    }

    @Override
    public void paintComponents(Graphics g) {
        // super.paintComponents(g);
    }

    public boolean checkAutoMark() {
        return JMPCore.getDataManager().isAutoPlay();
    }
    public void paintAutoMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnAutoIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
        }
    }

    public boolean checkLoopMark() {
        return JMPCore.getDataManager().isLoopPlay();
    }
    public void paintLoopMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnLoopIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
        }
    }

    public boolean checkPlaylistMark() {
        IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FILE_LIST);
        if (win != null) {
            return win.isWindowVisible();
        }
        return false;
    }
    public void paintPlaylistMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnListIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
        }
    }

    private int startX() {
        return MERGIN * 2;
    }

    private int startY() {
        return MERGIN;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        DataManager dm = JMPCore.getDataManager();
        WindowManager wm = JMPCore.getWindowManager();
        int x = startX();
        int y = startY();
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            dm.setLoopPlay(!dm.isLoopPlay());
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            dm.setAutoPlay(!dm.isAutoPlay());
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            IJmpWindow win = wm.getWindow(WindowManager.WINDOW_NAME_FILE_LIST);
            if (win != null) {
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
            return;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
