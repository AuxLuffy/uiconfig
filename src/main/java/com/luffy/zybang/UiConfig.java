package com.luffy.zybang;

import sun.jvm.hotspot.memory.Space;

import java.util.List;

/**
 * ui配置类
 * 所有可配置化的容器都是帧布局
 *
 * @author sunzhangfei
 * @since 2021/8/19 7:30 下午
 */
public class UiConfig {
    /**
     * 直播间内所有间隙大小
     */
    public Space space;
    /**
     * 直播间可用区域根布局
     */
    public Room room;

    public static class Room extends View {
        public int roomFrameType;
    }

    public static class SpaceCount {
        public int wNum;
        public int hNum;
    }

    public static class Background {
        public String type;
        public String value;
    }

    public static class Corner {
        public int radius;
        public String relative;
    }

    public static class Margin {
        public String relative;
        public double top;
        public double bottom;
        public double right;
        public double left;
    }

    public static class Align {
        int toId;
        String type;
    }

    public static class View {
        public SpaceCount spaceCount;
        public String name;
        public int viewPriority;
        public int viewId;
        public int viewType;
        public String layout_gravity;
        public int layoutType;
        public int width;
        public int height;
        public String ratio;
        public List<Integer> widthMinusId;
        public List<Integer> heightMinusId;
        public List<View> children;
        public Margin margin;
        public Corner corner;
        public Background background;
        public Align align = new Align();
    }

    public static class Space {
        public Double width;
        public String relative;
    }

    public interface ContainerId{

    }

}