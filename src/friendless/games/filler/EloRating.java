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

import java.util.*;

/**
 * Miscellaneous support for the ELO Rating system.
 * I have fiddled with the ranking cutoffs because not enough robot players
 * were getting high enough.
 *
 * @author John Farrell
 */
public class EloRating {
    public static String NOVICE, CLASSA, CLASSB, CLASSC, CLASSD, EXPERT, MASTER,
        INTLMASTER, GRANDMASTER, SUPERGRANDMASTER, WORLDCHAMPION;
    public static String[] TITLES;
    public static final int[] RATINGS = { 0, 800, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600 };
    public static final int INITIAL = 1600;
    public static final int PROVISIONAL = 20;

    /** This is the maximum number of points up for grabs in a game. */
    public static int K = 32;
    static ResourceBundle resources;

    static void setResources(ResourceBundle resources) {
        EloRating.resources = resources;
        NOVICE = resources.getString("filler.ranking.NOVICE");
        CLASSA = resources.getString("filler.ranking.CLASSA");
        CLASSB = resources.getString("filler.ranking.CLASSB");
        CLASSC = resources.getString("filler.ranking.CLASSC");
        CLASSD = resources.getString("filler.ranking.CLASSD");
        EXPERT = resources.getString("filler.ranking.EXPERT");
        MASTER = resources.getString("filler.ranking.MASTER");
        INTLMASTER = resources.getString("filler.ranking.INTLMASTER");
        GRANDMASTER = resources.getString("filler.ranking.GRANDMASTER");
        SUPERGRANDMASTER = resources.getString("filler.ranking.SUPERGRANDMASTER");
        WORLDCHAMPION = resources.getString("filler.ranking.WORLDCHAMPION");
        TITLES = new String[] { NOVICE, CLASSA, CLASSB, CLASSC, CLASSD, EXPERT, MASTER,
            INTLMASTER, GRANDMASTER, SUPERGRANDMASTER, WORLDCHAMPION };
    }

    /**
     * P(ratings[1]-ratings[0])
     * Expected chance player[1] will beat player[0]
     */
    public static double expectancy(int[] ratings) {
        int diff = ratings[0] - ratings[1];
        return 1.0 / (1.0 + Math.pow(10.0,diff/400.0));
    }

    /**
     * Expected number of points won by { player[0], player[1] } if they were
     * to win a match between the 2.
     */
    public static int[] expectedWinnings(int[] ratings) {
        double ex = expectancy(ratings);
        int[] result = { (int) (K * ex), (int) (K * (1.0 - ex)) };
        return result;
    }

    /**
     * Adjust a pair of ratings given that the winner was that indicated by
     * <code>winner</code>, which must be 0 or 1.
     * @return the number of points which were won or lost
     */
    public static int adjust(int[] ratings, int winner) {
        double ex = expectancy(ratings);
        int delta = 0;
        if (winner == 0) {
            delta = (int) (K * ex);
            ratings[0] += delta;
            ratings[1] -= delta;
        } else {
            delta = (int) (K * (1.0 - ex));
            ratings[0] -= delta;
            ratings[1] += delta;
        }
        return delta;
    }

    /** @return the title for a particular rating. */
    public static String getLabel(int rating) {
        String label = null;
        for (int i=0; i<RATINGS.length; i++) {
            if (rating > RATINGS[i]) {
                label = TITLES[i];
            } else {
                break;
            }
        }
        return label;
    }
}
