package com.togacure;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Vitaly Alekseev
 * @since 25.02.2021
 */
public interface PredictionStrategy {

    Block getInitialBlock();

    Block getNextBlock(Block block);

    boolean isEnough(Block block);

    interface BlockRecognizer {

        void recognize(Block block);

    }

    interface RectangleDetector {

        Block takeRectangle(Block predicted);

    }

    class Block {

        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private double m;

        public Block(int x, int y, int width, int height) {
            this(x, y, width, height, 0);
        }

        public Block(int x, int y, int width, int height, double m) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.m = m;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setM(final double m) {
            this.m = m;
        }

        public double getM() {
            return m;
        }

        @Override
        public String toString() {
            return m != 0 ? String.format("[x: %s y: %s w: %s h: %s m: %s]", x, y, width, height, m) :
                    String.format("[x: %s y: %s w: %s h: %s]", x, y, width, height);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Block)) {
                return false;
            }
            final Block block = (Block) obj;
            return x == block.x && y == block.y && width == block.width && height == block.height;
        }
    }
}
