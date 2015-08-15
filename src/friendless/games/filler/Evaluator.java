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

package friendless.games.filler;

/**
 * An interface to be implemented by something which can evaluate the
 * goodness of a particular board position.
 *
 * @author John Farrell
 */
public interface Evaluator {
    /**
     * @param model the board position to be evaluated
     * @param counted the analysis of the board position telling who owns
     * what.
     */
    int eval(FillerModel model, int[] counted);
}
