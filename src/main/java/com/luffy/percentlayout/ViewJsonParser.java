package com.luffy.percentlayout;

import com.google.gson.Gson;
import com.luffy.common.*;
import com.luffy.api.ContainerService;
import com.luffy.common.util.ScreenUtil;
import com.luffy.zybang.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.luffy.percentlayout.Constants.ContainerId.*;

/**
 * 百分比布局的json解析器
 *
 * @author sunzhangfei
 * @since 2021/8/27 5:03 下午
 */
public class ViewJsonParser implements ContainerService {
    String jsonSrc;
    PercentUiConfig uiConfig;

    private Container funContainer;
    private Container roomContainer;
    private Container pptContainer;
    private Container teacherAvatarContainer;
    private Container myAvatarContainer;
    private Container otherStudentsContainer;
    private Container assistTeacherContainer;
    private Container chatContainer;
    private Container interactContainer;
    public List<Container> otherStus = new ArrayList<>();

    public ViewJsonParser(String jsonSrc, Container rootview) {
        this.jsonSrc = jsonSrc;
        parseJson(jsonSrc);
        parseUiConfig(rootview);
    }

    private void parseJson(String json) throws InflateException {
        Gson gson = new Gson();
        uiConfig = gson.fromJson(json, PercentUiConfig.class);
    }

    /**
     * 将配置转化为各个容器，需要在方法参数加一个根布局
     */
    public void parseUiConfig(Container rootView) {
        PercentUiConfig.ViewBean room = uiConfig.room;
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
            width = (int) (screenSize.height * room.width / room.height);
        } else {
            //屏幕趋于正方形，则宽度撑满
            width = screenSize.width;
            height = (int) (screenSize.width * room.height / room.width);
        }
        LayoutParams lp = new LayoutParams(width, height);
        roomContainer_temp.layoutparams = lp;
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

    private void parseChildView(Container parentView, List<PercentUiConfig.ViewBean> children) {
        Collections.sort(children, new Comparator<PercentUiConfig.ViewBean>() {
            @Override
            public int compare(PercentUiConfig.ViewBean o1, PercentUiConfig.ViewBean o2) {
                return o2.priority - o1.priority;
            }
        });

        for (int i = 0; i < children.size(); i++) {
            Container childContainer = new Container();
            PercentUiConfig.ViewBean viewModel = children.get(i);
            LayoutParams lp = new LayoutParams();
            //根据x,y计算margin值
            lp.margin = getMargin(parentView, viewModel);
            if (isSizeViliable(lp.width) && isSizeViliable(lp.height)) {
                break;
            }
            //根据宽高计算view宽高
            //计算出能计算的一条边
            cacViewSingleLength(parentView, viewModel, lp);

            parentView.addView(childContainer, lp);
            attachInfo(childContainer, viewModel);
            if (viewModel.children != null && viewModel.children.size() > 0) {
                parseChildView(childContainer, viewModel.children);
            }
        }
    }

    private void attachInfo(Container container, PercentUiConfig.ViewBean viewModel) {
        switch (viewModel.viewId) {
            case ROOM:
                roomContainer = container;
                break;
            case COURSE:
                pptContainer = container;
                break;
            case FUNC:
                funContainer = container;
                break;
            case MY_AVATAR:
                myAvatarContainer = container;
                break;
            case OTHER_STU:
                otherStudentsContainer = container;
                break;
            case ASSIST_TEACHER:
                assistTeacherContainer = container;
                break;
            case CHAT:
                chatContainer = container;
                break;
            case TEACHER_AVATAR:
                teacherAvatarContainer = container;
                break;
            case STU_ITEM:
                otherStus.add(container);
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
    private void cacViewSingleLength(Container parentView, PercentUiConfig.ViewBean viewModel, LayoutParams lp) {
        LayoutParams layoutparams = parentView.layoutparams;
        lp.width = (int) (viewModel.width * layoutparams.width);
        lp.height = (int) (viewModel.height * layoutparams.height);
    }


    private Container findViewById(int id) {
        Container container = null;
        //根据id查container
        //....
        container = (Container) roomContainer.findViewById(id);
        return container;
    }

    private boolean isSizeViliable(int width) {
        if (width < 0) {//不能判断等于0,因为如果没值的时候默认解析出来的json是0
            return false;
        }
        return true;
    }

    private LayoutParams.Margin getMargin(Container parentView, PercentUiConfig.ViewBean view) {
        LayoutParams.Margin margin = new LayoutParams.Margin();
        LayoutParams lp = parentView.layoutparams;
        margin.left = (int) (lp.width * view.x);
        margin.right = 0;//相当于右边margin为0
        margin.top = (int) (lp.height * view.y);
        margin.bottom = 0;//相当于下边margin为0
        return margin;
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

    @Override
    public List<Container> getOtherStusContainerList() {
        return otherStus;
    }
}