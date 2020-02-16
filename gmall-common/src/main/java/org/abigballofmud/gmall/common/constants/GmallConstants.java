package org.abigballofmud.gmall.common.constants;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/16 1:07
 * @since 1.0
 */
public interface GmallConstants {

    String KAFKA_MESSAGE_TYPE = "type";
    String KAFKA_MESSAGE_TYPE_STARTUP = "startup";
    String KAFKA_TOPIC_STARTUP = "test_gmall_start";
    String KAFKA_MESSAGE_TYPE_EVENT = "event";
    String KAFKA_TOPIC_EVENT = "test_gmall_event";
}
