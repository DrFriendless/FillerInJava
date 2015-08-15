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

import java.text.*;
import java.util.*;

/**
 * A class which knows how to run tournaments.
 * The tournament types are:
 * <dl>
 * <dt>Round Robin
 * <dd>Each players plays every other player.
 * <dt>Knockout
 * <dd>Once a player loses a game, they are eliminated.
 * The last player eliminated wins.
 * <dt>Basho
 * <dd>Each player plays the same number of games.
 * The player with the highest number of wins wins the tournament.
 * <dt>Pepper
 * <dd>One player plays against every other opponent.
 * <dt>Challenge
 * <dd>Players issue challenges to other players.
 * </dl>
 *
 * @author John Farrell
 * @author Lang Sharpe
 */
public class Tournaments {
    private static final String SEP = System.getProperty("line.separator");
    static Random rng = new Random();
    private static boolean cancelled;
    private static ResourceBundle resources;

    static void setResources(ResourceBundle resources) {
        Tournaments.resources = resources;
    }

    static void cancel() {
        cancelled = true;
    }

    static void tournament(TournamentRules rules, FillerPanel panel, PlayerWrappers players) {
        cancelled = false;
        boolean onceOnly = true;
        TournamentResultsPanel.getInstance(resources).newTournament(rules, players);
        while (rules.isContinuous() || onceOnly) {
            onceOnly = false;
            switch (rules.rules) {
                case TournamentRules.ROUND_ROBIN:
                    roundRobin(panel, players);
                    break;
                case TournamentRules.BASHO:
                    basho(rules.bashoRounds, panel, players);
                    break;
                case TournamentRules.KNOCKOUT:
                    knockout(panel, players);
                    break;
                case TournamentRules.CHALLENGE:
                    challenge(panel, players);
                    break;
                default: ;
            }
            if (cancelled) break;
        }
    }

    static void challenge(FillerPanel panel, PlayerWrappers players) {
        for (int i=0; i<players.size(); i++) {
            if (cancelled) break;
            PlayerWrapper p1 = players.get(i);
            // interactive players don't get to challenge
            if (p1.getInstance().requiresButtons()) continue;
            PlayerWrapper p2 = getBestOpponent(p1, players);
            panel.tournamentMatch(new PlayerWrapper[] { p1, p2 });
        }
    }

    static PlayerWrapper getBestOpponent(PlayerWrapper challenger, PlayerWrappers players) {
        int best = Integer.MIN_VALUE;
        int points = -1;
        PlayerWrapper bestOpponent = null;
        for (int i=0; i<players.size(); i++) {
            PlayerWrapper opponent = players.get(i);
            if (opponent == challenger) continue;
            int[] h2h = challenger.getRecordAgainst(opponent);
            int tot = h2h[0] + h2h[1];
            if (tot < 10) {
                h2h[0] += (10 - tot)/2;
                h2h[1] += (10 - tot)/2;
            }
            int heuristicPercentage = 100 * h2h[0] / (h2h[0] + h2h[1]);
            int[] ratings = { challenger.getRating(), opponent.getRating() };
            int[] expectedWinnings = EloRating.expectedWinnings(ratings);
            int value = heuristicPercentage * (expectedWinnings[0] - expectedWinnings[1]);
/*
            System.out.println(challenger.getName() + " expects a " + heuristicPercentage +
            "% chance against " + opponent.getName() + " for which they would get " +
            expectedWinnings[0] + " points");
*/
            if (value > best) {
                best = value;
                bestOpponent = opponent;
                points = expectedWinnings[0];
            } else if (value == best) {
                // usually expect no chance, choose lower ranked opponent
                if (bestOpponent.getRating() > opponent.getRating()) {
                    bestOpponent = opponent;
                    points = expectedWinnings[0];
                }
            }
        }
        String template = resources.getString("filler.string.challenges");
        String mesg = MessageFormat.format(template,
            challenger.getName(), bestOpponent.getName(), points);
        TournamentResultsPanel.getInstance(resources).addText(mesg + SEP);
        return bestOpponent;
    }

    static void knockout(FillerPanel panel, PlayerWrappers players) {
        players.sortByRandom();
        int numRealPlayers = players.size();
        int numPlayers = numRealPlayers + (numRealPlayers % 2);
        int[] pis = new int[numPlayers];
        for (int i=0; i<pis.length; i++) {
            if (i < numRealPlayers) {
                pis[i] = i;
            } else {
                pis[i] = -1;
            }
        }
        while (pis.length > 1) {
            // build matches
            int effectiveLength = pis.length + (pis.length % 2);
            int[][] matches = new int[effectiveLength/2][2];
            for (int j=0; j<effectiveLength; j++) {
                if (j < pis.length) {
                    matches[j/2][j%2] = pis[j];
                } else {
                    matches[j/2][j%2] = -1;
                }
            }
            pis = playTournamentMatches(matches, panel, players, false);
            // If there are an odd number of players left, put the last player
            // into first position, so that a player can't get two byes -- LS
            if ((pis.length % 2) == 1) {
                int last = pis[pis.length-1];
                for (int i=(pis.length-1); i>0; i--) pis[i] = pis[i-1];
                pis[0] = last;
            }
            TournamentResultsPanel.getInstance(resources).addText(resources.getString("filler.string.endofround") + SEP);
        }
        String template = resources.getString("filler.string.knockoutwinner");
        String mesg = MessageFormat.format(template, players.get(pis[0]));
        panel.showMessage(mesg, "");
        TournamentResultsPanel.getInstance(resources).addText(mesg + SEP);
    }

    static PlayerWrappers checkEvenNumberOfPlayers(PlayerWrappers players) {
        if (players.size() % 2 == 1) {
            players.sortByRatings();
            PlayerWrapper eliminated = players.get(players.size() - 1);
            TournamentResultsPanel.getInstance(resources).addText(eliminated + " can not participate in the basho." + SEP);
            for (int i=0; i<players.size(); i++) {
                PlayerWrapper p = players.get(i);
                players.setSelection(players.get(i), p != eliminated);
            }
            return players.getSelected();
        } else {
            return players;
        }
    }

    private static void swap(Object[] objs, int[] vs, int i, int j) {
        Object o = objs[i];
        objs[i] = objs[j];
        objs[j] = o;
        int n = vs[i];
        vs[i] = vs[j];
        vs[j] = n;
    }

    /**
     * Sort by descending order of rating.
     */
    static void sortByRatings(PlayerWrapper[] players, int[] wins) {
        for (int i=0; i<players.length-1; i++) {
            for (int j=i+1; j<players.length; j++) {
                int scorei = players[i].getRating();
                int scorej = players[j].getRating();
                if (scorei < scorej) swap(players, wins, i, j);
            }
        }
    }

    /**
     * Sort by descending order of wins.
     */
    static void sortByWins(PlayerWrapper[] players, int[] wins) {
        for (int i=0; i<players.length-1; i++) {
            for (int j=i+1; j<players.length; j++) {
                int scorei = wins[i];
                int scorej = wins[j];
                if (scorei < scorej) swap(players, wins, i, j);
            }
        }
    }

    private static boolean hasPlayed(PlayerWrapper p1, PlayerWrapper p2,
            boolean[][] played, Map<PlayerWrapper, Integer> index) {
        int i1 = index.get(p1);
        int i2 = index.get(p2);
        return played[i1][i2];
    }

    private static void setPlayed(PlayerWrapper p1, PlayerWrapper p2,
            boolean[][] played, Map<PlayerWrapper, Integer> index, boolean value) {
        int i1 = index.get(p1);
        int i2 = index.get(p2);
        played[i1][i2] = value;
    }

    private static int minIndex(PlayerWrapper[] players, boolean[] allocated) {
        for (int i=0; i<players.length; i++) {
            if (!allocated[i]) return i;
        }
        // should not happen
        return -1;
    }

    /**
     * Allocate players to matches using a backtracking system.
     */
    static List allocateBasho(PlayerWrapper[] players, boolean[][] played, Map<PlayerWrapper, Integer> index,
            List allocatedPairs, boolean[] allocated) {
        if (allocatedPairs.size() * 2 == players.length) {
            return allocatedPairs;
        } else {
            int first = minIndex(players, allocated);
            allocated[first] = true;
            PlayerWrapper p1 = players[first];
            for (int i=first+1; i<players.length; i++) {
                if (allocated[i]) continue;
                PlayerWrapper p2 = players[i];
                if (!hasPlayed(p1, p2, played, index)) {
                    allocated[i] = true;
                    setPlayed(p1, p2, played, index, true);
                    PlayerWrapper[] pair = { p1, p2 };
                    allocatedPairs.add(pair);
                    List pairs = allocateBasho(players, played, index, allocatedPairs, allocated);
                    if (pairs != null) {
                        // success
                        return pairs;
                    } else {
                        // temporary failure
                        allocatedPairs.remove(pair);
                        allocated[i] = false;
                        setPlayed(p1, p2, played, index, false);
                    }
                }
            }
            // no opponent for p1
            allocated[first] = false;
            return null;
        }
    }

    static List allocateBasho(PlayerWrapper[] players, boolean[][] played, Map<PlayerWrapper, Integer> index) {
        List allocatedPairs = new ArrayList();
        boolean[] allocated = new boolean[players.length];
        return allocateBasho(players, played, index, allocatedPairs, allocated);
    }

    /**
     * Play a basho tournament. In a real sumo basho, match allocation is a
     * black art. Every day, matches are allocated so that rikishi fight
     * wrestlers of roughly equivalent skill. Towards the end of the tournament,
     * those rikishi with high numbers of wins are pitted against each other.
     * This means that if a lowly ranked rikishi has a large number of wins,
     * he is usually matched against highly ranked rikishi and hence falls back
     * to his appropriate number of wins. The winner is the rikishi with the
     * highest number of wins. This means that very occasionally there is a
     * somewhat surprise winner, but that surprise winner has no doubt had a
     * number of very good wins, above his expected ability. The same sort of
     * things happens in knockout tournaments as well.
     * <P>
     * We approximate this sort of tournament by, for the first half of the
     * basho, matching players against those of approximately equal skill.
     * In the second half, we allocate the players with the most wins against
     * each other. If, after the number of scheduled rounds, we still have
     * players with equal numbers of wins, they play a series of round robins
     * until there is a clear winner.
     */
    static void basho(int rounds, FillerPanel panel, PlayerWrappers players) {
        players = checkEvenNumberOfPlayers(players);
        if (players.size() == 0) {
            TournamentResultsPanel.getInstance(resources).addText("There are no players to participate in the basho." + SEP);
            return;
        }
        /* The played array records who has played whom. Because we sort the
         * array ps and the array wins, we have to record the indexes of the
         * players in the played array in the index map.
         */
        boolean[][] played = new boolean[players.size()][players.size()];
        Map<PlayerWrapper, Integer> index = new HashMap<>(players.size());
        for (int i=0; i<players.size(); i++) {
            index.put(players.get(i), i);
        }
        int openingRounds = rounds/2;
        int closingRounds = rounds - openingRounds;
        PlayerWrapper[] ps = players.toArray();
        int[] wins = new int[players.size()];
        for (int i=0; i<openingRounds; i++) {
            if (cancelled) break;
            sortByRatings(ps, wins);
            List pairs = allocateBasho(ps, played, index);
            playBashoMatches(panel, pairs, ps, wins);
        }
        for (int i=0; i<closingRounds; i++) {
            if (cancelled) break;
            sortByWins(ps, wins);
            String mesg = resources.getString("filler.string.basholeader");
            mesg = MessageFormat.format(mesg, ps[0].getName(), new Integer(wins[0]));
            TournamentResultsPanel.getInstance(resources).addText(mesg + SEP);
            List pairs = allocateBasho(ps, played, index);
            playBashoMatches(panel, pairs, ps, wins);
        }
        PlayerWrapper winner = null;
        while (true) {
            if (cancelled) break;
            sortByWins(ps, wins);
            if (wins[0] > wins[1]) {
                winner = players.get(0);
                break;
            } else {
                tieBreaker(panel, ps, wins);
            }
        }
        if (winner != null) {
            String mesg = resources.getString("filler.string.bashowinner");
            mesg = MessageFormat.format(mesg, winner.getName());
            panel.showMessage(mesg, "");
            TournamentResultsPanel.getInstance(resources).addText(mesg + SEP);
        }
    }

    /** Play the given list of matches. */
    private static void playBashoMatches(FillerPanel panel, List pairs, PlayerWrapper[] players, int[] wins) {
        for (int i=0; i<pairs.size(); i++) {
            if (cancelled) break;
            PlayerWrapper[] pair = (PlayerWrapper[]) pairs.get(i);
            int[] scores = panel.tournamentMatch(pair);
            int winner = (scores[0] > scores[1]) ? 0 : 1;
            for (int j=0; j<players.length; j++) {
                if (players[j] == pair[winner]) {
                    wins[j]++;
                    break;
                }
            }
        }
    }

    /** Play a tie breaker for a bash tournament. */
    private static void tieBreaker(FillerPanel panel, PlayerWrapper[] players, int[] wins) {
        int numEqual = 0;
        for (int i=0; i<wins.length; i++) {
            if (wins[i] == wins[0]) {
                numEqual++;
            } else {
                break;
            }
        }
        TournamentResultsPanel.getInstance(resources).addText("There are " + numEqual + " players in the tiebreaker." + SEP);
        int[][] schedule = roundRobinMatches(numEqual);
        List pairs = new ArrayList(schedule.length);
        for (int i=0; i<schedule.length; i++) {
            PlayerWrapper[] pair = { players[schedule[i][0]], players[schedule[i][1]] };
            pairs.add(pair);
        }
        playBashoMatches(panel, pairs, players, wins);
    }

    /** Calculate a schedule of matches for a round-robin tournament. */
    static int[][] roundRobinMatches(int n) {
        int len = n * (n-1) / 2;
        int index = 0;
        int realN = n + n % 2;
        int[][] matches = new int[len][2];
        int[][] day1 = new int[realN/2][2];
        for (int i=0; i<realN/2; i++) {
            day1[i][0] = realN - i - 1;
            day1[i][1] = i;
        }
        for (int i=0; i<realN-1; i++) {
            for (int j=0; j<day1.length; j++) {
                matches[index][0] = (day1[j][0] == 0) ? 0 : ((day1[j][0] + i) % (realN-1) + 1);
                matches[index][1] = (day1[j][1] == 0) ? 0 : ((day1[j][1] + i) % (realN-1) + 1);
                // skip byes
                if ((matches[index][0] == n) || (matches[index][1] == n)) continue;
                index++;
            }
        }
        return matches;
    }

    static void roundRobin(FillerPanel panel, PlayerWrappers players) {
        int[][] matches = roundRobinMatches(players.size());
        playTournamentMatches(matches, panel, players, true);
    }

    /**
     * @param recordResult whether to tell the TournamentResultsPanel what the
     * result of the match was.
     */
    static int[] playTournamentMatches(int[][] indexes, FillerPanel panel, PlayerWrappers players, boolean recordResult) {
        int[] winners = new int[indexes.length];
        for (int i=0; i<winners.length; i++) winners[i] = -1;
        for (int i=0; i<indexes.length; i++) {
            int[] opps = indexes[i];
            if (opps[0] < 0) {
                winners[i] = opps[1];
                continue;
            } else if (opps[1] < 0) {
                winners[i] = opps[0];
                continue;
            }
            // randomly swap order
            if (rng.nextBoolean()) {
                int t = opps[0];
                opps[0] = opps[1];
                opps[1] = t;
            }
            PlayerWrapper[] ps = { players.get(opps[0]), players.get(opps[1]) };
            int[] scores = panel.tournamentMatch(ps);
            if (scores[0] > scores[1]) {
                winners[i] = opps[0];
            } else {
                winners[i] = opps[1];
            }
            if (recordResult) TournamentResultsPanel.getInstance(resources).addMatch(ps,scores);
            if (cancelled) break;
        }
        return winners;
    }
}
