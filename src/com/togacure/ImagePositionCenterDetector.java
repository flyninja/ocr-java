package com.togacure;

import java.awt.image.BufferedImage;

import static com.togacure.PlayingCardImageSettings.TRACE;

/**
 * @author Vitaly Alekseev
 * @since 02.03.2021
 */
public class ImagePositionCenterDetector implements PredictionStrategy.RectangleDetector {

    private final BufferedImage image;

    private final int horizontalGap;

    private final int verticalGap;

    private final int imageWidth;

    private final int imageHeight;

    public ImagePositionCenterDetector(final BufferedImage image, final int horizontalGap, final int verticalGap, int imageWidth, int imageHeight) {
        this.image = image;
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public Block takeRectangle(final Block block) {
        int y1 = 0, y2 = 0, x1 = 0, x2 = 0;
        for (int yi = verticalGap; yi < block.getHeight() - verticalGap; yi++) {
            for (int xi = horizontalGap; xi < block.getWidth() - horizontalGap; xi++) {
                final int rgb = image.getRGB(block.getX() + xi, block.getY() + yi);
                if (rgb != block.getBackground()) {
                    x1 = x1 == 0 ? xi : Math.min(x1, xi);
                    x2 = x2 == 0 ? xi : Math.max(x2, xi);
                    y1 = y1 == 0 ? yi : Math.min(y1, yi);
                    y2 = y2 == 0 ? yi : Math.max(y2, yi);
                }
            }
        }
        final int width = ((x2 - x1) >> 1) | ((x2 - x1) & 1);
        final int height = ((y2 - y1) >> 1) | ((y2 - y1) & 1);
        final Block result = new Block(block.getX() + x1 - ((imageWidth >> 1) - width),
                block.getY() + y1 - ((imageHeight >> 1) - height),
                imageWidth,
                imageHeight);
        result.setBackground(block.getBackground());
        result.setM(block.getM());
        if (TRACE) {
            System.out.format("Centered area: %s\n", result);
        }
        return result;
    }

}
