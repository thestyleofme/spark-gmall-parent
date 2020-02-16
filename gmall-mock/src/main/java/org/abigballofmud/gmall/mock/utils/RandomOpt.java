package org.abigballofmud.gmall.mock.utils;

import lombok.Data;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/10 23:06
 * @since 1.0
 */
@Data
public class RandomOpt<T> {

    private T value;
    private int weight;

    public RandomOpt(T value, int weight) {
        this.value = value;
        this.weight = weight;
    }

}
