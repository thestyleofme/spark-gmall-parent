package org.abigballofmud.gmall.mock.utils;

import java.util.Random;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/10 23:02
 * @since 1.0
 */
public class RandomNumber {

    private static Random random = new Random();

    public static int getRandomInt(int fromNum, int toNum) {
        return fromNum + random.nextInt(toNum - fromNum);
    }

}
