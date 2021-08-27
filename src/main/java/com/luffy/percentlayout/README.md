# 百分比布局方案说明
json文件说明

单个view结构字段说明
```json
    {
        "viewId": 10002,     // 组件的唯一标识
        "description": "course", // 描述
        "corner": 10,      // 圆角 单位pt
        "priority": 0,     // 布局优先级，越小越有先布局
        "layoutType": 0,   // 布局方式 0、按占父view的比例计算 1、按照宽高比计算，即ratio值
        "width": 0.825,    // 宽度占父view宽度的比例 即 真实的宽度 = 父view的宽度 * width
        "height": 1,       // 宽度占父view高度的比例 即 真实的高度 = 父view的高度 * height
        "x": 0.02,         // 距离父view左边的距离比例 即 实际的x = 父view的宽度 * x
        "y": 0.02,         // 距离父view顶边的距离比例 即 实际的y = 父view的高度 * y
        "ratio": "16:9",   // 宽高比  即 layoutType = 1的时候 根据ratio去计算宽高
        "background": {
            "type": "color||pic",      // 背景类型 1、color为颜色，此时value为 #3A2DB7,0.5，后面代表透明度  2、pic为图片，value为图片名
            "value": "#3A2DB7,0.5||img/roomBg.png"
        }
    }
```
说明：
viewId的枚举类型如下:

    10001: room容器
    10002: 课件容器
    10003: 功能区域
    10004: 自己头像区
    10005: 组员父容器
    11000: 单个组员组件区
    10006; 辅导老师区
    10007; 老师头像区
    10008; 聊天区

