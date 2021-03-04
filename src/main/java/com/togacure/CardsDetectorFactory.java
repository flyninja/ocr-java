package com.togacure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

import static com.togacure.PlayingCardImageSettings.CARD_BACKGROUND_COLORS;
import static com.togacure.PlayingCardImageSettings.DEFAULT_CARD_DETECTOR_IMAGE_MAX_HEIGHT;
import static com.togacure.PlayingCardImageSettings.DEFAULT_CARD_DETECTOR_IMAGE_PART;
import static com.togacure.PlayingCardImageSettings.HANDS_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.HANDS_BLOCK_HORIZONTAL_GAP;
import static com.togacure.PlayingCardImageSettings.HANDS_BLOCK_VERTICAL_GAP;
import static com.togacure.PlayingCardImageSettings.HANDS_BLOCK_WIDTH;
import static com.togacure.PlayingCardImageSettings.HANDS_BLOCK_X_OFFSET;
import static com.togacure.PlayingCardImageSettings.HANDS_BLOCK_Y_OFFSET;
import static com.togacure.PlayingCardImageSettings.HANDS_IMAGE_HEIGHT;
import static com.togacure.PlayingCardImageSettings.HANDS_IMAGE_WIDTH;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BACKGROUND_BLOCK_PERCENTAGE;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BLOCK_WIDTH;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_HORIZONTAL_GAP;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_VERTICAL_GAP;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_WIDTH;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_X_OFFSET;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_Y_OFFSET;
import static com.togacure.PlayingCardImageSettings.SUITS_IMAGE_HEIGHT;
import static com.togacure.PlayingCardImageSettings.SUITS_IMAGE_WIDTH;

/**
 * @author Vitaly Alekseev
 * @since 28.02.2021
 */
public class CardsDetectorFactory {

    private static final PredictionStrategy.RectangleDetector suitsInitialDetector = new FixedAreaDetector(SUITS_BLOCK_X_OFFSET, SUITS_BLOCK_Y_OFFSET, SUITS_BLOCK_WIDTH, SUITS_BLOCK_HEIGHT);
    private static final PredictionStrategy.RectangleDetector handsInitialDetector = new FixedAreaDetector(HANDS_BLOCK_X_OFFSET, HANDS_BLOCK_Y_OFFSET, HANDS_BLOCK_WIDTH, HANDS_BLOCK_HEIGHT);

    private static final Map<ImgKey, PredictionStrategy> strategies = new ConcurrentHashMap<>();

    public static CardsDetector getCardsDetector(final BufferedImage img) {
        final PredictionStrategy.RectangleDetector detector = new CardAreaRectangleDetector(img);
        final PredictionStrategy strategy = strategies.computeIfAbsent(new ImgKey(img.getWidth(), img.getHeight()), k ->
                new SimpleDirectPredictionStrategy(img.getWidth(),
                        img.getHeight(),
                        img.getHeight() / DEFAULT_CARD_DETECTOR_IMAGE_PART,
                        (img.getHeight() / DEFAULT_CARD_DETECTOR_IMAGE_PART) + DEFAULT_CARD_DETECTOR_IMAGE_MAX_HEIGHT,
                        PREDICTION_BLOCK_WIDTH,
                        PREDICTION_BLOCK_HEIGHT,
                        PREDICTION_BACKGROUND_BLOCK_PERCENTAGE));
        final PredictionStrategy.BlockRecognizer recognizer = new SimpleColorStatisticsBlockRecognizer(img, CARD_BACKGROUND_COLORS);
        final CardPredictor predictor = new CardPredictor(strategy, recognizer);
        return new CardsDetector(img, detector, predictor);
    }

    public static PredictionStrategy.RectangleDetector getInitialSuitsDetector() {
        return suitsInitialDetector;
    }

    public static PredictionStrategy.RectangleDetector getInitialHandsDetector() {
        return handsInitialDetector;
    }

    public static PredictionStrategy.RectangleDetector getSuitsImageDetector(final BufferedImage image) {
        return new ImagePositionCenterDetector(image, SUITS_BLOCK_HORIZONTAL_GAP, SUITS_BLOCK_VERTICAL_GAP, SUITS_IMAGE_WIDTH, SUITS_IMAGE_HEIGHT);
    }

    public static PredictionStrategy.RectangleDetector getHandsImageDetector(final BufferedImage image) {
        return new ImagePositionCenterDetector(image, HANDS_BLOCK_HORIZONTAL_GAP, HANDS_BLOCK_VERTICAL_GAP, HANDS_IMAGE_WIDTH, HANDS_IMAGE_HEIGHT);
    }

    public static BufferedImage getImage(final String directory, final String... paths) throws IOException {
        try (final InputStream is = Files.newInputStream(Paths.get(directory, paths))) {
            return ImageIO.read(is);
        }
    }

    public static BufferedImage getImage(final File file) throws IOException {
        try (final InputStream is = Files.newInputStream(file.toPath())) {
            return ImageIO.read(is);
        }
    }

    private static class ImgKey {

        private final int width;

        private final int height;


        private ImgKey(final int width, final int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ImgKey && ((ImgKey) obj).width == width && ((ImgKey) obj).height == height;
        }
    }
}
