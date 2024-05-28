package com.example.paytenwebclient.controller;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class PreAuthRequest {
    private String sessionToken;
    private String cardToken;
    private String cvv;
}
