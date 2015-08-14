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

import java.io.*;
import java.util.*;

/**
 * Rating information for a set of players.
 * This is a singleton.
 *
 * @see PlayerRating
 * @author John Farrell
 */
public class PlayerRatings implements Serializable {
    /**
     * Filename of the file we store the ratings in.
     * This file contains a Java-serialised instance of this class.
     */
    public static final String RATINGS_FILENAME = "ratings.ser";
    static final long serialVersionUID = -5410208778282312709L;
    /** The singleton instance. */
    static PlayerRatings theInstance;

    /** A list of PlayerRating objects. */
    List ratings;

    public PlayerRatings() {
        ratings = new ArrayList();
    }

    /** Get the rating for a particular player. If they don't have one, return null. */
    static PlayerRating search(PlayerWrapper pw) {
        String name = pw.getFullName();
        for (int i=0; i<theInstance.ratings.size(); i++) {
            PlayerRating pr = (PlayerRating) theInstance.ratings.get(i);
            if (pr.name.equals(name)) return pr;
        }
        return null;
    }

    /** Get the rating for a particular player. If they don't have one, make up a new one. */
    static PlayerRating includePlayer(PlayerWrapper pw) {
        PlayerRating pr = search(pw);
        if (pr == null) {
            pr = new PlayerRating(pw.getFullName());
            theInstance.ratings.add(pr);
        }
        return pr;
    }

    /** Get the numeric ratings for a pair of players. */
    public static int[] getRatings(PlayerWrapper pw1, PlayerWrapper pw2) {
        PlayerRating pr1 = includePlayer(pw1);
        PlayerRating pr2 = includePlayer(pw2);
        int[] rs = new int[] { pr1.rating, pr2.rating };
        return rs;
    }

    /** Get a string representing the head to head rating of a pair of players. */
    public static String getHeadToHead(PlayerWrapper[] pws) {
        PlayerRating pr = includePlayer(pws[0]);
        // ensure it exists
        PlayerRating pr2 = includePlayer(pws[1]);
        return pr.getHeadToHead(pws[0].getName(), pws[1]);
    }

    /**
     *
     */
    public static void setRatings(PlayerWrapper[] fps, int[] ratings, int winner) {
        PlayerRating[] prs = new PlayerRating[] { search(fps[0]), search(fps[1]) };
        int loser = 1 - winner;
        // update game count and head to head
        prs[winner].result(fps[loser], true);
        prs[loser].result(fps[winner], false);
        // adjust ratings
        int delta = EloRating.adjust(ratings, winner);
        // System.out.println(fps[winner].getName() + " defeated " + fps[loser].getName() +
        // " and won " + delta + " points");
        for (int i=0; i<2; i++) prs[i].rating = ratings[i];
        int wRating = prs[winner].rating;
        int lRating = prs[loser].rating;
        int wgames = prs[winner].games;
        int lgames = prs[loser].games;
        // adjust wildly during provisional period
        if ((wgames < EloRating.PROVISIONAL) && (wRating < lRating)) {
            prs[winner].rating = (wgames * wRating + (EloRating.PROVISIONAL - wgames) * lRating) /
                EloRating.PROVISIONAL;
        }
        if ((lgames < EloRating.PROVISIONAL) && (lRating > wRating)) {
            prs[loser].rating = (lgames * lRating + (EloRating.PROVISIONAL - lgames) * wRating) /
                EloRating.PROVISIONAL;
        }
    }

    public void printRatings() {
        for (int i=0; i<ratings.size(); i++) System.out.println(ratings.get(i).toString());
    }

    public static void retrieve() {
        FileInputStream fis = null;
        try {
            File f = new File(System.getProperty("user.dir") + File.separator + RATINGS_FILENAME);
            //System.out.println("Reading ratings from " + f.getCanonicalPath());
            fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            theInstance = (PlayerRatings) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception ex) {
            System.out.println("In PlayerRatings.retrieve");
            ex.printStackTrace();
            theInstance = new PlayerRatings();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    public static void save() {
        try {
            File f = new File(System.getProperty("user.dir") + File.separator + RATINGS_FILENAME);
            //System.out.println("Saving ratings to " + f.getCanonicalPath());
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(theInstance);
            oos.close();
            fos.close();
        } catch (Exception ex) {
            System.out.println("In PlayerRatings.save");
            System.out.println(System.getProperty("user.dir"));
            ex.printStackTrace();
        }
    }
}


