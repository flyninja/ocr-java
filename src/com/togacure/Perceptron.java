package com.togacure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.togacure.PlayingCardImageSettings.TRACE;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class Perceptron {

    private final int size;

    private final double threshold;

    private final Map<String, double[]> weights;

    private final Map<String, Double> bias;

    public Perceptron(final double threshold, final int size, final List<String> characters) {
        this.threshold = threshold;
        this.size = size;
        this.weights = new HashMap<>();
        this.bias = new HashMap<>();
        characters.forEach(this::addCharacter);
    }

    public Perceptron(final double threshold, final int size, final Map<String, double[]> weights, final Map<String, Double> bias) {
        this.threshold = threshold;
        this.size = size;
        if (weights.values().stream().anyMatch(v -> v.length != size)) {
            throw new IllegalArgumentException(String.format("All weights arrays must be %s size", size));
        }
        if (bias != null && weights.size() != bias.size()) {
            throw new IllegalArgumentException("weights and bias must be similar size");
        }
        this.weights = weights;
        if (bias == null) {
            this.bias = new HashMap<>();
            weights.keySet().forEach(c -> this.bias.put(c, createInitialBias()));
        } else {
            this.bias = bias;
        }
    }

    public void addCharacter(final String character) {
        weights.put(character, createInitialWeights(size));
        bias.put(character, createInitialBias());
    }

    public Map<String, double[]> getWeights() {
        return weights.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void trainTrue(final String character, final double[][] input) {
        train(character, flatMap(input), sum -> sum >= threshold, Double::sum);
    }

    public void trainFalse(final String character, final double[][] input) {
        train(character, flatMap(input), sum -> sum < threshold, (w, i) -> w - i);
    }

    public String test(final double[][] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final double[] data = flatMap(input);
        if (data.length != size) {
            throw new IllegalArgumentException(String.format("input length must be %s size but %s has been received", size, input.length));
        }
        return weights.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            final double sum = sum(bias.get(e.getKey()), e.getValue(), data);
            if (TRACE) {
                System.out.format("test: character: %s sum: %s\n", e.getKey(), sum);
            }
            return sum;
        })).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(Perceptron::decodeCharacterName)
                .orElse(null);
    }

    public String getNextSimilarCharacterName(final String character) {
        if (!weights.containsKey(character)) {
            return character;
        }
        int idx = 0;
        for (final String key : weights.keySet()) {
            if (decodeCharacterName(key).equals(character)) {
                final String[] arr = key.split("_");
                if (arr.length > 1) {
                    idx = Math.max(idx, Integer.parseInt(arr[1]));
                }
            }
        }
        return character + "_" + (idx + 1);
    }

    private void train(final String character, final double[] input, final Predicate<Double> testCorrect, final BiFunction<Double, Double, Double> recalculate) {
        if (character == null || input == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (input.length != size) {
            throw new IllegalArgumentException(String.format("input length must be %s size but %s has been received", size, input.length));
        }
        final double[] weights = this.weights.get(character);
        if (testCorrect.test(sum(this.bias.get(character), weights, input))) {
            for (int i = 0; i < size; i++) {
                weights[i] = recalculate.apply(weights[i], input[i]);
            }
        }
    }

    private double sum(final double bias, final double[] weights, final double[] input) {
        double sum = bias;
        for (int i = 0; i < size; i++) {
            sum += input[i] * weights[i];
        }
        return sum;
    }

    private double[] flatMap(final double[][] data) {
        final double[] result = new double[data.length * data[0].length];
        for (int y = 0; y < data.length; y++) {
            System.arraycopy(data[y], 0, result, data[0].length * y, data[0].length);
        }
        return result;
    }

    private static double[] createInitialWeights(final int size) {
        return new double[size];
    }

    private static Double createInitialBias() {
        return (double) 0;
    }

    private static String decodeCharacterName(final String character) {
        return character.split("_")[0];
    }
}
