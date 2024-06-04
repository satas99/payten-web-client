package com.example.paytenwebclient.controller;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCardResponseData {
    private String sessionToken;
    private String cardToken;
    private String paymentId;
}
