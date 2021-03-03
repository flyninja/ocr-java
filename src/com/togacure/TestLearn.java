package com.togacure;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.togacure.LearnTool.getBackground;
import static com.togacure.LearnTool.getData;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class TestLearn {

    public static void main(final String[] args) throws IOException {
        PlayingCardImageSettings.TRACE = true;
        final BufferedImage img = CardsDetectorFactory.getImage(args[0]);
        final Path weights = Paths.get(args[1]);
        final int width = Integer.parseInt(args[2]);
        final int height = Integer.parseInt(args[3]);

        final Perceptron perceptron = PerceptronFactory.loadPerceptron(width * height, weights);

        final String detected = perceptron.test(getData(Paths.get(args[0]).toFile()));

        System.out.format("Done. Detected '%s'\n", detected);
    }

}
