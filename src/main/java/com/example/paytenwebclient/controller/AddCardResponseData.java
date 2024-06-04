package com.example.paytenwebclient.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddCardResponseData {
    private String sessionToken;
    private String cardToken;
    private String paymentId;
}
