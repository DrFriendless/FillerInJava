//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Library General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package friendless.awt;

import java.awt.*;
import java.util.*;

/**
 * A layout which allows components to be horizontally arrayed and take up
 * all available space in a sensible fashion.
 * Like Boxlayout only better and written earlier.
 * See VCodeLayout for documentation of codes.
 *
 * @see VCodeLayout
 * @author John Farrell
 */
public final class HCodeLayout implements LayoutManager {
    private int hgap;
    private String usual;
    private Hashtable codes;
    private boolean debug;

    /** By default, components are centred vertically within the row. */
    public HCodeLayout() { this("",4,false); }

    /** @param hgap specifies the vertical distance between components.
     * Horizontal distance is always decided by the layout.
     */
    public HCodeLayout(String usual, int hgap) {
        this(usual,hgap,false);
    }

    public HCodeLayout(String usual, int hgap, boolean debug) {
        if (usual == null) usual = "";
        this.usual = usual;
        this.hgap = hgap;
        this.codes = new Hashtable();
        this.debug = debug;
    }

    public void addLayoutComponent(String code, Component comp) {
        codes.put(comp,code);
    }

    private boolean hasCode(StringBuffer code, char c) {
        for (int i=0; i<code.length(); i++) {
            if (code.charAt(i) == c) return true;
        }
        return false;
    }

    /** Get the number of 'x' codes in this StringBuffer. */
    private int getExpansion(StringBuffer code) {
        int count = 0;
        for (int i=0; i<code.length(); i++) {
            if (code.charAt(i) == 'x') count++;
        }
        return count;
    }

    /**
     * Do all appropriate additions, subtractions and defaults to find the
     * correct code for this component.
     */
    private StringBuffer getCode(Component c) {
        String code = (String)codes.get(c);
        if (code == null) code = "";
        StringBuffer fullCode = new StringBuffer(usual + code);
        Vector soFar = new Vector();
        boolean negative = false;
        for (int i=0; i<fullCode.length(); i++) {
            char ch = fullCode.charAt(i);
            if (ch == '-') {
                negative = true;
            } else if (ch == '+') {
                negative = false;
            } else {
                Character cc = new Character(ch);
                if (negative) {
                    soFar.removeElement(cc);
                } else {
                    soFar.addElement(cc);
                }
            }
        }
        StringBuffer buf = new StringBuffer(soFar.size());
        for (int i=0; i<soFar.size(); i++) {
            Character ch = (Character)soFar.elementAt(i);
            buf.append(ch.charValue());
        }
        return buf;
    }

    /** Relocates the components of parent according to this layout. **/
    public void layoutContainer(Container parent) {
        boolean min;
        int height, width, exCount, expansion, h, v;
        Rectangle b = parent.getBounds();
        Insets insets = parent.getInsets();
        if (insets == null) insets = new Insets(0,0,0,0);
        if (debug) {
            System.out.println("HCodeLayout: Bounds: " + b);
            System.out.println("HCodeLayout: Insets: " + insets);
        }
        int actualHeight = b.height - insets.top - insets.bottom;
        int actualWidth = b.width - insets.left - insets.right;
        width = 0;
        exCount = 0;
        Component[] cs = parent.getComponents();
        // calculate preferred sizes
        width = 0;
        for (int i=0; i<cs.length; i++) {
            Dimension d = cs[i].getPreferredSize();
            StringBuffer code = getCode(cs[i]);
            int ex = getExpansion(code);
            if (ex > 0) {
                exCount += ex;
            } else {
                width += d.width;
            }
            width += hgap;
        }
        width -= hgap;
        // calculate size to expand zero height components to
        if (width >= actualWidth) {
            // preferred size is too big, so try minimum size
            min = true;
            width = 0;
            for (int i=0; i<cs.length; i++) {
                Dimension d = cs[i].getMinimumSize();
                StringBuffer code = getCode(cs[i]);
                int ex = getExpansion(code);
                if (ex > 0) {
                    exCount += ex;
                } else {
                    width += d.width;
                }
                width += hgap;
            }
            width -= hgap;
            expansion = 0;
            if ((exCount > 0) && (width < actualWidth)) {
                expansion = (actualWidth - width) / exCount;
            }
        } else if (exCount == 0) {
            // no expandable components
            min = false;
            expansion = 0;
        } else {
            min = false;
            expansion = (actualWidth-width)/exCount;
        }
        // layout
        width = insets.left;
        for (int i=0; i<cs.length; i++) {
            Dimension d = null;
            h = 0;
            // figure out vertical size
            if (min) {
                d = cs[i].getMinimumSize();
            } else {
                d = cs[i].getPreferredSize();
            }
            StringBuffer code = getCode(cs[i]);
            int ex = getExpansion(code);
            if (ex > 0) {
                h = expansion * ex;
                v = 0;
            } else {
                h = d.width;
                v = d.height;
            }
            // figure out vertical placement
            int top = insets.top;
            if (hasCode(code,'f')) {
                v = actualHeight;
            } else if ((v == 0) || (v > actualHeight)) {
                v = actualHeight;
            }
            if (hasCode(code,'b')) {
                top = actualHeight + insets.top - v;
            } else if (hasCode(code,'t')) {
                top = insets.top;
            } else {
                top = insets.top + (actualHeight - v)/2;
            }
            // place it
            cs[i].setBounds(width,top,h,v);
            width += h;
            width += hgap;
        }
    }

    /** Returns the minimum size on which this component may be drawn. **/
    public Dimension minimumLayoutSize(Container parent) {
        int height = 0, width = 0;
        Component[] cs = parent.getComponents();
        for (int i=0; i<cs.length; i++) {
            Dimension d = cs[i].getMinimumSize();
            width += d.width;
            width += hgap;
            if (d.height > height) {
                height = d.height;
                if (debug) System.out.println("HCodeLayout: calc min: " + cs[i]);
            }
        }
        Dimension sz = new Dimension(width - hgap,height);
        Insets insets = parent.getInsets();
        if (insets == null) insets = new Insets(0,0,0,0);
        sz.width += insets.left + insets.right;
        sz.height += insets.top + insets.bottom;
        if (debug) {
            System.out.println("HCodeLayout: Minimum = " + sz);
        }
        return sz;
    }

    /**
     * Returns the preferred size that this container would be if all
     * components told the truth about their size. Canvases are notorious
     * liars.
     */
    public Dimension preferredLayoutSize(Container parent) {
        int height = 0, width = 0;
        Component[] cs = parent.getComponents();
        for (int i=0; i<cs.length; i++) {
            Dimension d = cs[i].getPreferredSize();
            width += d.width;
            width += hgap;
            if (d.height > height) {
                height = d.height;
                if (debug) System.out.println("HCodeLayout: calc pref: " + cs[i]);
            }
        }
        Dimension sz = new Dimension(width - hgap,height);
        Insets insets = parent.getInsets();
        if (insets == null) insets = new Insets(0,0,0,0);
        sz.width += insets.left + insets.right;
        sz.height += insets.top + insets.bottom;
        return sz;
    }

    /** This method is empty. **/
    public void removeLayoutComponent(Component comp) { }

    /** Returns a summary of this object as a string. **/
    public String toString() { return getClass().getName(); }
}
