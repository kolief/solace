package net.solace.api.movement.pathfinder.model.poh;

public enum JewelryBox {
    NONE(-1),
    BASIC(29154),
    FANCY(29155),
    ORNATE(29156);

    private final int objectId;

    private JewelryBox(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return this.objectId;
    }
}

