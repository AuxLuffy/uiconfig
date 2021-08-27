package com.luffy.percentlayout;


import java.util.List;

/**
 * 百分比配置化的
 *
 * @author sunzhangfei
 * @since 2021/8/27 5:11 下午
 */
public class PercentUiConfig {
    /**
     * 根布局
     */
    public ViewBean room;

    public static class ViewBean {
        public int viewId;
        /**
         * 此view的描述
         */
        public String description;
        /**
         * 圆角，蓝湖上的标注，android单位是dp，ios是pt
         */
        public float corner;
        /**
         * 布局优先级，也可以叫做zIndex
         */
        public int priority;
        /**
         * 0表示按父布局百分比布局，1表示按宽高比例布局，此参数用来计算视图宽高{@link #width} and {@link #height}
         */
        public int layoutType;
        public double width;
        public double height;
        /**
         * 相对于父布局百分比的左上顶点的x坐标（0.0-1.0）
         */
        public double x;
        /**
         * 相对于父布局百分比的左上顶点的y坐标（0.0-1.0）
         */
        public double y;
        /**
         * 子view
         */
        public List<ViewBean> children;
        public Background background;
        /**
         * 当layoutType为2时需要此参数表示宽高比
         */
        String ratio;
    }

    public static class Background {
        public String type;
        public String value;
    }
}