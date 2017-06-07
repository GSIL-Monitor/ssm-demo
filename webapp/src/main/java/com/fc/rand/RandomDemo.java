package com.fc.rand;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by fangcong on 2017/4/7.
 */
public class RandomDemo {

    private static final Random RANDOM = new Random();

    private static final Map<Integer, String> appMap = new HashMap<>();

    static {
        appMap.put(999, "lottey1");
        appMap.put(1010, "lottey2");
        appMap.put(1050, "lottey3");
    }

    /**
     * 控制随机范围的概率
     */
    public static int randChance(int r1, int total) {
        int exp = RANDOM.nextInt(total) + 1;
        if (r1 >= exp) {
            return randRange1(90, 100);
        }
        return randRange1(0, 80);
    }

    /**
     * 生成范围内随机数
     * @return
     */
    public static int randRange1(int start, int end) {
        if (end == 0 || start > end) {
            return 0;
        }
        if (start == 0) {
            return RANDOM.nextInt(end);
        }
        if (start == end) {
            return start;
        }
        return RANDOM.nextInt(end - start) + start;
    }

    /**
     * 测试，1/100概率生成90-100随机数
     * @param args
     */
    public static void main(String[] args) {
        /*int count = 0;
        for (int i = 1;i <= 1000; i++) {
            int exp = randChance(1, 100);
            if (exp >= 90 && exp <= 100) {
                count++;
            }
            System.out.print(exp + "\t");
            if (i % 40 == 0) {
                System.out.println();
            }
        }
        System.out.println(count);*/
        Double[] score = {99.9, 101.0, 105.0};
        Double result = score[RANDOM.nextInt(score.length)];
        System.out.println(result);

        String app = appMap.get((int) (result * 10));
        System.out.println(app);
    }


}
