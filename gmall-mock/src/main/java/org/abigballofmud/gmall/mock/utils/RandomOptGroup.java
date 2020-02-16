package org.abigballofmud.gmall.mock.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/10 23:16
 * @since 1.0
 */
public class RandomOptGroup<T> {

    private Random random = new Random();

    private int totalWeight = 0;
    private List<RandomOpt<T>> optList = new ArrayList<>();

    @SafeVarargs
    public RandomOptGroup(RandomOpt<T>... opts) {
        for (RandomOpt<T> opt : opts) {
            totalWeight += opt.getWeight();
            for (int i = 0; i < opt.getWeight(); i++) {
                optList.add(opt);
            }
        }
    }

    public RandomOpt<T> getRandomOpt() {
        return optList.get(random.nextInt(totalWeight));
    }

    public static void main(String[] args) {
        ArrayList<RandomOpt<String>> arrayList = new ArrayList<>(3);
        arrayList.add(new RandomOpt<>("zhangsan", 10));
        arrayList.add(new RandomOpt<>("lisi", 5));
        arrayList.add(new RandomOpt<>("wangwu", 40));
        @SuppressWarnings("unchecked")
        RandomOpt<String>[] opts = new RandomOpt[arrayList.size()];
        opts = arrayList.toArray(opts);
        RandomOptGroup<String> randomOptGroup = new RandomOptGroup<>(opts);
        for (int i = 0; i < 10; i++) {
            System.out.println(randomOptGroup.getRandomOpt());
        }
    }

}
