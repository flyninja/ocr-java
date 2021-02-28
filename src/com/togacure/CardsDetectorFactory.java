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
import static com.togacure.PlayingCardImageSettings.HAND_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.HAND_BLOCK_WIDTH;
import static com.togacure.PlayingCardImageSettings.HAND_BLOCK_X_OFFSET;
import static com.togacure.PlayingCardImageSettings.HAND_BLOCK_Y_OFFSET;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BACKGROUND_BLOCK_PERCENTAGE;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BLOCK_WIDTH;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_WIDTH;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_X_OFFSET;
import static com.togacure.PlayingCardImageSettings.SUITS_BLOCK_Y_OFFSET;

/**
 * @author Vitaly Alekseev
 * @since 28.02.2021
 */
public class CardsDetectorFactory {

    private static final PredictionStrategy.RectangleDetector suitsDetector = new FixedAreaDetector(SUITS_BLOCK_X_OFFSET, SUITS_BLOCK_Y_OFFSET, SUITS_BLOCK_WIDTH, SUITS_BLOCK_HEIGHT);
    private static final PredictionStrategy.RectangleDetector handDetector = new FixedAreaDetector(HAND_BLOCK_X_OFFSET, HAND_BLOCK_Y_OFFSET, HAND_BLOCK_WIDTH, HAND_BLOCK_HEIGHT);

    private static final Map<ImgKey, PredictionStrategy> strategies = new ConcurrentHashMap<>();

    public static CardsDetector getCardsDetector(final BufferedImage img) {
        final PredictionStrategy.RectangleDetector detector = new CardAreaByMaxColorCrossRectangleDetector(img);
        final PredictionStrategy strategy = strategies.computeIfAbsent(new ImgKey(img.getWidth(), img.getHeight()), k ->
                new SimpleDirectPredictionStrategy(img.getWidth(),
                        img.getHeight(),
                        img.getHeight() / 2,
                        (img.getHeight() / 2) + 100,
                        PREDICTION_BLOCK_WIDTH,
                        PREDICTION_BLOCK_HEIGHT,
                        PREDICTION_BACKGROUND_BLOCK_PERCENTAGE));
        final PredictionStrategy.BlockRecognizer recognizer = new SimpleColorStatisticsBlockRecognizer(img, CARD_BACKGROUND_COLORS);
        final CardPredictor predictor = new CardPredictor(strategy, recognizer);
        return new CardsDetector(img, detector, predictor);
    }

    public static PredictionStrategy.RectangleDetector getDefaultSuitsDetector() {
        return suitsDetector;
    }

    public static PredictionStrategy.RectangleDetector getDefaultHandDetector() {
        return handDetector;
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
