package com.ryoua.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: RyouA
 * @Date: 2020/4/19 - 9:00 下午
 **/
@RestController
public class ConsumerController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/ribbon-consumer", method = RequestMethod.GET)
    public String helloConsumer() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
    }
}
