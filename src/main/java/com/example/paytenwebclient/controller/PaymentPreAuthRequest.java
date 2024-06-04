package com.example.paytenwebclient.controller;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPreAuthRequest {
    private String cardId;
    private String paymentId;
    private String amount;
}
