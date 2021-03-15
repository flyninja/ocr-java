package com.togacure;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
        try (final InputStream is = Files.newInputStream(path)) {
            return load(is);
        }
    }

    public static <T> T load(final InputStream is) throws IOException {
        try {
            return (T) new ObjectInputStream(is).readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public static Perceptron createPerceptron(final int size, final Map<String, double[]> outputs) {
        return new Perceptron(size, outputs);
    }

    public static Perceptron loadPerceptron(final int size, final Path weights, final Path outputs) throws IOException {
        return new Perceptron(size, load(weights), load(outputs));
    }

    public static Perceptron loadPerceptron(final int size, final InputStream weights, final InputStream outputs) throws IOException {
        return new Perceptron(size, load(weights), load(outputs));
    }

    public static double[] rgb2bin(final int[] rgb, final int background) {
        final double[] result = new double[rgb.length];
        for (int i = 0; i < rgb.length; i++) {
            result[i] = rgb[i] == background ? 1 : 0;
        }
        return result;
    }
}
