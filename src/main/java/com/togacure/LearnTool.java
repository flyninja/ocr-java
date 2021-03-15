package com.togacure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.togacure.Utils.getInputFolder;
import static com.togacure.Utils.trainImage;

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
     *             - 2: output file name template
     *             - 3: image width
     *             - 4: image height
     *             - 5: epoch count
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 6) {
            throw new IllegalArgumentException(String.format("five arguments are expected but have/has been received %s", Arrays.toString(args)));
        }

        final Path input = getInputFolder(args[0]);
        final Path output = Paths.get(args[1]);
        final String template = args[2];
        final int width = Integer.parseInt(args[3]);
        final int height = Integer.parseInt(args[4]);
        final int epochCount = Integer.parseInt(args[5]);

        if (!Files.exists(output)) {
            Files.createDirectories(output);
        }

        final Map<String, double[]> outputs = Arrays.stream(Objects.requireNonNull(input.toFile().listFiles())).filter(File::isDirectory).collect(Collectors.toMap(folder -> folder.getName(),
                folder -> Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                        .filter(File::isFile)
                        .filter(file -> file.getName().endsWith("png"))
                        .findFirst()
                        .map(Utils::getData)
                        .orElseThrow(IllegalStateException::new)));

        final Perceptron perceptron = PerceptronFactory.createPerceptron(width * height, outputs);

        Arrays.stream(Objects.requireNonNull(input.toFile().listFiles())).filter(File::isDirectory).forEach(folder -> trainFolder(perceptron, folder, epochCount));

        PerceptronFactory.store(output.resolve(template + ".weights"), perceptron.getWeights());
        PerceptronFactory.store(output.resolve(template + ".outputs"), outputs);

        System.out.println("Done.");
    }

    private static void trainFolder(final Perceptron perceptron, final File folder, final int epochCount) {
        System.out.format("train folder: %s\n", folder.getName());
        final String character = folder.getName();
        final List<File> images = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith("png"))
                .collect(Collectors.toList());
        for (int i = 0; i < epochCount; i++) {
            Collections.shuffle(images);
            trainFolder(perceptron, character, images);
        }
    }

    private static void trainFolder(final Perceptron perceptron, final String character, final List<File> folderFiles) {
        folderFiles.stream()
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith("png"))
                .forEach(file -> trainImage(perceptron, file, character));
    }

}
