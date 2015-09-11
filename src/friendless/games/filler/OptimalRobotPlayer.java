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
 * A robot player which uses distance calculations to find the shortest path to
 * a given free location.
 *
 * @author John Farrell
 */
public abstract class OptimalRobotPlayer extends RobotPlayer {
    /**
     * Calculate types as per RobotPlayer, but then additionally calculate the
     * shortest distance from our territory to all of the free space.
     */
    protected void calculate(FillerModel model) {
        FillerModel.allocateTypes(model, origins, space);
        FillerModel.allocateDistance(model, space);
        //PopupFillerBoard.popup(new FillerModel(space.combineCountedAndDistance()), "combined");
    }

    /**
     * @return the colour which will get us to the goal quickest.
     */
    protected BitSet getBestGoalColours(int goal) {
        int[] distance = space.distance;
        int distanceToGoal = distance[goal];
        if (distanceToGoal <= 0) return new BitSet(FillerSettings.NUM_COLOURS);
        /* We now know the distance to the goal.
         * We have to find a sequence of locations with ever-decreasing distances
         * until we get to locations which are distance 0, i.e. on the border.
         * We then choose any myColour from those distance 0 locations.
         */
        space.resetListed();
        boolean[] listed = space.listed;
        int[] pieces = model.pieces;
        int[] thisDistance = new int[distance.length];
        int thisDistanceIndex = 0;
        int[] lowerDistance = new int[distance.length];
        int lowerDistanceIndex = 0;
        thisDistance[thisDistanceIndex++] = goal;
        // find all the pieces of the same colour joined to the goal
        while (thisDistanceIndex > 0) {
            int p = thisDistance[--thisDistanceIndex];
            if (listed[p]) continue;
            lowerDistance[lowerDistanceIndex++] = p;
            listed[p] = true;
            for (int q : FillerModel.neighbours(p)) {
                if (listed[q]) continue;
                if (pieces[q] == pieces[goal]) {
                    thisDistance[thisDistanceIndex++] = q;
                }
            }
        }
        int[] tempArray = lowerDistance;
        lowerDistance = thisDistance;
        thisDistance = tempArray;
        thisDistanceIndex = lowerDistanceIndex;
        lowerDistanceIndex = 0;
        // now for each distance, find all the pieces in the next distance down
        while (distanceToGoal > 1) {
            while (thisDistanceIndex > 0) {
                int p = thisDistance[--thisDistanceIndex];
                for (int q : FillerModel.neighbours(p)) {
                    if (listed[q]) continue;
                    int distq = distance[q];
                    if (distq >= 0 && distq < distanceToGoal) {
                        lowerDistance[lowerDistanceIndex++] = q;
                        listed[q] = true;
                    }
                }
            }
            // add neighbours of same colour attached to pieces at the lower distance (yuk)
            tempArray = lowerDistance;
            lowerDistance = thisDistance;
            thisDistance = tempArray;
            thisDistanceIndex = lowerDistanceIndex;
            lowerDistanceIndex = 0;
            while (thisDistanceIndex > 0) {
                int p = thisDistance[--thisDistanceIndex];
                lowerDistance[lowerDistanceIndex++] = p;
                for (int q : FillerModel.neighbours(p)) {
                    if (listed[q]) continue;
                    if (pieces[p] == pieces[q]) {
                        thisDistance[thisDistanceIndex++] = q;
                        listed[q] = true;
                    }
                }
            }
            /* We have finished with the thisDistance array, we now move down to
             * the lower distance. To do this we swap the arrays and reset the
             * index on the lowerDistance one.
             */
            tempArray = lowerDistance;
            lowerDistance = thisDistance;
            thisDistance = tempArray;
            thisDistanceIndex = lowerDistanceIndex;
            lowerDistanceIndex = 0;
            distanceToGoal--;
        }
        /* All the locations at distance 0 which will get us to the target are in thisDistance. */
        BitSet colours = new BitSet(FillerSettings.NUM_COLOURS);
        for (int i=0; i<thisDistanceIndex; i++) {
            colours.set(pieces[thisDistance[i]]);
        }
        return colours;
    }

    public String getIcon() { return "armorine.png"; }
}
