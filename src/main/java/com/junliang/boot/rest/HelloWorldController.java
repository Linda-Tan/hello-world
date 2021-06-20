package com.junliang.boot.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class HelloWorldController {

    @RequestMapping("/")
    String home() {
        log.trace("qweqw");
        log.info("qweqw");
        log.debug("qweqw");
        log.warn("qweqw");
        log.error("qweqw");

        return "Hello World!";
    }
}
