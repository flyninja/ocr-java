package com.togacure;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public class CardPredictor {

    private final PredictionStrategy strategy;

    private final PredictionStrategy.BlockRecognizer recognizer;

    private final List<PredictionStrategy.Block> blocks = new LinkedList<>();

    public CardPredictor(final PredictionStrategy strategy, final PredictionStrategy.BlockRecognizer recognizer) {
        this.strategy = strategy;
        this.recognizer = recognizer;
    }

    public void predict() {
        PredictionStrategy.Block block = strategy.getInitialBlock();
        do {
            recognizer.recognize(block);
            System.out.format("predict: current block: %s\n", block);
            if (strategy.isEnough(block)) {
                blocks.add(block);
            }
        } while ((block = strategy.getNextBlock(block)) != null);
    }


    public List<PredictionStrategy.Block> getBlocks() {
        return blocks;
    }


}
