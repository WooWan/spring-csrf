package com.csrf.csrf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FakeController {

    @GetMapping("/fake")
    public String fakePage() {
        System.out.println("fake");
        return "fakeBank";
    }

    @GetMapping("/bank")
    public String bankPage() {
        System.out.println("bank");
        return "bank";
    }
}
