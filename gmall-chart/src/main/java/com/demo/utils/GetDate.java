package com.demo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/2/23 4:52
 * @since 1.0
 */
public class GetDate {

    private GetDate() {
        throw new IllegalStateException("util class");
    }

    public static String getSysDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
