package com.togacure;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public class SimpleColorStatisticsBlockRecognizer implements PredictionStrategy.BlockRecognizer {

    private final BufferedImage image;

    private final Set<Integer> colors;

    public SimpleColorStatisticsBlockRecognizer(final BufferedImage image, final Set<Integer> colors) {
        this.image = image;
        this.colors = colors;
    }

    @Override
    public void recognize(final Block block) {
        final Map<Integer, Double> counters = new HashMap<>();
        for (int i = block.getY(); i < block.getY() + block.getHeight(); i++) {
            for (int j = block.getX(); j < block.getX() + block.getWidth(); j++) {
                counters.compute(image.getRGB(j, i), (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        final Map.Entry<Integer, Double> max = counters.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow(IllegalStateException::new);
        if (colors.contains(max.getKey())) {
            final double percent = ((double) (block.getWidth() * block.getHeight())) / 100;
            block.setM(max.getValue() / percent);
            block.setBackground(max.getKey());
        }
    }

}
