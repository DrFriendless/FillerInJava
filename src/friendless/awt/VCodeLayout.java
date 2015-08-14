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
 * A layout which allows components to be vertically stacked.
 * Each component has constraints which are a cryptic string.
 * "x" means expand the component along the mainm axis.
 * Multiple instances of "x" mean to expand a proportionately larger amount,
 * e.g. a component with "xx" will expand twice as much as one with "x".
 * "+x" means this component's constraints are the layout's usual, with an
 * additional "x".
 * "-x" means this component's constraints are the layout's usual but with
 * one less "x".
 * "+" and "-" can be used for all codes.
 * "f" means fill along the minor axis.
 * "l" ("t") means left (top) justify the component.
 * "r" ("b") mean right (bottom) justify the component.
 *
 * @see HCodeLayout
 * @author John Farrell
 */
public final class VCodeLayout implements LayoutManager {
    private int vgap;
    private String usual;
    private Hashtable codes;
    private boolean debug;

    /** By default, components expand horizontally to fill the entire column. */
    public VCodeLayout() { this("",4,false); }

    /** @param vgap specifies the vertical distance between components. */
    public VCodeLayout(String usual, int vgap) {
        this(usual,vgap,false);
    }

    public VCodeLayout(String usual, int vgap, boolean debug) {
        if (usual == null) usual = "";
        this.usual = usual;
        this.vgap = vgap;
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

    private int getExpansion(StringBuffer code) {
        int count = 0;
        for (int i=0; i<code.length(); i++) {
            if (code.charAt(i) == 'x') count++;
        }
        return count;
    }

    /**
     * Evaluate all the +, -, usual and component settings to find the
     * definitive code for this component.
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
        int height, width, exCount, expansion, h, w;
        Rectangle b = parent.getBounds();
        Insets insets = parent.getInsets();
        if (insets == null) insets = new Insets(0,0,0,0);
        if (debug) {
            System.out.println("VCodeLayout: Bounds: " + b);
            System.out.println("VCodeLayout: Insets: " + insets);
        }
        int actualHeight = b.height - insets.top - insets.bottom;
        int actualWidth = b.width - insets.left - insets.right;
        width = 0;
        exCount = 0;
        Component[] cs = parent.getComponents();
        // calculate preferred sizes
        height = 0;
        for (int i=0; i<cs.length; i++) {
            Dimension d = cs[i].getPreferredSize();
            StringBuffer code = getCode(cs[i]);
            int ex = getExpansion(code);
            if (ex > 0) {
                exCount += ex;
            } else {
                height += d.height;
            }
            height += vgap;
        }
        height -= vgap;
        // calculate size to expand zero height components to
        if (height >= actualHeight) {
            // preferred size is too big, so try minimum size
            min = true;
            height = 0;
            for (int i=0; i<cs.length; i++) {
                Dimension d = cs[i].getMinimumSize();
                StringBuffer code = getCode(cs[i]);
                int ex = getExpansion(code);
                if (ex > 0) {
                    exCount += ex;
                } else {
                    height += d.height;
                }
                height += vgap;
            }
            height -= vgap;
            expansion = 0;
            if ((exCount != 0) && (height < actualHeight)) {
                expansion = (actualHeight - height) / exCount;
            }
        } else if (exCount == 0) {
            // no expandable components
            min = false;
            expansion = 0;
        } else {
            min = false;
            expansion = (actualHeight-height)/exCount;
        }
        // layout
        height = insets.top;
        for (int i=0; i<cs.length; i++) {
            StringBuffer code = getCode(cs[i]);
            Dimension d = null;
            int v = 0;
            // figure out vertical size
            if (min) {
                d = cs[i].getMinimumSize();
            } else {
                d = cs[i].getPreferredSize();
            }
            int ex = getExpansion(code);
            if (ex > 0) {
                h = expansion * ex;
                v = 0;
            } else {
                h = d.height;
                v = d.width;
            }
            // figure out horizontal placement
            int left = 0;
            if (hasCode(code,'f')) {
                v = actualWidth;
            } else if ((v == 0) || (v > actualWidth)) {
                v = actualWidth;
            }
            if (hasCode(code,'l')) {
                left = insets.left;
            } else if (hasCode(code,'r')) {
                left = actualWidth + insets.left - v;
            } else {
                left = insets.left + (actualWidth - v)/2;
            }
            // place it
            cs[i].setBounds(left,height,v,h);
            height += h;
            height += vgap;
        }
    }

    /** Returns the minimum size on which this component may be drawn. **/
    public Dimension minimumLayoutSize(Container parent) {
        int height = 0, width = 0;
        Component[] cs = parent.getComponents();
        for (int i=0; i<cs.length; i++) {
            Dimension d = cs[i].getMinimumSize();
            height += d.height;
            height += vgap;
            if (d.width > width) width = d.width;
        }
        Dimension sz = new Dimension(width,height-vgap);
        Insets insets = parent.getInsets();
        if (insets == null) insets = new Insets(0,0,0,0);
        sz.width += insets.left + insets.right;
        sz.height += insets.top + insets.bottom;
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
            height += d.height;
            height += vgap;
            if (d.width > width) width = d.width;
        }
        Dimension sz = new Dimension(width,height-vgap);
        Insets insets = parent.getInsets();
        if (insets == null) insets = new Insets(0,0,0,0);
        sz.width += insets.left + insets.right;
        sz.height += insets.top + insets.bottom;
        return sz;
    }

    public void removeLayoutComponent(Component comp) { codes.remove(comp); }

    /** Returns a summary of this object as a string. **/
    public String toString() { return getClass().getName(); }
}
