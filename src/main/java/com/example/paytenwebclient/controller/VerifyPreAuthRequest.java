package com.example.paytenwebclient.controller;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPreAuthRequest {
    private String cardToken;
    private String paymentId;
}
