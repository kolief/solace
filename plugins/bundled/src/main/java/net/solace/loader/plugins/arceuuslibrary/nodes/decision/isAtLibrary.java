package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.runelite.api.coords.WorldPoint;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.LibraryPosition;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.Players;

public class isAtLibrary extends DecisionNode {
    private static final WorldPoint OUTSIDE_ARBITER = new WorldPoint(1627, 3797, 0);

    public isAtLibrary(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        boolean isInLibraryArea = false;

        //prevent getting stuck in the corner on the outside of the library
        var localOrigin = Players.getLocal().getWorldLocation();
        if (localOrigin.getPlane() == OUTSIDE_ARBITER.getPlane()
            && localOrigin.getX() > OUTSIDE_ARBITER.getX()
            && localOrigin.getY() < OUTSIDE_ARBITER.getY()) {

            return false;
        }

        for (int floor = 0; floor < 3; floor++) {
            var LibraryArea = LibraryPosition.getArea(floor);
            if (LibraryArea != null) {
                isInLibraryArea |= (LibraryArea.contains(localOrigin)) && Players.getLocal().getPlane() == floor;
            }
        }

        return isInLibraryArea;
    }
}