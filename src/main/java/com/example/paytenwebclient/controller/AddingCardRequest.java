package com.example.paytenwebclient.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddingCardRequest {
    private String accountId;
    private String cardHolderName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String cardExpiry;
    private String cardSaveName;
    private String cardPan;
    private String callBackUrl;
    private String cvv;
}
