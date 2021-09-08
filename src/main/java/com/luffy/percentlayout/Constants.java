package com.luffy.percentlayout;

/**
 * 常量
 *
 * @author sunzhangfei
 * @since 2021/8/27 5:36 下午
 */
public class Constants {
    public static interface ContainerId {
        /**
         * ROOM容器
         */
        public static final int ROOM = 10001;
        /**
         * 课件区父容器，提供给在课件域的业务用，课件和课件区视频流容器的父容器
         */
        public static final int COURSE = 10002;
        /**
         * 功能区
         */
        public static final int FUNC = 10003;
        /**
         * 自己头像区
         */
        public static final int MY_AVATAR = 10004;
        /**
         * 组员区父容器
         */
        public static final int OTHER_STU = 10005;
        /**
         * 单个组员区
         */
        public static final int STU_ITEM = 11000;
        /**
         * 辅导老师区
         */
        public static final int ASSIST_TEACHER = 10006;
        /**
         * 聊天列表区
         */
        public static final int CHAT_LIST = 10008;
        /**
         * 老师头像区
         */
        public static final int TEACHER_AVATAR = 10007;
        /**
         * 组标题容器
         */
        public static final int GROUP_TITLE = 10009;
        /**
         * 聊天按钮容器
         */
        public static final int CHAT_BTN = 10010;
        /**
         * 课件容器，提供给课件服务用的
         */
        public static final int PPT = 10011;
        /**
         * 播放器容器，给课件区内视频流用的
         */
        public static final int VIDEO_COMPONENT = 10012;
    }
}