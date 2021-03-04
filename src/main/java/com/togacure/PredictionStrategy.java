package com.togacure;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public interface PredictionStrategy {

    Block getInitialBlock();

    Block getNextBlock(Block block);

    boolean isEnough(Block block);

    interface BlockRecognizer {

        void recognize(Block block);

    }

    interface RectangleDetector {

        Block takeRectangle(Block block);

    }

}
