package com.example.PetApp.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PracticeController {

    @GetMapping("/hello")
    public String hello() {
        return "hello from server!";
    }
}
