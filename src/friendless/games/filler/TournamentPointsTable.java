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

import javax.swing.table.*;
import java.util.*;

/**
 * This class represents a points table from a Round Robin Turnament
 * it implements TableModel, so it can be viewed with a jTable
 * Points table can be sorted manually, by calling the sort() method .
 *
 * @author Lang Sharpe
 */
public class TournamentPointsTable extends AbstractTableModel {
    private TournamentPointsTableRow rows[];
    private ResourceBundle resources;

    /** Constructor
     * pre: true; post: Object created, matches in matchesdata added.
     */
    public TournamentPointsTable(ResourceBundle resources, PlayerWrappers wrappers) {
        this.resources = resources;
        PlayerWrapper players[] = wrappers.toArray();
        rows = new TournamentPointsTableRow[players.length];
        for (int j=0; j < players.length; j++) {
            rows[j] = new TournamentPointsTableRow(players[j].getName());
        }
    }

    /** Add a match to the table
    Pre: true; Post: table is updated with new match. */
    public void addMatch(PlayerWrapper[] players, int[] scores) {
        int player0 = indexOf(players[0].getName());
        int player1 = indexOf(players[1].getName());
        if (scores[0] > scores[1]) {
            rows[player0].addWin();
            scores[0] = 689;
            rows[player1].addLoss();
        } else {
            rows[player1].addWin();
            scores[1] = 689;
            rows[player0].addLoss();
        }
        rows[player0].addFor(scores[0]);
        rows[player0].addAgainst(scores[1]);
        rows[player1].addFor(scores[1]);
        rows[player1].addAgainst(scores[0]);
        sort();
        fireTableDataChanged();
    }

    /** Part of AbstractTableModel
     * Pre: true; post: returned no of players in table
     */
    public int getRowCount() {
        return rows.length;
    }

    /** Part of AbstractTableModel
     * Pre: true; post: returned no of columns in table
     */
    public int getColumnCount() {
        return 7;
    }

    /** Part of AbstractTableModel
     * Pre: true; post: returned what should be in the specified cell
     */
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return rows[row].getTeam();
            case 1:
                return new Integer(rows[row].getPlayed());
            case 2:
                return new Integer(rows[row].getWon());
            case 3:
                return new Integer(rows[row].getLost());
            case 4:
                return new Integer(rows[row].getFor());
            case 5:
                return new Integer(rows[row].getAgainst());
            case 6:
                return new Integer(rows[row].getDiff());
            default:
                return "error";
        }
    }

    /** Part of AbstractTableModel
     * Pre: true; post: returned name of column for columnIndex
     */
    public String getColumnName(int columnIndex) {
      switch (columnIndex) {
          case 0:
              return null;
          case 1:
              return resources.getString("filler.label.played");
          case 2:
              return resources.getString("filler.label.won");
          case 3:
              return resources.getString("filler.label.lost");
          case 4:
              return resources.getString("filler.label.for");
          case 5:
              return resources.getString("filler.label.against");
          case 6:
              return "+/-";
          default:
              return "error";
        }
    }

    /** Sorts the table
     * pre: true; post pointstable is sorted by points, then by (F-A) then by wins
     */
    public void sort() {
        Arrays.sort(rows);
        fireTableDataChanged();
    }

    private int indexOf(String teamin) {
        for (int j=0; j < rows.length; j++) {
            if ((rows[j].getTeam()).equals(teamin)) {
               return j;
            }
        }
        System.out.println("TournamentPointsTable.indexOf - Player Not Found");
        return 0;
    }
}