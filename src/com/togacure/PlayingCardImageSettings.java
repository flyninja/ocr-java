package com.togacure;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaly Alekseev
 * @since 20.02.2021
 */
public class PlayingCardImageSettings {

    public static int PREDICTION_BLOCK_WIDTH = 20;

    public static int PREDICTION_BLOCK_HEIGHT = 20;

    public static double PREDICTION_BACKGROUND_BLOCK_PERCENTAGE = 70;

    public static int SUITS_BLOCK_X_OFFSET = 3;

    public static int SUITS_BLOCK_Y_OFFSET = 28;

    public static int SUITS_BLOCK_WIDTH = 27;

    public static int SUITS_BLOCK_HEIGHT = 27;

    public static int HAND_BLOCK_X_OFFSET = 3;

    public static int HAND_BLOCK_Y_OFFSET = 3;

    public static int HAND_BLOCK_WIDTH = 30;

    public static int HAND_BLOCK_HEIGHT = 27;

    public static Set<Integer> CARD_BACKGROUND_COLORS = new HashSet<>();

    static {
        CARD_BACKGROUND_COLORS.add(Color.WHITE.getRGB());
        CARD_BACKGROUND_COLORS.add(new Color(0x78, 0x78, 0x78).getRGB());
    }

}
