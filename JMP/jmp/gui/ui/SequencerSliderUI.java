package jmp.gui.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

import function.Utility;
import jlib.manager.PlayerAccessor;
import jmplayer.JMPPlayer;

public class SequencerSliderUI extends BasicSliderUI {
    public static final Color BLINK_COLOR_1 = Color.GREEN;
    public static final Color BLINK_COLOR_2 = Utility.convertHighLightColor(BLINK_COLOR_1, 210);

    public SequencerSliderUI() {
        super(null);
    }

    @Override
    public void paintThumb(Graphics g) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            // 色をトグルさせる
            Color color = (JMPPlayer.TimerColorToggleFlag == false) ? BLINK_COLOR_1 : BLINK_COLOR_2;
            if (PlayerAccessor.getInstance().getCurrent().isRunnable() == false) {
                color = BLINK_COLOR_1;
            }

            Rectangle knobBounds = thumbRect;
            int w = knobBounds.width;
            int h = knobBounds.height;
            g.translate(knobBounds.x, knobBounds.y);
            g.setColor(color);
            g.drawLine((w / 2) - 1, 0, (w / 2) - 1, h);
            g.drawLine((w / 2), 0, (w / 2), h);
            g.drawLine((w / 2) + 1, 0, (w / 2) + 1, h);
            g.translate(-knobBounds.x, -knobBounds.y);
        }
        else {
            super.paintThumb(g);
        }
    }

    @Override
    public void paintTrack(Graphics g) {
        // super.paintTrack(g);
        Rectangle trackBounds = trackRect;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int cy = (trackBounds.height / 2) - 2;
            int cw = trackBounds.width;

            final int TRACK_HEIGHT = 3;
            g.translate(trackBounds.x, trackBounds.y + cy);
            g.setColor(Color.WHITE);
            g.drawLine(0, 0, cw, 0);
            g.drawLine(0, TRACK_HEIGHT, cw, TRACK_HEIGHT);
            g.drawLine(cw, 0, cw, TRACK_HEIGHT);
            g.drawLine(0, 0, 0, TRACK_HEIGHT);
            g.translate(-trackBounds.x, -(trackBounds.y + cy));
        }
        else {
            super.paintTrack(g);
        }
    }

}
