package com.example.paytenwebclient.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditingCardRequest {
    private String cardId;
    private String cardExpiry;
    private String cardSaveName;
    private String callBackUrl;
    private String cvv;
    private String accountId;
}
