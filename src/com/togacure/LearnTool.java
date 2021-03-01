package com.togacure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.togacure.PlayingCardImageSettings.CARD_BACKGROUND_COLORS;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class LearnTool {


    /**
     * This program takes sets of images which were split to separate folders in input, trains perceptron and stores bias and weights to output
     *
     * @param args program arguments:
     *             - 0: input root folder name (relative to current dir), sub folders are counted as characters-names
     *             - 1: output folder name (relative to current dir)
     *             - 2: output files template (result will be as pair [template.name.weights  template.name.bias])
     *             - 3: image width
     *             - 4: image height
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 5) {
            throw new IllegalArgumentException(String.format("five arguments are expected but have/has been received %s", Arrays.toString(args)));
        }

        final Path input = Paths.get(args[0]);
        if (!Files.exists(input) || !Files.isDirectory(input)) {
            throw new IllegalArgumentException(String.format("%s should be directory", input));
        }

        final Path output = Paths.get(args[1]);

        final String template = args[2];

        final int width = Integer.parseInt(args[3]);

        final int height = Integer.parseInt(args[4]);

        if (!Files.exists(output)) {
            Files.createDirectories(output);
        }

        final Perceptron perceptron = PerceptronFactory.createPerceptron(width * height);

        Arrays.stream(Objects.requireNonNull(input.toFile().listFiles())).filter(File::isDirectory).forEach(folder -> trainFolder(perceptron, folder));

        PerceptronFactory.store(output.resolve(template + ".weights"), perceptron.getWeights());
        PerceptronFactory.store(output.resolve(template + ".bias"), perceptron.getBias());

        System.out.println("Done.");
    }

    private static void trainFolder(final Perceptron perceptron, final File folder) {
        Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith("png"))
                .forEach(file -> trainImage(perceptron, file, folder.getName()));
    }

    private static void trainImage(final Perceptron perceptron, final File file, final String character) {
        System.out.format("train image: character: %s file: %s\n", character, file.getName());
        try {
            final BufferedImage img = CardsDetectorFactory.getImage(file);
            final int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
            perceptron.train(character, PerceptronFactory.rgb2bipolar(rgb, getBackground(rgb)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static int getBackground(final int[] rgb) {
        final Map<Integer, Integer> counters = new HashMap<>();
        for (int i = 0; i < rgb.length; i++) {
            counters.compute(rgb[i], (k, v) -> v == null ? 1 : v + 1);
        }
        return CARD_BACKGROUND_COLORS.stream().filter(counters::containsKey).max(Comparator.comparing(counters::get)).orElseThrow(IllegalStateException::new);
    }

}
