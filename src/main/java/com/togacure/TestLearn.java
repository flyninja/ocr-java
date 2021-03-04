package com.togacure;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.togacure.Utils.getBackground;


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

        final int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

        final String detected = perceptron.test(PerceptronFactory.rgb2bin(rgb, getBackground(rgb)));

        System.out.format("Done. Detected '%s'\n", detected);
    }

}
