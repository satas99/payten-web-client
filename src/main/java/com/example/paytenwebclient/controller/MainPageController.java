package com.example.paytenwebclient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/addcard")
    public String addCard(Model model) {
        final var request = new AddingCardRequest("1",
                "John Doe", "John Doe",
                "customer@gmail.com", "5422433412",
                "12/26", "John Doe",
                "4799150896081734", "http://localhost:8081/3D-result", "000");
        model.addAttribute("addingCardRequest", request);
        return "addcard";
    }

    @PostMapping("/addcard")
    @ResponseBody
    public String addCard(Model model, @ModelAttribute("addingCardRequest") AddingCardRequest addingCardRequest) throws JsonProcessingException {
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
        return restClient.post().uri("/cards/pre-auth-page").body(preAuthRequest).retrieve().body(String.class);
    }


    @RequestMapping(value = "/someURL", method = GET)
    public String yourMethod(RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("rd", "rdValue");
        return "redirect:/someOtherURL";
    }

    @PostMapping("/3D-result")
    public String succes(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model) throws JsonProcessingException {
        /*final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
        final var verifyPreAuthRequest = VerifyPreAuthRequest.builder()
                .pgTranId(httpServletRequest.getParameter("pgTranId"))
                .cardToken(paytenSessionScope.getCardToken()).build();
        final var response = restClient.post().uri("/cards/verify-pre-auth")
                .body(verifyPreAuthRequest)
                .retrieve()
                .body(String.class);
        final var verifyPreAuthResponse = objectMapper.readValue(response, VerifyPreAuthResponse.class);
        model.addAttribute("success", verifyPreAuthResponse.getData().getSuccess());*/
        return "3D-result";
    }

    @PostMapping("/savecard")
    public String saveCard() {
        return "savecard";
    }

}
