package org.abigballofmud.gmall.publisher.controller;

import java.util.List;
import java.util.Map;

import org.abigballofmud.gmall.publisher.service.PublisherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/23 3:09
 * @since 1.0
 */
@RestController
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("realtime-total")
    public List<Map<String, Object>> getRealtimeTotal(@RequestParam String date) {
        return publisherService.getRealtimeTotal(date);
    }

    @GetMapping("realtime-hour")
    public Map<String, Map<String, Long>> getRealtimeHour(@RequestParam String id, @RequestParam String date) {
        return publisherService.getRealtimeHour(id, date);
    }

}
