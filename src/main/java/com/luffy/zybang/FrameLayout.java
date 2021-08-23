package com.luffy.zybang;

import java.util.ArrayList;

/**
 * 帧布局
 *
 * @author sunzhangfei
 * @since 2021/8/20 1:01 下午
 */
class FrameLayout {
    private Size size;
    public LayoutParams layoutparams;
    private int id;
    public ArrayList<FrameLayout> children;

    public FrameLayout() {

    }

    public FrameLayout(Size size) {
        this.size = size;
    }

    public Size size() {
        return size;
    }

    public void addView(Container view, LayoutParams layoutparams) {
        if (children == null) {
            children = new ArrayList<FrameLayout>();
        }
        children.add(view);
        this.size = new Size(layoutparams.width, layoutparams.height);
        view.layoutparams = layoutparams;
    }

    public void setLayoutParams(LayoutParams layoutparams) {
        this.layoutparams = layoutparams;
    }

    public void setId(int viewId) {
        this.id = viewId;
    }

    public int getId() {
        return id;
    }

    public FrameLayout findViewById(int id) {
        if (this.id == id) {
            return this;
        }
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getId() == id) {
                return children.get(i);
            }
        }
        return null;
    }
}