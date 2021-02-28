package com.togacure;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.togacure.PlayingCardImageSettings.TRACE;

/**
 * @author Vitaly Alekseev
 * @since 28.02.2021
 */
public class CardsDetector {

    private final BufferedImage image;

    private final PredictionStrategy.RectangleDetector detector;

    private final CardPredictor predictor;

    private final List<Block> cards = new LinkedList<>();

    public CardsDetector(final BufferedImage image, final PredictionStrategy.RectangleDetector detector, final CardPredictor predictor) {
        this.image = image;
        this.detector = detector;
        this.predictor = predictor;
    }

    /**
     * !!! thread unsafe !!!
     *
     * @return detected cards blocks
     */
    public List<Block> getCards() {
        if (cards.isEmpty()) {
            predictor.predict();
            if (TRACE) {
                System.out.format("predicted blocks: %s\n", predictor.getBlocks());
            }
            cards.addAll(predictor.getBlocks().stream()
                    .map(detector::takeRectangle)
                    .filter(block -> block.getWidth() != 0 && block.getHeight() != 0)
                    .distinct()
                    .collect(Collectors.toList()));
        }
        return cards;
    }

    public List<Block> getSuits() {
        return getCards().stream().map(CardsDetectorFactory.getDefaultSuitsDetector()::takeRectangle).collect(Collectors.toList());
    }

    public List<Block> getHands() {
        return getCards().stream().map(CardsDetectorFactory.getDefaultHandDetector()::takeRectangle).collect(Collectors.toList());
    }
}
