package com.togacure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.togacure.LearnTool.getBackground;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class TestLearnStat {

    public static void main(final String[] args) throws IOException {
        final Path input = Paths.get(args[0]);
        if (!Files.exists(input) || !Files.isDirectory(input)) {
            throw new IllegalArgumentException(String.format("%s should be directory", input));
        }
        final Path weights = Paths.get(args[1]);
        final Path bias = Paths.get(args[2]);
        final int width = Integer.parseInt(args[3]);
        final int height = Integer.parseInt(args[4]);

        final Perceptron perceptron = PerceptronFactory.loadPerceptron(width * height, weights, bias);

        final Map<String, Integer> counts = new HashMap<>();

        Arrays.stream(Objects.requireNonNull(input.toFile().listFiles()))
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith("png"))
                .forEach(file -> {
                    try {
                        final BufferedImage img = CardsDetectorFactory.getImage(file);
                        final int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
                        final String detected = perceptron.test(PerceptronFactory.rgb2bipolar(rgb, getBackground(rgb)));
                        counts.compute(detected, (k, v) -> v == null ? 1 : v + 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        System.out.format("Done. Statistic '%s'\n", counts);
    }

}
