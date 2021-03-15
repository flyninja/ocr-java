package com.togacure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.togacure.PlayingCardImageSettings.CARD_BACKGROUND_COLORS;

/**
 * @author Vitaly Alekseev
 * @since 04.03.2021
 */
public final class Utils {

    static Path getInputFolder(final String arg) {
        final Path input = Paths.get(arg);
        if (!Files.exists(input) || !Files.isDirectory(input)) {
            throw new IllegalArgumentException(String.format("%s should be directory", input));
        }
        return input;
    }

    static double[] getData(final File file) {
        try {
            final BufferedImage img = CardsDetectorFactory.getImage(file);
            final int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
            return getData(img, new Block(0, 0, img.getWidth(), img.getHeight()), getBackground(rgb));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static double[] getData(final BufferedImage img, final Block block, final int background) {
        final int[] rgb = img.getRGB(block.getX(), block.getY(), block.getWidth(), block.getHeight(), null, 0, block.getWidth());
        return PerceptronFactory.rgb2bin(rgb, background);
    }

    static int getBackground(final int[] rgb) {
        final Map<Integer, Integer> counters = new HashMap<>();
        for (int j : rgb) {
            counters.compute(j, (k, v) -> v == null ? 1 : v + 1);
        }
        return CARD_BACKGROUND_COLORS.stream().filter(counters::containsKey).max(Comparator.comparing(counters::get)).orElseThrow(IllegalStateException::new);
    }

    static void trainImage(final Perceptron perceptron, final File file, final String character) {
        System.out.format("train image: character: %s file: %s\n", character, file.getName());
        final double[] bin = getData(file);
        perceptron.train(character, bin);
    }

    private Utils() {

    }
}
