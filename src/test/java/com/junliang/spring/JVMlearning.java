package com.junliang.spring;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author junliang
 * @date 2018/05/17
 */
public class JVMlearning {

    /**
     * 校验时间戳
     *
     * @param validTime 有效时间
     * @param unit      {@code validTime} 的时间单位
     * @param timestamp 时间戳
     * @return 超过有效时间 false
     */
    private static boolean verifyTimestamp(long validTime, TimeUnit unit, long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long effective = unit.toSeconds(validTime);
        return diff <= effective;
    }


    @Test
    public void test() {
        int n = 10;//边长
        //int n = new Scanner(System.in).nextInt();
        for (int i = 1; i <= n; i++) {
            for (int k = 1; k <= n - i; k++) {
                System.out.print(" ");
                System.out.print(" ");
            }
            for (int j = 1; j <= 2 * i - 1; j++) {
                if (i == 1 || j == 2 * i - 1 || j == 1)
                    System.out.print("*");
                else
                    System.out.print(" ");
                System.out.print(" ");

                if (i == n && j == 2 * i - 1) {
                    System.out.println();
                    int a = i - 1;
                    for (int s = a; s >= 1; s--) {

                        for (int d = a - s; d >= 0; d--) {
                            System.out.print(" ");
                            System.out.print(" ");
                        }
                        for (int f = 2 * s - 1; f >= 1; f--) {
                            if (s == 1 || f == 2 * s - 1 || f == 1)
                                System.out.print("*");
                            else
                                System.out.print(" ");
                            System.out.print(" ");

                        }
                        System.out.println();
                    }
                }
            }
            System.out.println();//换行
        }
    }


    @Test
    public void test2() {
        int n = 2;//边长
        int hight = (n << 1) - 1;//总高度
        for (int i = 1; i <= hight; i++) {
            int padding = Math.abs(n - i);//求两侧边距
            for (int j = 1; j <= hight; j++) {
                if ((j - 1) == padding || (hight - j) == padding)
                    System.out.print("* ");
                else
                    System.out.print("  ");
            }
            System.out.println();//换行
        }
    }
}
