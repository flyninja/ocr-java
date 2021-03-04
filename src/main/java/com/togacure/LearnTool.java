package com.togacure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.togacure.Utils.getInputFolder;
import static com.togacure.Utils.trainImage;
import static com.togacure.Utils.trainImageFalse;

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
     *             - 2: output file weights
     *             - 3: image width
     *             - 4: image height
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 5) {
            throw new IllegalArgumentException(String.format("five arguments are expected but have/has been received %s", Arrays.toString(args)));
        }

        final Path input = getInputFolder(args[0]);
        final Path output = Paths.get(args[1]);
        final String template = args[2];
        final int width = Integer.parseInt(args[3]);
        final int height = Integer.parseInt(args[4]);

        if (!Files.exists(output)) {
            Files.createDirectories(output);
        }

        final List<String> characters = Arrays.stream(Objects.requireNonNull(input.toFile().listFiles())).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());

        final Perceptron perceptron = PerceptronFactory.createPerceptron(width * height, characters);

        characters.forEach(c -> trainZero(perceptron, c));

        Arrays.stream(Objects.requireNonNull(input.toFile().listFiles())).filter(File::isDirectory).forEach(folder -> trainFolder(perceptron, characters, folder));

        PerceptronFactory.store(output.resolve(template + ".weights"), perceptron.getWeights());

        System.out.println("Done.");
    }

    private static void trainFolder(final Perceptron perceptron, final List<String> characters, final File folder) {
        System.out.format("train folder: %s\n", folder.getName());
        final String character = folder.getName();
        Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith("png"))
                .forEach(file -> {
                    trainImage(perceptron, file, character);
                    trainImagesFalse(perceptron, characters, file, character);
                });
    }

    private static void trainImagesFalse(final Perceptron perceptron, final List<String> characters, final File file, final String character) {
        characters.stream().filter(c -> !c.equals(character)).forEach(c -> trainImageFalse(perceptron, file, character));
    }

    private static void trainZero(final Perceptron perceptron, final String character) {
        System.out.format("train zero: %s\n", character);
        final double[] zero = new double[perceptron.getSize()];
        perceptron.trainFalse(character, zero);
    }

}
