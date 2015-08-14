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
 * A specification of how a tournament is to be run.
 *
 * @author John Farrell
 */
class TournamentRules {
    public static final int ROUND_ROBIN = 0;
    public static final int KNOCKOUT = 1;
    public static final int BASHO = 2;
    public static final int CHALLENGE = 3;

    int rules;
    int bashoRounds;
    FillerPlayer pepperTarget;
    /** Whether the tournament is played once, or until cancelled. */
    boolean continuous;

    TournamentRules(int rules) {
        this.rules = rules;
        this.bashoRounds = 4;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public boolean isContinuous() { return continuous; }

    public String toString() { return "" + continuous + " " + rules; }
}
