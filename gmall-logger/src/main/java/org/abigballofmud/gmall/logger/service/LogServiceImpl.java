package org.abigballofmud.gmall.logger.service;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.gmall.common.constants.GmallConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/16 1:15
 * @since 1.0
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public LogServiceImpl(ObjectMapper objectMapper,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void doLog(String logString) {
        try {
            // 对log加时间戳
            Map map = objectMapper.readValue(logString, Map.class);
            map.put("ts", System.currentTimeMillis());
            // 写日志 用于离线采集
            String logJson = objectMapper.writeValueAsString(map);
            log.info(logJson);
            // 发送kafka
            if (String.valueOf(map.get(GmallConstants.KAFKA_MESSAGE_TYPE)).equalsIgnoreCase(GmallConstants.KAFKA_MESSAGE_TYPE_EVENT)) {
                kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_EVENT, logJson);
            } else if (String.valueOf(map.get(GmallConstants.KAFKA_MESSAGE_TYPE)).equalsIgnoreCase(GmallConstants.KAFKA_MESSAGE_TYPE_STARTUP)) {
                kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_STARTUP, logJson);
            } else {
                log.error("kafka message type invalid");
            }
        } catch (JsonProcessingException e) {
            log.error("logString is a valid json", e);
        }
    }
}
