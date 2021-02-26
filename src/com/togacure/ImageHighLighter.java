package com.togacure;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import static com.togacure.PlayingCardImageSettings.CARD_BACKGROUND_COLORS;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BACKGROUND_BLOCK_PERCENTAGE;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BLOCK_HEIGHT;
import static com.togacure.PlayingCardImageSettings.PREDICTION_BLOCK_WIDTH;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public final class ImageHighLighter {

    private static final int VERTICAL_CELL_SIZE = PREDICTION_BLOCK_WIDTH;
    private static final int HORIZONTAL_CELL_SIZE = PREDICTION_BLOCK_HEIGHT;

    public static void main(final String[] args) throws IOException {
        try (final InputStream is = Files.newInputStream(Paths.get("resources", "imgs", args[0]))) {
            final BufferedImage img = ImageIO.read(is);
            System.out.format("%s\n", img);

            if ("cells".equals(args[1])) {
                cells(img);
            } else if ("cell".equals(args[1])) {
                cell(img, args[2]);
            } else if ("cards".equals(args[1])) {
                cards(img);
            }

            ImageIO.write(img, "PNG", new File(args[0]));
        }
        System.out.println("Done.");
    }

    private static void cells(final BufferedImage img) {
        for (int i = 0; i < img.getWidth(); i += HORIZONTAL_CELL_SIZE) {
            for (int j = img.getHeight() / 2; j < img.getHeight(); j++) {
                img.setRGB(i, j, Color.RED.getRGB());
            }
        }

        for (int i = img.getHeight() / 2; i < img.getHeight(); i += VERTICAL_CELL_SIZE) {
            for (int j = 0; j < img.getWidth(); j++) {
                img.setRGB(j, i, Color.RED.getRGB());
            }
        }
    }

    private static void cell(final BufferedImage img, final String cellStr) {
        final int cell = Integer.parseInt(cellStr);
        for (int i = 0; i < img.getWidth(); i += HORIZONTAL_CELL_SIZE) {
            if (i == (cell * HORIZONTAL_CELL_SIZE)) {
                int y = img.getHeight() / 2;
                for (int j = i; j < i + HORIZONTAL_CELL_SIZE; j++) {
                    img.setRGB(j, y, Color.RED.getRGB());
                    img.setRGB(j, y + VERTICAL_CELL_SIZE, Color.RED.getRGB());
                }
                for (int j = y; j < y + VERTICAL_CELL_SIZE; j++) {
                    img.setRGB(i, j, Color.RED.getRGB());
                    img.setRGB(i + HORIZONTAL_CELL_SIZE, j, Color.RED.getRGB());
                }
                for (int j = y; j < y + VERTICAL_CELL_SIZE; j++) {
                    String row = String.format("row: %s: RGB's: ", j);
                    for (int k = i; k < i + HORIZONTAL_CELL_SIZE; k++) {
                        row = String.format("%s 0x%x", row, img.getRGB(k, j));
                    }
                    System.out.format("%s\n", row);
                }
                break;
            }
        }
    }

    private static void cards(final BufferedImage img) {
        final PredictionStrategy.BlockRecognizer recognizer = new SimpleColorStatisticsBlockRecognizer(img, CARD_BACKGROUND_COLORS);
        final PredictionStrategy.RectangleDetector detector = new SimpleMaxColorCrossRectangleDetector(img);
        final PredictionStrategy strategy = new SimpleDirectPredictionStrategy(img.getWidth(),
                img.getHeight(),
                img.getHeight() / 2,
                (img.getHeight() / 2) + 100,
                PREDICTION_BLOCK_WIDTH,
                PREDICTION_BLOCK_HEIGHT,
                PREDICTION_BACKGROUND_BLOCK_PERCENTAGE);
        final CardPredictor predictor = new CardPredictor(strategy, recognizer);
        predictor.predict();
        System.out.format("predicted blocks: %s\n", predictor.getBlocks());
        final List<PredictionStrategy.Block> cards = predictor.getBlocks().stream()
                .map(detector::takeRectangle)
                .filter(block -> block.getWidth() != 0 && block.getHeight() != 0)
                .distinct()
                .collect(Collectors.toList());
        System.out.format("cards: %s\n", cards);
        cards.forEach(card -> {
            for (int i = 0; i < card.getWidth(); i++) {
                img.setRGB(card.getX() + i, card.getY(), Color.RED.getRGB());
                img.setRGB(card.getX() + i, card.getY() + card.getHeight(), Color.RED.getRGB());
            }

            for (int i = 0; i < card.getHeight(); i++) {
                img.setRGB(card.getX(), card.getY() + i, Color.RED.getRGB());
                img.setRGB(card.getX() + card.getWidth(), card.getY() + i, Color.RED.getRGB());
            }
        });
    }

}
