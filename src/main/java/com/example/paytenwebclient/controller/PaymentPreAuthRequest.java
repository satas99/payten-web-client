package com.example.paytenwebclient.controller;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPreAuthRequest {
    private String cardId;
    private String merchantPaymentId;
    private String amount;
    private String cardPan;
    private String cardExpiry;
    private String cardCvv;
}
