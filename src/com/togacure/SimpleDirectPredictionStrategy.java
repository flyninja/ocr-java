package com.togacure;

/**
 * !!!Thread unsafe state-full!!!
 *
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public class SimpleDirectPredictionStrategy implements PredictionStrategy {

    private final int imageWidth;

    private final int initialHeightForStart;

    private final int maxHeightForEnd;

    private final int blockWidth;

    private final int blockHeight;

    private final double matchThreshold;

    private int matched;

    public SimpleDirectPredictionStrategy(int imageWidth, int imageHeight, int initialHeightForStart, int maxHeightForEnd, int blockWidth, int blockHeight, double matchThreshold) {
        if (maxHeightForEnd > imageHeight) {
            throw new IllegalArgumentException(String.format("maxHeightForEnd is too large %s", maxHeightForEnd));
        }
        if (blockWidth > imageWidth) {
            throw new IllegalArgumentException(String.format("blockWidth is too large %s", blockWidth));
        }
        if (initialHeightForStart >= maxHeightForEnd) {
            throw new IllegalArgumentException(String.format("invalid initialHeightForStart %s", initialHeightForStart));
        }
        if (maxHeightForEnd - initialHeightForStart < blockHeight) {
            throw new IllegalArgumentException(String.format("invalid blockHeight %s", blockHeight));
        }
        this.imageWidth = imageWidth;
        this.initialHeightForStart = initialHeightForStart;
        this.maxHeightForEnd = maxHeightForEnd;
        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;
        this.matchThreshold = matchThreshold;
    }


    @Override
    public Block getInitialBlock() {
        matched = 0;
        return new Block(0, initialHeightForStart, blockWidth, blockHeight);
    }

    @Override
    public Block getNextBlock(final Block block) {
        if (imageWidth - (block.getX() + block.getWidth()) > blockWidth) {
            if (isEnough(block)) {
                this.matched += 1;
            }
            return new Block(block.getX() + blockWidth, block.getY(), blockWidth, blockHeight);
        } else if ((block.getY() + block.getHeight()) + blockHeight < maxHeightForEnd) {
            if (matched > 0) {
                return null;
            }
            return new Block(0, block.getY() + blockHeight, blockWidth, blockHeight);
        }
        return null;
    }

    @Override
    public boolean isEnough(final Block block) {
        return block.getM() > matchThreshold;
    }

}
