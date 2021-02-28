package com.togacure;

/**
 * @author Vitaly Alekseev
 * @since 27.02.2021
 */
public class FixedAreaDetector implements PredictionStrategy.RectangleDetector {

    private final int xOffset;

    private final int yOffset;

    private final int width;

    private final int height;

    public FixedAreaDetector(int xOffset, int yOffset, int width, int height) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
        this.height = height;
    }

    @Override
    public Block takeRectangle(final Block block) {
        final Block result = new Block(block.getX() + xOffset, block.getY() + yOffset, width, height);
        result.setBackground(block.getBackground());
        return result;
    }

}
