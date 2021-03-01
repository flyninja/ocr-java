package com.togacure;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.togacure.PlayingCardImageSettings.TRACE;

/**
 * @author Vitaly Alekseev
 * @since 01.03.2021
 */
public class Perceptron {

    private final int size;

    private final double threshold;

    private final double lr;

    private final Map<String, double[]> weights;

    private final Map<String, Double> bias;

    public Perceptron(final double threshold, final double lr, final int size) {
        this(threshold, lr, size, null, null);
    }

    public Perceptron(final double threshold, final double lr, final int size, final Map<String, double[]> weights, final Map<String, Double> bias) {
        this.threshold = threshold;
        this.lr = lr;
        this.size = size;
        if (weights != null & bias != null) {
            if (weights.values().stream().anyMatch(v -> v.length != size)) {
                throw new IllegalArgumentException(String.format("All weights arrays must be %s size", size));
            }
            if (weights.size() != bias.size()) {
                throw new IllegalArgumentException("weights and bias must be similar size");
            }
            this.weights = weights;
            this.bias = bias;
        } else {
            this.weights = new HashMap<>();
            this.bias = new HashMap<>();
        }
    }

    public Map<String, double[]> getWeights() {
        return weights.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Double> getBias() {
        return bias.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void train(final String character, final double[] input) {
        if (character == null || input == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (input.length != size) {
            throw new IllegalArgumentException(String.format("input length must be %s size but %s has been received", size, input.length));
        }
        final double[] weights = this.weights.computeIfAbsent(character, k -> createInitialWeights(size));
        final double bias = this.bias.computeIfAbsent(character, k -> createInitialBias());
        final double sum = sum(bias, weights, input);
        final double res = sum > threshold ? 1 : (sum < -threshold ? -1 : 0);
        if (res != 1) {
            final double error = 1 - res;
            for (int i = 0; i < size; i++) {
                weights[i] = weights[i] + lr * input[i] * error;
            }
            this.bias.put(character, bias + lr * error);
        }
    }

    public String test(final double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (input.length != size) {
            throw new IllegalArgumentException(String.format("input length must be %s size but %s has been received", size, input.length));
        }
        return weights.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            final double sum = sum(bias.get(e.getKey()), e.getValue(), input);
            if (TRACE) {
                System.out.format("test: character: %s sum: %s\n", e.getKey(), sum);
            }
            return sum;
        })).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private double sum(final double bias, final double[] weights, final double[] input) {
        double sum = bias;
        for (int i = 0; i < size; i++) {
            sum += input[i] * weights[i];
        }
        return sum;
    }

    private static double[] createInitialWeights(final int size) {
        return new double[size];
    }

    private static Double createInitialBias() {
        return (double) 0;
    }
}
