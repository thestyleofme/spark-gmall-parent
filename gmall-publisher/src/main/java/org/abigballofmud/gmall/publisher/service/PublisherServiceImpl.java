package org.abigballofmud.gmall.publisher.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.abigballofmud.gmall.common.constants.GmallConstants;
import org.abigballofmud.gmall.publisher.mapper.DauMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/23 2:51
 * @since 1.0
 */
@Service
public class PublisherServiceImpl implements PublisherService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DauMapper dauMapper;

    public PublisherServiceImpl(DauMapper dauMapper) {
        this.dauMapper = dauMapper;
    }

    @Override
    public List<Map<String, Object>> getRealtimeTotal(String date) {
        List<Map<String, Object>> list = new ArrayList<>();
        // 新增日活
        HashMap<String, Object> map = new HashMap<>(8);
        map.put("id", "dau");
        map.put("name", "新增日活");
        map.put("value", dauMapper.getDauTotal(date));
        list.add(map);
        // 新增设备
        map = new HashMap<>(8);
        map.put("id", "new_mid");
        map.put("name", "新增设备");
        map.put("value", 100);
        list.add(map);
        return list;
    }

    @Override
    public Map<String, Map<String, Long>> getRealtimeHour(String id, String date) {
        Map<String, Map<String, Long>> resultMap = new HashMap<>(4);
        if (GmallConstants.DAU_TYPE.equalsIgnoreCase(id)) {
            // 今天
            Map<String, Long> todayMap = dauMapper.getDauHourCount(date).stream().collect(
                    Collectors.toMap(
                            map -> String.valueOf(map.get("LOGHOUR")),
                            map -> (Long) map.get("CNT"))
            );
            resultMap.put("today", todayMap);
            // 昨天
            Map<String, Long> yesterdayMap = dauMapper.getDauHourCount(
                    LocalDate.parse(date, DATE_TIME_FORMATTER).minusDays(1L).format(DATE_TIME_FORMATTER)
            ).stream().collect(
                    Collectors.toMap(
                            map -> String.valueOf(map.get("LOGHOUR")),
                            map -> (Long) map.get("CNT"))
            );
            resultMap.put("yesterday", yesterdayMap);
        } else {
            throw new IllegalArgumentException("invalid id");
        }
        return resultMap;
    }

}
