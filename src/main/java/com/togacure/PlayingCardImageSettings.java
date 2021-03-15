package com.togacure;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaly Alekseev
 * @since 20.02.2021
 */
public class PlayingCardImageSettings {

    public static final String SUITS_WEIGHTS_RESOURCE = "suits.weights";
    public static final String SUITS_OUTPUTS_RESOURCE = "suits.outputs";
    public static final String HANDS_WEIGHTS_RESOURCE = "hands.weights";
    public static final String HANDS_OUTPUTS_RESOURCE = "hands.outputs";

    public static boolean TRACE = false;

    public static final int PREDICTION_BLOCK_WIDTH = 20;

    public static final int PREDICTION_BLOCK_HEIGHT = 20;

    public static final double PREDICTION_BACKGROUND_BLOCK_PERCENTAGE = 70;

    public static final int SUITS_BLOCK_X_OFFSET = 25;

    public static final int SUITS_BLOCK_Y_OFFSET = 45;

    public static final int SUITS_BLOCK_WIDTH = 35;

    public static final int SUITS_BLOCK_HEIGHT = 35;

    public static final int HANDS_BLOCK_X_OFFSET = 3;

    public static final int HANDS_BLOCK_Y_OFFSET = 3;

    public static final int HANDS_BLOCK_WIDTH = 30;

    public static final int HANDS_BLOCK_HEIGHT = 28;

    public static final int SUITS_IMAGE_WIDTH = 35;

    public static final int SUITS_IMAGE_HEIGHT = 35;

    public static final int SUITS_BLOCK_VERTICAL_GAP = 4;

    public static final int SUITS_BLOCK_HORIZONTAL_GAP = 4;

    public static final int HANDS_IMAGE_WIDTH = 30;

    public static final int HANDS_IMAGE_HEIGHT = 28;

    public static final int HANDS_BLOCK_VERTICAL_GAP = 2;

    public static final int HANDS_BLOCK_HORIZONTAL_GAP = 2;

    public static final Set<Integer> CARD_BACKGROUND_COLORS = new HashSet<>();

    static {
        CARD_BACKGROUND_COLORS.add(Color.WHITE.getRGB());
        CARD_BACKGROUND_COLORS.add(new Color(0x78, 0x78, 0x78).getRGB());
    }

    public static final int DEFAULT_CARD_DETECTOR_IMAGE_PART = 2;

    public static final int DEFAULT_CARD_DETECTOR_IMAGE_MAX_HEIGHT = 100;

    public static final double PERCEPTRON_OUTPUT_MATCH_THRESHOLD = 0.5d;
}
