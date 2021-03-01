package com.togacure;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaly Alekseev
 * @since 20.02.2021
 */
public class PlayingCardImageSettings {

    public static boolean TRACE = false;

    public static final int PREDICTION_BLOCK_WIDTH = 20;

    public static final int PREDICTION_BLOCK_HEIGHT = 20;

    public static final double PREDICTION_BACKGROUND_BLOCK_PERCENTAGE = 70;

    public static final int SUITS_BLOCK_X_OFFSET = 3;

    public static final int SUITS_BLOCK_Y_OFFSET = 28;

    public static final int SUITS_BLOCK_WIDTH = 27;

    public static final int SUITS_BLOCK_HEIGHT = 27;

    public static final int HAND_BLOCK_X_OFFSET = 3;

    public static final int HAND_BLOCK_Y_OFFSET = 3;

    public static final int HAND_BLOCK_WIDTH = 30;

    public static final int HAND_BLOCK_HEIGHT = 27;

    public static final Set<Integer> CARD_BACKGROUND_COLORS = new HashSet<>();

    static {
        CARD_BACKGROUND_COLORS.add(Color.WHITE.getRGB());
        CARD_BACKGROUND_COLORS.add(new Color(0x78, 0x78, 0x78).getRGB());
    }

    public static final double DEFAULT_PERCEPTRON_THRESHOLD = 0.0;

    public static final double DEFAULT_PERCEPTRON_LR = 1.0;

    public static final int DEFAULT_CARD_DETECTOR_IMAGE_PART = 2;

    public static final int DEFAULT_CARD_DETECTOR_IMAGE_MAX_HEIGHT = 2;

}
