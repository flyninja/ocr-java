package com.togacure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.togacure.PlayingCardImageSettings.DEFAULT_PERCEPTRON_THRESHOLD;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class PerceptronFactory {

    public static <T> void store(final Path path, final T obj) throws IOException {
        try (final ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(path))) {
            os.writeObject(obj);
        }
    }

    public static <T> T load(final Path path) throws IOException {
        try (final ObjectInputStream is = new ObjectInputStream(Files.newInputStream(path))) {
            return (T) is.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public static Perceptron createPerceptron(final int size, final List<String> characters) {
        return new Perceptron(DEFAULT_PERCEPTRON_THRESHOLD, size, characters);
    }

    public static Perceptron loadPerceptron(final int size, final Path weights) throws IOException {
        return new Perceptron(DEFAULT_PERCEPTRON_THRESHOLD, size, load(weights), null);
    }

    public static double[][] rgb2bin(final int[][] rgb, final int background) {
        final double[][] result = new double[rgb.length][rgb[0].length];
        for (int y = 0; y < rgb.length; y++) {
            for (int x = 0; x < rgb.length; x++) {
                result[y][x] = rgb[y][x] == background ? 0 : 1;
            }
        }
        return result;
    }
}
