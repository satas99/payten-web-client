package com.example.paytenwebclient.controller;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ProvisionRequest {

    private String cardId;
    private String amount;
    private String rentalId;


}
