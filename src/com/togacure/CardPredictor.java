package com.togacure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.togacure.PlayingCardImageSettings.TRACE;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public class CardPredictor {

    private final PredictionStrategy strategy;

    private final PredictionStrategy.BlockRecognizer recognizer;

    private final List<Block> blocks = new LinkedList<>();

    public CardPredictor(final PredictionStrategy strategy, final PredictionStrategy.BlockRecognizer recognizer) {
        this.strategy = strategy;
        this.recognizer = recognizer;
    }

    public void predict() {
        Block block = strategy.getInitialBlock();
        do {
            recognizer.recognize(block);
            if (TRACE) {
                System.out.format("predict: current block: %s\n", block);
            }
            if (strategy.isEnough(block)) {
                blocks.add(block);
            }
        } while ((block = strategy.getNextBlock(block)) != null);
    }


    public List<Block> getBlocks() {
        return new ArrayList<>(blocks);
    }


}
