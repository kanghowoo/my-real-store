package com.myrealstore.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.myrealstore.payment.properties.PaymentProperties;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {
}
