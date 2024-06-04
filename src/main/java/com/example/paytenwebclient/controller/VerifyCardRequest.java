package com.example.paytenwebclient.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerifyCardRequest {
    private String cardId;
    private String cvv;
}
