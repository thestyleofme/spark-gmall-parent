package org.abigballofmud.gmall.publisher.service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/23 2:51
 * @since 1.0
 */
public interface PublisherService {

    /**
     * 根据日期获取数量
     *
     * @param date 日期，如2020-02-22
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    List<Map<String, Object>> getRealtimeTotal(String date);

    /**
     * 根据日期获取时间以及总数的分布
     *
     * @param id   类型，如dau
     * @param date 日期，如2020-02-22
     * @return java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.Long>>
     */
    Map<String, Map<String, Long>> getRealtimeHour(String id, String date);
}
