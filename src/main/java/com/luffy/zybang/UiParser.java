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
        UiConfig.ViewModel room = uiConfig.room;
        if (room == null) {
            throw new InflateException("根布局为空");
        }
        Size screenSize = getScreen().size();
        Container roomContainer_temp = new Container();
        int width = 0;
        int height = 0;
        if (screenSize.width * room.height > screenSize.height * room.width) {
            //屏幕是狭长形，则高度撑满
            height = screenSize.height;
            width = screenSize.height * room.width / room.height;
        } else {
            //屏幕趋于正方形，则宽度撑满
            width = screenSize.width;
            height = screenSize.width * room.height / room.width;
        }
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

    private void parseChildView(Container parentView, List<UiConfig.ViewModel> children) {
        Collections.sort(children, new Comparator<UiConfig.ViewModel>() {
            @Override
            public int compare(UiConfig.ViewModel o1, UiConfig.ViewModel o2) {
                return o2.priority - o1.priority;
            }
        });

        for (int i = 0; i < children.size(); i++) {
            Container childContainer = new Container();
            UiConfig.ViewModel viewModel = children.get(i);
            LayoutParams lp = new LayoutParams();
            LayoutParams.Margin extraMargin = null;
            if (viewModel.layoutGravity != null) {
                lp.gravity = calGravity(viewModel.layoutGravity);
            } else {
                lp.gravity = LayoutParams.Gravity.LEFT;
                //... 走align判断位置，相当于有额外的margin值
                extraMargin = getExtraMargin(parentView, viewModel.align);
            }
            lp.margin = getMargin(parentView, viewModel.margin).plus(extraMargin);
            if (isSizeViliable(lp.width) && isSizeViliable(lp.height)) {

                break;
            }
            //计算出能计算的一条边
            cacViewSingleLength(parentView, viewModel, lp);
            //根据宽高比计算另一边
            if (!StringUtils.isEmpty(viewModel.ratio) && viewModel.ratio.matches("^\\d+:\\d+$")) {
                String[] split = viewModel.ratio.split(",");
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
            attachInfo(childContainer, viewModel);
            if (viewModel.children != null && viewModel.children.size() > 0) {
                parseChildView(childContainer, viewModel.children);
            }
        }
    }

    private void attachInfo(Container container, UiConfig.ViewModel viewModel) {
        switch (viewModel.name) {
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
        container.setId(viewModel.viewId);
    }

    /**
     * 先计算出能计算的一边长度
     *
     * @param parentView
     * @param viewModel
     * @param lp
     */
    private void cacViewSingleLength(Container parentView, UiConfig.ViewModel viewModel, LayoutParams lp) {
        if (viewModel.width == -1 || viewModel.height == -1) {//充满父布局
            if (viewModel.width == -1) {
                lp.width = parentView.layoutparams.width - lp.margin.left - lp.margin.right;
            }
            if (viewModel.height == -1) {
                lp.height = parentView.layoutparams.height - lp.margin.top - lp.margin.bottom;
            }
        }
        if (viewModel.width == -2 || viewModel.height == -2) {//父布局大小减去widthMinusId相关的子空间后的大小
            if (viewModel.width == -2) {
                List<Integer> widthMinusId = viewModel.widthMinusId;
                int widthNeedMinus = cacWidthSum(widthMinusId);
                lp.width = parentView.layoutparams.width - widthNeedMinus;
            }
            if (viewModel.height == -2) {
                List<Integer> widthMinusId = viewModel.heightMinusId;
                int heightNeedMinus = cacHeightSum(widthMinusId);
                lp.width = parentView.layoutparams.height - heightNeedMinus;
            }
        }
        if (viewModel.width > 0 || viewModel.height > 0) {
            if (viewModel.width > 0) {
                if (viewModel.layoutType == 2) {
                    lp.width = viewModel.width;
                }
            }
            if (viewModel.height > 0) {
                if (viewModel.layoutType == 2) {
                    lp.height = viewModel.height;
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
        if (width < -2) {//不能判断等于0,因为如果没值的时候默认解析出来的json是0
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