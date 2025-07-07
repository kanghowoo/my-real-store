package com.myrealstore.payment.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "payment")
@Getter
public class PaymentProperties {
    private final Toss toss = new Toss();
    private final Kakao kakao = new Kakao();

    @Getter
    @Setter
    public static class Toss {
        private String baseUrl;
        private String secretKey;
        private String clientKey;
    }

    @Getter
    @Setter
    public static class Kakao {
        private String baseUrl;
        private String secretKey;
    }
}
