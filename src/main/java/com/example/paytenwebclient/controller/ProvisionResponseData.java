package com.example.paytenwebclient.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProvisionResponseData {
    private boolean success;
    private String errorCode;
    private String errorMessage;
}
