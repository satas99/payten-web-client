package com.example.paytenwebclient.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainPageController {
    private final ObjectMapper objectMapper;
    private final PaytenSessionScope paytenSessionScope;

    @GetMapping()
    public String mainPage() {
        return "mainpage";
    }

    @GetMapping("/addcardlist")
    public String addCardList() {
        return "addcardlist";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/preauth")
    public String preauth() {
        return "preauth";
    }

    @GetMapping("/addcard")
    public String addCard(Model model) {
        final var request = new AddingCardRequest("1",
                "John Doele", "John Doe",
                "customer@gmail.com", "5422433412",
                "12/26", "John Doe",
                "5218487962459752", "http://localhost:8081/3D-result", "000");
        model.addAttribute("addingCardRequest", request);
        return "addcard";
    }

    @PostMapping("/addcard")
    @ResponseBody
    public String addCard(@ModelAttribute("addingCardRequest") AddingCardRequest addingCardRequest) throws JsonProcessingException {
        final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
        final var body = restClient.post().uri("/cards/add").body(AddingCardRequest
                .builder()
                .accountId("1")
                .cardHolderName(addingCardRequest.getCardHolderName())
                .customerName(addingCardRequest.getCardHolderName())
                .customerEmail("customer@gmail.com")
                .customerPhone("5422433412")
                .cardExpiry(addingCardRequest.getCardExpiry())
                .cardSaveName(addingCardRequest.getCardHolderName())
                .cardPan(addingCardRequest.getCardPan())
                .callBackUrl("http://localhost:8081/3D-result")
                .build()).retrieve().body(String.class);
        final var addingCardResponse = objectMapper.readValue(body, AddingCardResponse.class);
        final var preAuthRequest = new PreAuthRequest(addingCardResponse.getData().getSessionToken(), addingCardResponse.getData().getCardToken(), addingCardRequest.getCvv());
        paytenSessionScope.setCardToken(addingCardResponse.getData().getCardToken());
        paytenSessionScope.setPaymentId(addingCardResponse.getData().getPaymentId());
        return restClient.post().uri("/cards/pre-auth-page").body(preAuthRequest).retrieve().body(String.class);
    }


    @RequestMapping(value = "/someURL", method = GET)
    public String yourMethod(RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("rd", "rdValue");
        return "redirect:/someOtherURL";
    }

    @PostMapping("/3D-result")
    public String result(Model model) throws JsonProcessingException {
        final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
        final var verifyPreAuthRequest = VerifyPreAuthRequest.builder()
                .paymentId(paytenSessionScope.getPaymentId())
                .cardToken(paytenSessionScope.getCardToken()).build();
        final var response = restClient.post().uri("/cards/verify-pre-auth")
                .body(verifyPreAuthRequest)
                .retrieve()
                .body(String.class);
        final var verifyPreAuthResponse = objectMapper.readValue(response, VerifyPreAuthResponse.class);
        model.addAttribute("success", verifyPreAuthResponse.getData().getSuccess());
        return "3D-result";
    }

    @PostMapping("/savecard")
    public String saveCard() {
        return "savecard";
    }


    @PostMapping("/preauth")
    public String preauth(@ModelAttribute("provisionRequest") ProvisionRequest provisionRequest,Model model) throws JsonProcessingException {

        final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    final var paymentPreauthRequest =
        PaymentPreAuthRequest.builder()
            .merchantPaymentId(String.valueOf(new Random().nextInt(16)))
            .cardId("3f625d6c-3cfd-4ba4-893a-ee702b629bd8")
            .amount("1500")
            .cardPan("6501617060023449")
            .cardExpiry("12.2040")
            .cardCvv("000")
            .build();

        final var response = restClient.post().uri("/provision/open")
                .body(paymentPreauthRequest)
                .retrieve()
                .body(String.class);


        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        final var paymentPreauthResponse = objectMapper.readValue(response, PaymentPreAuthResponse.class);
        model.addAttribute("result", paymentPreauthResponse.getData().getResponseCode().equals("00")?"Provizyon alma basarili":"Provizyon alma sirasinda hata "+paymentPreauthResponse.getData().getErrorMsg());

        return "preauthResult";
    }


}
