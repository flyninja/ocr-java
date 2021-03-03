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
import static com.togacure.LearnTool.getData;

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
        final int width = Integer.parseInt(args[2]);
        final int height = Integer.parseInt(args[3]);

        final Perceptron perceptron = PerceptronFactory.loadPerceptron(width * height, weights);

        final Map<String, Integer> counts = new HashMap<>();

        Arrays.stream(Objects.requireNonNull(input.toFile().listFiles()))
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith("png"))
                .forEach(file -> {
                    final String detected = perceptron.test(getData(file));
                    counts.compute(detected, (k, v) -> v == null ? 1 : v + 1);
                });

        System.out.format("Done. Statistic '%s'\n", counts);
    }

}
