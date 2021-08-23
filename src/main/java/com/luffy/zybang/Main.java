package com.luffy.zybang;

/**
 * @author sunzhangfei
 * @since 2021/8/23 4:31 下午
 */
public class Main {
    public static void main(String[] args) {
        regexpTest("111111:3");
    }

    private static boolean regexpTest(String s) {
        String regex = "^\\d+:\\d+$";
        boolean match = s.matches(regex);
        System.out.println(s + " :正则匹配: " + regex + "\r\n结果: " + match);
        return match;
    }
}