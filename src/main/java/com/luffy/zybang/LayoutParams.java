package com.luffy.zybang;

/**
 * @author sunzhangfei
 * @since 2021/8/23 10:53 上午
 */
public class LayoutParams {
    public int height;
    public int width;
    public int gravity;
    public Margin margin = new Margin();

    public LayoutParams() {
    }

    public LayoutParams(int width, int height) {
        this.width = width;
        this.height = height;
    }


    public static class Gravity {

        public static final int CENTER = 0x1;
        public static final int LEFT = CENTER << 1;
        public static final int TOP = CENTER << 2;
        public static final int RIGHT = CENTER << 3;
        public static final int BOTTOM = CENTER << 4;
    }

    public static class Margin {
        public int left;
        public int top;
        public int right;
        public int bottom;

        public Margin() {
        }

        public Margin(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public Margin plus(Margin margin) {
            if (margin == null) {
                return this;
            }
            this.left += margin.left;
            this.top += margin.top;
            this.bottom += margin.bottom;
            this.right += margin.right;
            return this;
        }
    }
}