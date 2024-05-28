package com.example.paytenwebclient.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddingCardResponseData {
    private String sessionToken;
    private String cardToken;
}
