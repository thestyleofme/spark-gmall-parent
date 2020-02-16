package org.abigballofmud.gmall.mock;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.gmall.mock.utils.RestTemplateUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/11 0:03
 * @since 1.0
 */
@Slf4j
public class LogUploader {

    private LogUploader() {
        throw new IllegalStateException("util class");
    }

    public static void sendLog(String logContent) {
        RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(logContent,
                RestTemplateUtil.applicationJsonHeaders());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                // "http://127.0.0.1:38080/log", requestEntity, String.class);
                // 这里nginx做了反向代理 监听本机8000端口
                "http://127.0.1.0:8000/log", requestEntity, String.class);
        log.debug("upload log, status: {}, response: {}", responseEntity.getStatusCode(), requestEntity.getBody());
    }
}
