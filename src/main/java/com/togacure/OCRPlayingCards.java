package com.togacure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.togacure.PlayingCardImageSettings.HANDS_IMAGE_HEIGHT;
import static com.togacure.PlayingCardImageSettings.HANDS_IMAGE_WIDTH;
import static com.togacure.PlayingCardImageSettings.HANDS_WEIGHTS_RESOURCE;
import static com.togacure.PlayingCardImageSettings.SUITS_IMAGE_HEIGHT;
import static com.togacure.PlayingCardImageSettings.SUITS_IMAGE_WIDTH;
import static com.togacure.PlayingCardImageSettings.SUITS_WEIGHTS_RESOURCE;
import static com.togacure.Utils.getData;
import static com.togacure.Utils.getInputFolder;

/**
 * @author Vitaly Alekseev
 * @since 04.03.2021
 */
public class OCRPlayingCards {

    private static final Map<String, String> suitsNameMap = Map.of("club", "c",
            "diamond", "d",
            "heart", "h",
            "spade", "s");

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException(String.format("one argument (input folder) is expected but have/has been received %s", Arrays.toString(args)));
        }
        final Path input = getInputFolder(args[0]);

        final Perceptron suitsPerceptron = PerceptronFactory.loadPerceptron(SUITS_IMAGE_WIDTH * SUITS_IMAGE_HEIGHT, OCRPlayingCards.class.getClassLoader().getResourceAsStream(SUITS_WEIGHTS_RESOURCE));
        final Perceptron handsPerceptron = PerceptronFactory.loadPerceptron(HANDS_IMAGE_WIDTH * HANDS_IMAGE_HEIGHT, OCRPlayingCards.class.getClassLoader().getResourceAsStream(HANDS_WEIGHTS_RESOURCE));

        final long startTime = System.currentTimeMillis();

        final List<File> images = Arrays.stream(Objects.requireNonNull(input.toFile().listFiles()))
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith("png"))
                .collect(Collectors.toList());

        images.forEach(file -> recognize(file, suitsPerceptron, handsPerceptron));


        System.out.format("\n\nDone. %s seconds. Total files: %s\n", (System.currentTimeMillis() - startTime) / 1000, images.size());
    }

    private static void recognize(final File file, final Perceptron suitsPerceptron, final Perceptron handsPerceptron) {
        try {
            final BufferedImage img = CardsDetectorFactory.getImage(file);
            final CardsDetector detector = CardsDetectorFactory.getCardsDetector(img);
            final List<Block> suits = detector.getSuits();
            final List<Block> hands = detector.getHands();
            if (suits.size() != hands.size()) {
                throw new IllegalStateException(String.format("File: %s suits size: %s isn't equal to hands size: %s", file, suits.size(), hands.size()));
            }
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < suits.size(); i++) {
                final Block suit = suits.get(i);
                final Block hand = hands.get(i);
                result.append(handsPerceptron.test(getData(img, hand, hand.getBackground())));
                result.append(suitsNameMap.get(suitsPerceptron.test(getData(img, suit, suit.getBackground()))));
            }
            System.out.format("%s: %s\n", file.getName(), result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
