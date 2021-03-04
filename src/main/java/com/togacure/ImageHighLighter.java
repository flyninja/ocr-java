package com.togacure;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

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
        PlayingCardImageSettings.TRACE = true;

        final BufferedImage img = CardsDetectorFactory.getImage("input", "imgs", args[0]);

        System.out.format("%s\n", img);

        if ("cells".equals(args[1])) {
            cells(img);
        } else if ("cell".equals(args[1])) {
            cell(img, args[2]);
        } else if ("cards".equals(args[1])) {
            cards(img, args[2]);
        }

        ImageIO.write(img, "PNG", new File(args[0]));

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
                rectangle(img, new Block(i, y, HORIZONTAL_CELL_SIZE, VERTICAL_CELL_SIZE));
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

    private static void cards(final BufferedImage img, final String area) {
        final CardsDetector detector = CardsDetectorFactory.getCardsDetector(img);
        final List<Block> cards = detector.getCards();
        System.out.format("cards: %s\n", cards);
        cards.forEach(card -> rectangle(img, card));
        if ("suits".equals(area)) {
            detector.getSuits().forEach(suit -> rectangle(img, suit));
        } else if ("hands".equals(area)) {
            detector.getHands().forEach(hand -> rectangle(img, hand));
        }
    }

    private static void rectangle(final BufferedImage img, final Block block) {
        for (int i = 0; i < block.getWidth(); i++) {
            img.setRGB(block.getX() + i, block.getY(), Color.RED.getRGB());
            img.setRGB(block.getX() + i, block.getY() + block.getHeight(), Color.RED.getRGB());
        }

        for (int i = 0; i < block.getHeight(); i++) {
            img.setRGB(block.getX(), block.getY() + i, Color.RED.getRGB());
            img.setRGB(block.getX() + block.getWidth(), block.getY() + i, Color.RED.getRGB());
        }
    }
}
