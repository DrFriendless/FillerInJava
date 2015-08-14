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
 * This class summarizes the matches of a single player
 *
 * @author Lang Sharpe
 */
public class TournamentPointsTableRow implements Comparable {
    private String teamname;
    private int won;
    private int lost;
    private int pointsFor;
    private int pointsAgainst;

   /** Constructor.
    * @param team Name of the team.
    */
    public TournamentPointsTableRow(String team) {
        teamname = new String(team);
        // the rest aren't really needed
        won = 0;
        lost = 0;
        pointsFor = 0;
        pointsAgainst = 0;
    }

    /* The next group of methods hopefully speak for themselves.
     * the provide accesors and mutators for the internal private variables
     */

    public void addWin() { won += 1;}
    public void addLoss() { lost += 1;}
    public void addFor(int score) { pointsFor += score;}
    public void addAgainst(int score) { pointsAgainst += score;}

    public String getTeam() { return teamname;}
    public int getPlayed() { return (won + lost);}
    public int getWon() {return won;}
    public int getLost() {return lost;}
    public int getFor() {return pointsFor;}
    public int getAgainst() {return pointsAgainst;}
    public int getDiff() {return (pointsFor - pointsAgainst);}

    /**
     * Compares the object to any other object.
     * From comparable interface.
     * pre: other is a result
     * post: returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
    public int compareTo(Object other) {
        return compareTo((TournamentPointsTableRow) other);
    }

    /**
     * Compares the object to any other object.
     * From comparable interface.
     * pre: other is a result
     * post: returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object
     */
    public int compareTo(TournamentPointsTableRow other) {
        if (this.getWon() != other.getWon()) {
            // wins aren't equal
            return (other.getWon() - this.getWon()); // use wins to separate teams
        } else {
            return (other.getDiff() - this.getDiff()); // else use goal difference to separate teams
        }
    }

    /**
     * Used for debugging only
     * Pre: true; Post: sends object to standard output
     */
    public void display() {
        System.out.println(teamname + "\t" + won + " " + lost
            + " " + pointsFor + " " + pointsAgainst );
    }
}
