package friendless.games.filler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by john on 4/09/15.
 */
public class PopupCalculationsBoard extends FillerBoard {
    private int[] data;
    private static int imageFrame = 0;
    protected JFrame frame;

    public PopupCalculationsBoard(FillerPlayerSpace space) {
        this.data = space.counted;
    }

    @Override
    public BufferedImage resetOffscreenImage() {
        Rectangle b = getBounds();
        BufferedImage img = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_ARGB);
        Graphics goff = img.getGraphics();
        goff.setColor(getBackground());
        goff.fillRect(0,0,b.width,b.height);
        for (int i=0; i<data.length; i++) {
            if (!FillerModel.valid(i)) continue;
            int ci = data[i];
            Color c;
            if (ci == FillerModel.HIS) {
                c = Color.RED;
            } else if (ci == FillerModel.SHARED_BORDER) {
                c = Color.MAGENTA;
            } else if (ci == FillerModel.HIS_BORDER) {
                c = Color.PINK;
            } else if (ci == FillerModel.HIS_INTERNAL_BORDER) {
                c = Color.PINK.darker();
            } else if (ci == FillerModel.MINE) {
                c = Color.BLUE;
            } else if (ci == FillerModel.BORDER) {
                c = Color.CYAN;
            } else if (ci == FillerModel.INTERNAL_BORDER) {
                c = Color.CYAN.darker();
            } else if (ci == FillerModel.HIS_REACHABLE) {
                c = Color.PINK.brighter();
            } else if (ci == FillerModel.REACHABLE) {
                c = Color.CYAN.brighter();
            } else {
                c = Color.BLACK;
            }
            drawHex(goff, c, i);
        }
        goff.dispose();
        String filename = MessageFormat.format("frames/calculations_{0,number,00}.png", imageFrame++);
        try {
            File f = new File(filename);
            ImageIO.write(img, "png", f);
        } catch (IOException ex) {
        }
        off = img;
        return img;
    }

    public void popdown() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    public void popup(String name) {
        frame = new JFrame(name);
        frame.getContentPane().add("Center", this);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { popdown(); }
        });
        frame.setVisible(true);
    }

    public static void popup(FillerPlayerSpace space, String name) {
        (new PopupCalculationsBoard(space)).popup(name);
    }
}

