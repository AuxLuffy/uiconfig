package com.luffy.api;

import com.luffy.common.Container;

import java.util.List;

/**
 * @author sunzhangfei
 * @since 2021/8/20 12:59 下午
 */
public interface ContainerService {
    /**
     * 获取房间区容器
     *
     * @return
     */
    Container getRoomContainer();

    /**
     * 课件容器
     *
     * @return
     */
    Container getPPtContainer();

    /**
     * 老师头像容器
     *
     * @return
     */
    Container getTeacherAvatarContainer();

    /**
     * 自己头像容器
     *
     * @return
     */
    Container getMyAvatarContainer();

    /**
     * 组同学容器
     *
     * @return
     */
    Container getOtherStudentsContainer();

    /**
     * 辅导老师容器
     *
     * @return
     */
    Container getAssistTeacherContainer();

    /**
     * 聊天窗口容器
     *
     * @return
     */
    Container getChatContainer();

    /**
     * 通用互动容器
     *
     * @return
     */
    Container getInteractContainer();

    /**
     * 获取功能区容器
     *
     * @return
     */
    Container getFucContainer();

    /**
     * 获取其他学生的子容器的列表
     * @return
     */
    List<Container> getOtherStusContainerList();

}
