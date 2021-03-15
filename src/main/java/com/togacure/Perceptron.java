package com.togacure;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.togacure.PlayingCardImageSettings.PERCEPTRON_OUTPUT_MATCH_THRESHOLD;
import static com.togacure.PlayingCardImageSettings.TRACE;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class Perceptron {

    private static final Random random = new Random(1);

    private final int size;

    private final Map<String, double[]> weights;

    private final Map<String, double[]> outputs;

    public Perceptron(final int size, final Map<String, double[]> outputs) {
        this.size = size;
        this.weights = new HashMap<>();
        this.outputs = outputs;
        outputs.keySet().forEach(character -> weights.put(character, createInitialWeights(size)));
    }

    public Perceptron(final int size, final Map<String, double[]> weights, final Map<String, double[]> outputs) {
        this.size = size;
        if (weights.values().stream().anyMatch(v -> v.length != size)) {
            throw new IllegalArgumentException(String.format("All weights arrays must be %s size", size));
        }
        this.weights = weights;
        this.outputs = outputs;
    }

    public Map<String, double[]> getWeights() {
        return weights.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void train(final String character, final double[] input) {
        train(character, input, this::activation, this::derivation);
    }

    public String test(final double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (input.length != size) {
            throw new IllegalArgumentException(String.format("input length must be %s size but %s has been received", size, input.length));
        }
        return weights.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            final double[] value = dot(e.getValue(), input);
            apply(value, this::activation);
            final double[] outputs = this.outputs.get(e.getKey());
            final int match = match(value, outputs, (d1, d2) -> Math.abs(Math.abs(d1) - Math.abs(d2)) <= PERCEPTRON_OUTPUT_MATCH_THRESHOLD ? 0 : d1.compareTo(d2));
            if (TRACE) {
                System.out.format("test: character: %s match: %s\n", e.getKey(), match);
            }
            return match;
        })).entrySet()
                .stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void train(final String character, final double[] input, final Function<Double, Double> activation, final Function<Double, Double> derivation) {
        if (character == null || input == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (input.length != size) {
            throw new IllegalArgumentException(String.format("input length must be %s size but %s has been received", size, input.length));
        }
        final double[] weights = this.weights.get(character);
        final double[] outputs = this.outputs.get(character);
        final double[] l1 = dot(weights, input);
        apply(l1, activation);
        final double[] error = sub(outputs, l1);
        apply(l1, derivation);
        final double[] delta = dot(error, l1);
        final double[] grad = dot(input, delta);
        add(weights, grad);
    }

    private double[] dot(final double[] first, final double[] second) {
        final double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = first[i] * second[i];
        }
        return result;
    }

    private void apply(final double[] d, final Function<Double, Double> f) {
        for (int i = 0; i < size; i++) {
            d[i] = f.apply(d[i]);
        }
    }

    private double[] sub(final double[] first, final double[] second) {
        final double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = first[i] - second[i];
        }
        return result;
    }

    private void add(final double[] first, final double[] second) {
        for (int i = 0; i < size; i++) {
            first[i] += second[i];
        }
    }

    private double activation(final double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private double derivation(final double x) {
        return x * (1 - x);
    }

    private int match(final double[] first, final double[] second, final Comparator<Double> comparator) {
        for (int i = 0; i < size; i++) {
            int c = comparator.compare(first[i], second[i]);
            if (c != 0) {
                if (TRACE) {
                    System.out.format("match: first: %s second: %s index: %s\n", first[i], second[i], i);
                }
                return c;
            }
        }
        return 0;
    }

    private static double[] createInitialWeights(final int size) {
        return random.doubles(size, -1, 1).toArray();
    }

}
