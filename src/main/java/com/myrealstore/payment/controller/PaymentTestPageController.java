package com.myrealstore.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myrealstore.payment.properties.PaymentProperties;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/test/payment")
@RequiredArgsConstructor
public class PaymentTestPageController {
    private final PaymentProperties paymentProperties;

    @GetMapping
    public String paymentTestPage(Model model) {
        model.addAttribute("clientKey", paymentProperties.getToss().getClientKey());
        return "payment/checkout";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam int amount,
                                 Model model) {
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "payment/success";
    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam String code,
                              @RequestParam String message,
                              Model model) {
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        return "payment/fail";
    }
}
