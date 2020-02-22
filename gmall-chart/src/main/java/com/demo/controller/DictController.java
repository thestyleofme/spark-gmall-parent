package com.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/2/23 4:56
 * @since 1.0
 */
@RestController
public class DictController {

    @GetMapping("dict")
    public  String dict(HttpServletResponse response){
        response.addHeader("Last-Modified",new Date().toString());
        return "蓝瘦香菇";
    }
}
