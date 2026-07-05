package net.solace.api.util;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.concurrent.ThreadLocalRandom;
import net.solace.api.coords.Coordinate;

public class Randomizer {
    public static Coordinate getRandomPointIn(Shape shape) {
        if (shape == null) {
            return null;
        }
        Rectangle rect = shape.getBounds();
        return Randomizer.getRandomPointIn(rect);
    }

    public static Coordinate getRandomPointIn(Rectangle rect) {
        if (rect == null) {
            return null;
        }
        int xDeviation = (int)Math.log(rect.getWidth() * Math.PI);
        int yDeviation = (int)Math.log(rect.getHeight() * Math.PI);
        return Randomizer.getRandomPointIn(rect, xDeviation, yDeviation);
    }

    public static Coordinate getRandomPointIn(Rectangle rect, int xDeviation, int yDeviation) {
        double centerX = rect.getCenterX();
        double centerY = rect.getCenterY();
        double randX = Math.max(Math.min(centerX + (double)xDeviation * ThreadLocalRandom.current().nextGaussian(), rect.getMaxX()), rect.getMinX());
        double randY = Math.max(Math.min(centerY + (double)yDeviation * ThreadLocalRandom.current().nextGaussian(), rect.getMaxY()), rect.getMinY());
        return new Coordinate((int)randX, (int)randY);
    }
}

