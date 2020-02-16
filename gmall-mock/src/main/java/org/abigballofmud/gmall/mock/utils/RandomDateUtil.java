package org.abigballofmud.gmall.mock.utils;

import java.time.*;
import java.util.Random;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/10 2:50
 * @since 1.0
 */
public class RandomDateUtil {

    private Long logDateTime;
    private int maxTimeStep;

    public RandomDateUtil(LocalDateTime startDate, LocalDateTime endDate, int num) {
        long avgStepTime = Duration.between(endDate, startDate).toMillis() / num;
        this.maxTimeStep = (int) avgStepTime * 2;
        this.logDateTime = startDate.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public LocalDateTime getRandomDate() {
        int timeStep = new Random().nextInt(maxTimeStep);
        this.logDateTime = this.logDateTime + timeStep;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.logDateTime), ZoneId.systemDefault());
    }

}
