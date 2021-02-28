package com.togacure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class ImageSplitParts {

    /**
     * This program takes set of images, splits detected cards to suits and hands sub-images and stores them to output
     *
     * @param args program arguments:
     *             - 0: input folder name (relative to current dir)
     *             - 1: output folder name (relative to current dir)
     */
    public static void main(final String[] args) throws IOException {

        if (args.length != 2) {
            throw new IllegalArgumentException(String.format("two arguments are expected but was/were gotten %s", Arrays.toString(args)));
        }

        final Path input = Paths.get(args[0]);
        if (!Files.exists(input) || !Files.isDirectory(input)) {
            throw new IllegalArgumentException(String.format("%s should be directory", input));
        }

        final Path output = Paths.get(args[1]);
        Files.deleteIfExists(output);
        Files.createDirectories(output.resolve("suits"));
        Files.createDirectories(output.resolve("hands"));

        Arrays.stream(input.toFile().listFiles()).forEach(imgFile -> storeSuitAndHand(imgFile, output));

        System.out.println("Done.");
    }

    private static void storeSuitAndHand(final File imgFile, final Path output) {
        try {
            final BufferedImage img = CardsDetectorFactory.getImage(imgFile);
            final CardsDetector detector = CardsDetectorFactory.getCardsDetector(img);
            final List<BufferedImage> suits = detector.getSuitsImages();
            for (int i = 0; i < suits.size(); i++) {
                store(suits.get(i), imgFile, output.resolve("suits"), i);
            }
            final List<BufferedImage> hands = detector.getHandsImages();
            for (int i = 0; i < suits.size(); i++) {
                store(hands.get(i), imgFile, output.resolve("hands"), i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void store(final BufferedImage img, final File file, final Path output, final int idx) {
        try {
            ImageIO.write(img, "PNG", output.resolve(file.getName().replace(".png", "." + idx + ".png")).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
