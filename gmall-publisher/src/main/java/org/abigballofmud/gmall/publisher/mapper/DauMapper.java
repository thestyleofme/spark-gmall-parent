package org.abigballofmud.gmall.publisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/23 1:52
 * @since 1.0
 */
public interface DauMapper {

    /**
     * 根据日期获取每日活跃用户数量
     *
     * @param date 日期，如2020-02-22
     * @return java.lang.Long 总数
     */
    Long getDauTotal(String date);

    /**
     * 根据日期获取每日活跃用户的时间以及总数的分布
     *
     * @param date 日期，如2020-02-22
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    List<Map<String, Object>> getDauHourCount(String date);
}
