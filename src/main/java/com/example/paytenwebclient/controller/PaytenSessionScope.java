package com.example.paytenwebclient.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class PaytenSessionScope {
    private String cardToken;
    private String paymentId;
}
