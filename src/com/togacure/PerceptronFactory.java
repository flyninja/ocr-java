package com.togacure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.togacure.PlayingCardImageSettings.DEFAULT_PERCEPTRON_LR;
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

    public static Perceptron createPerceptron(final int size) {
        return new Perceptron(DEFAULT_PERCEPTRON_THRESHOLD, DEFAULT_PERCEPTRON_LR, size);
    }

    public static Perceptron loadPerceptron(final int size, final Path weights, final Path bias) throws IOException {
        return new Perceptron(DEFAULT_PERCEPTRON_THRESHOLD, DEFAULT_PERCEPTRON_LR, size, load(weights), load(bias));
    }

}
