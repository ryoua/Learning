package com.ryoua.controller;

import com.ryoua.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: RyouA
 * @Date: 2020/4/26 - 1:04 下午
 **/
@RestController
public class ConsumerController {
    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "/ribbon-consumer", method = RequestMethod.GET)
    public String helloConsumer() {
        return helloService.helloService();
    }
}



