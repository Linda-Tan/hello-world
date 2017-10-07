package com.junliang.spring.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("game")
public class GameController {

    @GetMapping("login")
    public String longin(){

        return "login";
    }

}
