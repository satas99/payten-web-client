package com.example.paytenwebclient.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentPreAuthResponseData {
    private String pgTranId;
    private String responseCode;
    private String errorCode;
    private String errorMsg;
    private String responseMsg;
    private String violatorParam;
    private String merchantPaymentId;
}
