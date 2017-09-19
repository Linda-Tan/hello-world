package com.junliang.helloworld.restcontrol;

import com.junliang.helloworld.pojo.vo.ObjectRestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hello")
@RestController
public class TestController {


    @GetMapping("/world")
    public ObjectRestResponse index(){

        return new ObjectRestResponse("hellowzho直接后果orld");
    }

}
