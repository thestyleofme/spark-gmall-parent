package org.abigballofmud.gmall.logger.controller;

import org.abigballofmud.gmall.logger.service.LogService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/15 22:47
 * @since 1.0
 */
@RestController
@RequestMapping("/log")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public String doLog(@RequestBody String logString) {
        logService.doLog(logString);
        return "success";
    }

}
