package com.csrf.csrf.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BankController {

    @GetMapping("/transfer")
    public void transfer(@RequestParam int accountNo, @RequestParam final int amount) {
        log.warn("accountNo: {} , amount:{}", accountNo, amount);
    }

    @PostMapping("/transfer")
    public void transfer2(@RequestParam int accountNo, @RequestParam final int amount) {
        log.warn("accountNo: {} , amount:{}", accountNo, amount);
    }
}
