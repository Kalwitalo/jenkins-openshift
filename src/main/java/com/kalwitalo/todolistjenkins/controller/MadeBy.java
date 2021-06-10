package com.kalwitalo.todolistjenkins.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("madeby")
public class MadeBy {

    @GetMapping
    public String madeBy() {
        return "Made by Techlead IT Solutions";
    }
}
