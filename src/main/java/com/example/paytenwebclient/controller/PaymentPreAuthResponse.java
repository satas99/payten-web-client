package com.example.paytenwebclient.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPreAuthResponse {
    private PaymentPreAuthResponseData data;
}
