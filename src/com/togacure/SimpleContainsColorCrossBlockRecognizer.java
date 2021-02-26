package com.togacure;

import java.awt.image.BufferedImage;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public class SimpleContainsColorCrossBlockRecognizer implements PredictionStrategy.BlockRecognizer {

    private final BufferedImage image;

    private final int shoulderXSize;

    private final int shoulderYSize;

    private final int color;

    public SimpleContainsColorCrossBlockRecognizer(final BufferedImage image, final int shoulderXSize, final int shoulderYSize, final int color) {
        this.image = image;
        this.shoulderXSize = shoulderXSize;
        this.shoulderYSize = shoulderYSize;
        this.color = color;
    }

    @Override
    public void recognize(PredictionStrategy.Block block) {
        final int maxHeight = block.getHeight() - (shoulderYSize / 2);
        for (int i = shoulderYSize / 2; i < maxHeight; i++) {
            int x = block.getX();
            while ((x = searchHorizontal(x, block.getY() + i, block.getWidth() - (shoulderXSize / 2))) >= 0) {
                if (verticalMatch(x + (shoulderXSize / 2), block.getY() + i - (shoulderYSize / 2))) {
                    block.setM(100);
                    return;
                }
                x += 1;
            }
        }
    }

    private int searchHorizontal(final int x, final int y, final int w) {
        for (int i = 0; i < w; i++) {
            if (horizontalMatch(x + i, y)) {
                return x + i;
            }
        }
        return -1;
    }

    private boolean horizontalMatch(final int x, final int y) {
        int i = 0;
        for (; i < shoulderXSize; i++) {
            if (image.getRGB(x + i, y) != color) {
                return false;
            }
        }
        return i == shoulderXSize;
    }

    private boolean verticalMatch(final int x, final int y) {
        int i = 0;
        for (; i < shoulderYSize; i++) {
            if (image.getRGB(x, y + i) != color) {
                return false;
            }
        }
        return i == shoulderYSize;
    }

}
