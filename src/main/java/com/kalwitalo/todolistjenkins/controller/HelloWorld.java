package com.kalwitalo.todolistjenkins.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloWorld {

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("{message}")
    public String helloWorldWithMessage(@PathVariable String message) {
        return "Hello World " + message;
    }
}
