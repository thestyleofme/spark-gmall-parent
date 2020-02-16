package org.abigballofmud.gmall.logger.service;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/16 1:15
 * @since 1.0
 */
public interface LogService {

    /**
     * 记录日志并发送到kafka
     *
     * @param logString log json字符串
     */
    void doLog(String logString);
}
