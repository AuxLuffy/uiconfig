package com.luffy.zybang;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 解析类
 *
 * @author sunzhangfei
 * @since 2021/8/20 12:37 下午
 */
public class UiParser implements ContainerService {
    String jsonSrc;
    UiConfig uiConfig;

    private Container funContainer;
    private Container roomContainer;
    private Container pptContainer;
    private Container teacherAvatarContainer;
    private Container myAvatarContainer;
    private Container otherStudentsContainer;
    private Container assistTeacherContainer;
    private Container chatContainer;
    private Container interactContainer;

    public int spaceWidth;

    public UiParser(String jsonSrc) {
        this.jsonSrc = jsonSrc;
        parseJson(jsonSrc);
    }

    private void parseJson(String json) throws InflateException {
        Gson gson = new Gson();
        uiConfig = gson.fromJson(json, UiConfig.class);
//        parseUiConfig();
    }

    /**
     * 将配置转化为各个容器，需要在方法参数加一个根布局
     */
    public void parseUiConfig(Container rootView) {
        UiConfig.Room room = uiConfig.room;
        if (room == null) {
            throw new InflateException("根布局为空");
        }
        Size screenSize = getScreen().size();
        Container roomContainer_temp = new Container();
        int width = 0;
        int height = 0;
        LayoutParams lp = new LayoutParams(width, height);
        roomContainer_temp.layoutparams = lp;
        spaceWidth = cacSpaceWidth(roomContainer_temp, uiConfig.space);
        lp.gravity = LayoutParams.Gravity.CENTER;
        attachInfo(roomContainer_temp, room);
        roomContainer = roomContainer_temp;
        rootView.addView(roomContainer, lp);
        parseChildView(roomContainer_temp, uiConfig.room.children);
    }

    /**
     * 计算留白空隙
     *
     * @param roomContainer_temp
     * @param space
     * @return
     */
    private int cacSpaceWidth(Container roomContainer_temp, UiConfig.Space space) {
        int width = 0;
        if (space.relative.equals("roomWidth")) {
            return (int) (roomContainer_temp.layoutparams.width * space.width);
        } else {
            return width;
        }

    }

    private void parseChildView(Container parentView, List<UiConfig.View> children) {
        Collections.sort(children, new Comparator<UiConfig.View>() {
            @Override
            public int compare(UiConfig.View o1, UiConfig.View o2) {
                return o2.viewPriority - o1.viewPriority;
            }
        });

        for (int i = 0; i < children.size(); i++) {
            Container childContainer = new Container();
            UiConfig.View view = children.get(i);
            LayoutParams lp = new LayoutParams();
            LayoutParams.Margin extraMargin = null;
            if (view.layout_gravity != null) {
                lp.gravity = calGravity(view.layout_gravity);

            } else {
                lp.gravity = LayoutParams.Gravity.LEFT;
                //... 走align判断位置，相当于有额外的margin值
                extraMargin = getExtraMargin(parentView, view.align);
            }
            lp.margin = getMargin(parentView, view.margin).plus(extraMargin);
            if (isSizeViliable(lp.width) && isSizeViliable(lp.height)) {

                break;
            }
            //计算其中一条边
            cacViewSingleLength(parentView, view, lp);
            if (!StringUtils.isEmpty(view.ratio) && view.ratio.matches("\\d?:\\d?")) {
                String[] split = view.ratio.split(",");
                int w = Integer.parseInt(split[0]);
                int h = Integer.parseInt(split[1]);
                if (lp.width > 0 && lp.height == 0) {//width已经计算出来了
                    lp.height = lp.width * h / w;
                }
                if (lp.height > 0 && lp.width == 0) {//height已经计算出来了
                    lp.width = lp.height * w / h;
                }
            }

            parentView.addView(childContainer, lp);
            attachInfo(childContainer, view);
            if (view.children != null && view.children.size() > 0) {
                parseChildView(childContainer, view.children);
            }
        }
    }

    private void attachInfo(Container container, UiConfig.View view) {
        switch (view.name) {
            case "room":
                roomContainer = container;
                break;
            case "course":
                pptContainer = container;
                break;
            case "fun":
                funContainer = container;
                break;
            case "myAvatar":
                myAvatarContainer = container;
                break;
            case "otherStu":
                otherStudentsContainer = container;
                break;
            case "assistTeacher":
                assistTeacherContainer = container;
                break;
            case "chat":
                chatContainer = container;
                break;
            case "teacher":
                teacherAvatarContainer = container;
                break;
            case "interact":
                interactContainer = container;
                break;
            default:
                break;
        }
        container.setId(view.viewId);
    }


    private void cacViewSingleLength(Container parentView, UiConfig.View view, LayoutParams lp) {
        if (view.width == -1 || view.height == -1) {//充满父布局
            if (view.width == -1) {
                lp.width = parentView.layoutparams.width - lp.margin.left - lp.margin.right;
            }
            if (view.height == -1) {
                lp.height = parentView.layoutparams.height - lp.margin.top - lp.margin.bottom;
            }
        }
        if (view.width == -2 || view.height == -2) {//父布局大小减去widthMinusId相关的子空间后的大小
            if (view.width == -2) {
                List<Integer> widthMinusId = view.widthMinusId;
                int widthNeedMinus = cacWidthSum(widthMinusId);
                lp.width = parentView.layoutparams.width - widthNeedMinus;
            }
            if (view.height == -2) {
                List<Integer> widthMinusId = view.heightMinusId;
                int heightNeedMinus = cacHeightSum(widthMinusId);
                lp.width = parentView.layoutparams.height - heightNeedMinus;
            }
        }
        if (view.width > 0 || view.height > 0) {
            if (view.width > 0) {
                if (view.layoutType == 2) {
                    lp.width = view.width;
                }
            }
            if (view.height > 0) {
                if (view.layoutType == 2) {
                    lp.height = view.height;
                }
            }
        }
    }

    private int cacWidthSum(List<Integer> widthMinusId) {
        int sum = 0;
        for (int i = 0; i < widthMinusId.size(); i++) {
            Container container = findViewById(widthMinusId.get(i));
            if (container != null) {
                sum = container.layoutparams.width + sum + sum + container.layoutparams.margin.left + sum + container.layoutparams.margin.right;
            }
        }
        return sum;
    }

    private int cacHeightSum(List<Integer> widthMinusId) {
        int sum = 0;
        for (int i = 0; i < widthMinusId.size(); i++) {
            Container container = findViewById(widthMinusId.get(i));
            if (container != null) {
                sum = container.layoutparams.height + sum + container.layoutparams.margin.top + sum + container.layoutparams.margin.bottom;
            }
        }
        return sum;
    }

    private Container findViewById(int id) {
        Container container = null;
        //根据id查container
        //....
        container = (Container) roomContainer.findViewById(id);
        return container;
    }

    private boolean isSizeViliable(int width) {
        if (width < -2 || width == 0) {
            return false;
        }
        return true;
    }

    private LayoutParams.Margin getMargin(Container parentView, UiConfig.Margin margin) {
        return cacMargin(margin);
    }

    /**
     * 根据当前view内已添加的view来确定一个margin值
     *
     * @param parentView
     * @param align
     * @return
     */
    private LayoutParams.Margin getExtraMargin(Container parentView, UiConfig.Align align) {
        UiConfig.Margin extraMargin = new UiConfig.Margin();
        //... 根据父view和align求margin

        return cacMargin(extraMargin);
    }

    private LayoutParams.Margin cacMargin(UiConfig.Margin extraMargin) {
        LayoutParams.Margin margin = new LayoutParams.Margin();
        if (extraMargin.relative.equals("space")) {
            margin.left = (int) (extraMargin.left * spaceWidth);
            margin.right = (int) (extraMargin.right * spaceWidth);
            margin.top = (int) (extraMargin.top * spaceWidth);
            margin.bottom = (int) (extraMargin.bottom * spaceWidth);
        }
        return margin;
    }

    private int calGravity(String gravity) {
        int temp = 0x0;
        String[] split = gravity.split("|");
        for (String g : split) {
            switch (g) {
                case "center":
                    temp = temp | LayoutParams.Gravity.CENTER;
                    break;
                case "left":
                    temp = temp | LayoutParams.Gravity.LEFT;
                    break;
                case "top":
                    temp = temp | LayoutParams.Gravity.TOP;
                    break;
                case "right":
                    temp = temp | LayoutParams.Gravity.RIGHT;
                    break;
                case "bottom":
                    temp = temp | LayoutParams.Gravity.BOTTOM;
                    break;
                default:
                    break;
            }
        }
        return temp;
    }

    /**
     * 获取根布局
     *
     * @return
     */
    private FrameLayout getScreen() {
        //对外部依赖
        return new FrameLayout(ScreenUtil.getScreenSize());
    }

    @Override
    public Container getRoomContainer() {
        return roomContainer;
    }

    @Override
    public Container getPPtContainer() {
        return pptContainer;
    }

    @Override
    public Container getTeacherAvatarContainer() {
        return teacherAvatarContainer;
    }

    @Override
    public Container getMyAvatarContainer() {
        return myAvatarContainer;
    }

    @Override
    public Container getOtherStudentsContainer() {
        return otherStudentsContainer;
    }

    @Override
    public Container getAssistTeacherContainer() {
        return assistTeacherContainer;
    }

    @Override
    public Container getChatContainer() {
        return chatContainer;
    }

    @Override
    public Container getInteractContainer() {
        return interactContainer;
    }

    @Override
    public Container getFucContainer() {
        return funContainer;
    }
}