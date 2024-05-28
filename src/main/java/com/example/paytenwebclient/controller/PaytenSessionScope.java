package com.example.paytenwebclient.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Setter
@Getter
public class PaytenSessionScope {
    private String cardToken;
}
