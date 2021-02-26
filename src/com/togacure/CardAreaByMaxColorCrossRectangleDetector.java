package com.togacure;

import java.awt.image.BufferedImage;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public class CardAreaByMaxColorCrossRectangleDetector implements PredictionStrategy.RectangleDetector {

    private final BufferedImage image;

    public CardAreaByMaxColorCrossRectangleDetector(BufferedImage image) {
        this.image = image;
    }

    @Override
    public PredictionStrategy.Block takeRectangle(final PredictionStrategy.Block block) {
        final Coordinate width = findMaxWidth(block.getX(), block.getY(), block.getHeight(), block.getBackground());
        final Coordinate height = findMaxHeight(width.x, width.y, width.size, block.getBackground());
        return new PredictionStrategy.Block(width.x, height.y, width.size, height.size);
    }

    private Coordinate findMaxWidth(final int x, final int y, final int height, final int color) {
        Coordinate max = new Coordinate(x, y, 0);
        for (int i = 0; i < height; i++) {
            if (image.getRGB(x, y + i) != color) {
                continue;
            }
            int below = findBelowX(x, y + i, color);
            int above = findAboveX(x, y + i, color);
            if (max.size < above - below) {
                max = new Coordinate(below, y + i, above - below);
            }
        }
        return max;
    }

    private Coordinate findMaxHeight(final int x, final int y, final int width, final int color) {
        Coordinate max = new Coordinate(x, y, 0);
        for (int i = 0; i < width; i++) {
            int below = findBelowY(x + i, y, color);
            int above = findAboveY(x + i, y, color);
            if (max.size < above - below) {
                max = new Coordinate(x + i, below, above - below);
            }
        }
        return max;
    }

    private int findBelowX(final int x, final int y, final int color) {
        int i = x;
        while (image.getRGB(i, y) == color) {
            i = i - 1;
        }
        return i;
    }

    private int findAboveX(final int x, final int y, final int color) {
        int i = x;
        while (image.getRGB(i, y) == color) {
            i = i + 1;
        }
        return i;
    }

    private int findBelowY(final int x, final int y, final int color) {
        int i = y;
        while (image.getRGB(x, i) == color) {
            i = i - 1;
        }
        return i;
    }

    private int findAboveY(final int x, final int y, final int color) {
        int i = y;
        while (image.getRGB(x, i) == color) {
            i = i + 1;
        }
        return i;
    }

    private static class Coordinate {

        private final int x;

        private final int y;

        private final int size;

        private Coordinate(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
}
