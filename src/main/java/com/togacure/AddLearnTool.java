package com.togacure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.togacure.Utils.getData;
import static com.togacure.Utils.getInputFolder;
import static com.togacure.Utils.trainImage;
import static com.togacure.Utils.trainImageFalse;

/**
 * @author Vitaly Alekseev
 * @since 03.03.2021
 */
public class AddLearnTool {

    /**
     * This program takes sets of images with specified character for additional learning
     *
     * @param args program arguments:
     *             - 0: input root folder name (relative to current dir)
     *             - 1: weights file
     *             - 2: output weights file
     *             - 3: image width
     *             - 4: image height
     *             - 5: character
     *             - 6: mode: rep (repeat learn of that character); fal (false-learn of that character); swt (switch to new character for false-tested)
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 7) {
            throw new IllegalArgumentException(String.format("seven arguments are expected but have/has been received %s", Arrays.toString(args)));
        }
        final Path input = getInputFolder(args[0]);
        final Path output = Paths.get(args[1]);
        final Path weights = Paths.get(args[2]);
        final int width = Integer.parseInt(args[3]);
        final int height = Integer.parseInt(args[4]);
        final String character = args[5];
        final String mode = args[6];

        final Perceptron perceptron = PerceptronFactory.loadPerceptron(width * height, weights);

        if ("rep".equals(mode)) {
            Arrays.stream(Objects.requireNonNull(input.toFile().listFiles()))
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith("png"))
                    .forEach(file -> trainImage(perceptron, file, character));
        } else if ("fal".equals(mode)) {
            Arrays.stream(Objects.requireNonNull(input.toFile().listFiles()))
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith("png"))
                    .forEach(file -> trainImageFalse(perceptron, file, character));
        } else if ("swt".equals(mode)) {
            final List<File> wrongDetected = Arrays.stream(Objects.requireNonNull(input.toFile().listFiles()))
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith("png"))
                    .filter(file -> !character.equals(perceptron.test(getData(file))))
                    .collect(Collectors.toList());
            if (!wrongDetected.isEmpty()) {
                final String nextCharacter = perceptron.getNextSimilarCharacterName(character);
                wrongDetected.forEach(file -> trainImage(perceptron, file, nextCharacter));
                System.out.format("nextCharacter: %s\n", nextCharacter);
            }
        }

        PerceptronFactory.store(output, perceptron.getWeights());

        System.out.println("Done.");
    }
}
