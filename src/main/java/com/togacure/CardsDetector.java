package com.togacure;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
        return new ArrayList<>(cards);
    }

    public List<Block> getSuits() {
        final PredictionStrategy.RectangleDetector imageDetector = CardsDetectorFactory.getSuitsImageDetector(image);
        return getCards().stream().map(CardsDetectorFactory.getInitialSuitsDetector()::takeRectangle).map(imageDetector::takeRectangle).collect(Collectors.toList());
    }

    public List<Block> getHands() {
        final PredictionStrategy.RectangleDetector imageDetector = CardsDetectorFactory.getHandsImageDetector(image);
        return getCards().stream().map(CardsDetectorFactory.getInitialHandsDetector()::takeRectangle).map(imageDetector::takeRectangle).collect(Collectors.toList());
    }

    public List<BufferedImage> getSuitsImages() {
        return getSuits().stream().map(this::blockToImage).collect(Collectors.toList());
    }

    public List<BufferedImage> getHandsImages() {
        return getHands().stream().map(this::blockToImage).collect(Collectors.toList());
    }

    private BufferedImage blockToImage(final Block block) {
        return image.getSubimage(block.getX(), block.getY(), block.getWidth(), block.getHeight());
    }
}
