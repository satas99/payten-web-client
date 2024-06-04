package com.example.paytenwebclient.controller;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class CardEditPreAuthRequest {
    private String sessionToken;
    private String cardToken;

}
