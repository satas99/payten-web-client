package com.example.paytenwebclient.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddCardRequest {
    private String accountId;
    private String cardHolderName;
    private String cardExpiry;
    private String cardSaveName;
    private String cardPan;
    private String cvv;
}
