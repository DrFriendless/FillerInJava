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
public class PopupDistanceBoard extends FillerBoard {
    private int[] distance;
    private static int imageFrame = 0;
    protected JFrame frame;

    public PopupDistanceBoard(FillerPlayerSpace space) {
        this.distance = space.distance;
    }

    @Override
    public BufferedImage resetOffscreenImage() {
        Rectangle b = getBounds();
        BufferedImage img = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_ARGB);
        Graphics goff = img.getGraphics();
        goff.setColor(getBackground());
        goff.fillRect(0,0,b.width,b.height);
        int maxDistance = 0;
        for (int d : distance) {
            if (d > maxDistance) maxDistance = d;
        }
        Color[] greys = new Color[maxDistance+1];
        for (int i=1; i<greys.length; i++) {
            int j = i * 255 / (maxDistance+1);
            greys[i] = new Color(j,j,j);
        }
        greys[0] = Color.GREEN;
        for (int i=0; i<distance.length; i++) {
            if (!FillerModel.valid(i)) continue;
            int ci = distance[i];
            Color c;
            if (ci == FillerModel.UNREACHABLE_DISTANCE) {
                c = Color.RED;
            } else  {
                c = greys[ci];
            }
            drawHex(goff, c, i);
        }
        goff.dispose();
        String filename = MessageFormat.format("frames/distance_{0,number,00}.png", imageFrame++);
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
        (new PopupDistanceBoard(space)).popup(name);
    }
}
