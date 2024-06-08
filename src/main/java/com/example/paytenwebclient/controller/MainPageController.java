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

import java.util.UUID;

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
  public String preauth(Model model) {

    final var request =
        new ProvisionRequest(
            "fe6f4d99-c2cc-4c0b-b28b-76a3a273ebc6", "100", UUID.randomUUID().toString());
    model.addAttribute("provisionRequest", request);

    return "preauth";
  }

  @GetMapping("/addcard")
  public String addCard(Model model) {
    final var request =
        new AddCardRequest("1", "John Doe", "12/26", "John Doe", "5218487962459752", "000");
    model.addAttribute("addCardRequest", request);
    return "addcard";
  }

  @GetMapping("/editCard")
  public String editCard(Model model) {

    final var request =
        new EditingCardRequest(
            "e90bf5df-ac33-4fd4-a8eb-2384be743dde",
            "12/26",
            "my edited new card",
            "http://localhost:8081/3D-result",
            "000",
            "7");
    model.addAttribute("editingCardRequest", request);

    return "editCard";
  }

  @PostMapping("/addcard")
  @ResponseBody
  public String addCard(@ModelAttribute("addCardRequest") AddCardRequest addCardRequest)
      throws JsonProcessingException {
    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    return restClient
        .post()
        .uri("/cards/add")
        .body(
            AddCardRequest.builder()
                .accountId("1")
                .cardHolderName(addCardRequest.getCardHolderName())
                .cardExpiry(addCardRequest.getCardExpiry())
                .cardSaveName(addCardRequest.getCardHolderName())
                .cardPan(addCardRequest.getCardPan())
                .cvv(addCardRequest.getCvv())
                .build())
        .retrieve()
        .body(String.class);
  }

  // kard pan gonderdigimde farklı kartın tokenini alıp basarılı provizyon alıyor acık olabilir!!!

  @PostMapping("/3D-result")
  public String result(Model model, HttpServletRequest httpServletRequest)
      throws JsonProcessingException {
    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    final var verifyPreAuthRequest =
        VerifyPreAuthRequest.builder()
            .paymentId(httpServletRequest.getParameter("merchantPaymentId"))
            .build();
    final var response =
        restClient
            .post()
            .uri("/provision/verify-pre-auth")
            .body(verifyPreAuthRequest)
            .retrieve()
            .body(String.class);
    final var verifyPreAuthResponse = objectMapper.readValue(response, VerifyPreAuthResponse.class);
    model.addAttribute("success", verifyPreAuthResponse.getData().getSuccess());
    model.addAttribute("errorCode", verifyPreAuthResponse.getData().getErrorCode());
    model.addAttribute("errorMessage", verifyPreAuthResponse.getData().getErrorMessage());
    return "3D-result";
  }

  @PostMapping("/savecard")
  public String saveCard() {
    return "savecard";
  }

  // provizyon alma servisidir
  @PostMapping("/preauth")
  public String preauth(
      @ModelAttribute("provisionRequest") ProvisionRequest provisionRequest, Model model)
      throws JsonProcessingException {

    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    final var provisionReq =
        ProvisionRequest
            .builder()
            .cardId(provisionRequest.getCardId())
            .rentalId(UUID.randomUUID().toString())
            .amount(provisionRequest.getAmount())
            .build();

    final var response =
        restClient.post().uri("/provision/open").body(provisionReq).retrieve().body(String.class);

    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    final var paymentPreauthResponse = objectMapper.readValue(response, ProvisionResponse.class);
    model.addAttribute("success", paymentPreauthResponse.getData().isSuccess());
    model.addAttribute("errorCode", paymentPreauthResponse.getData().getErrorCode());
    model.addAttribute("errorMessage", paymentPreauthResponse.getData().getErrorMessage());

    return "preauthResult";
  }

  @PostMapping("/editCard")
  @ResponseBody
  public String editCard(
      @ModelAttribute("editingCardRequest") EditingCardRequest editingCardRequest)
      throws JsonProcessingException {
    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    final var body =
        restClient
            .post()
            .uri("/cards/edit")
            .body(
                EditingCardRequest
                    .builder()
                    .accountId("8")
                    .cardId(editingCardRequest.getCardId())
                    .cardSaveName(editingCardRequest.getCardSaveName())
                    .cardExpiry(editingCardRequest.getCardExpiry())
                    .cvv(editingCardRequest.getCvv())
                    .callBackUrl("http://localhost:8081/3D-result")
                    .build())
            .retrieve()
            .body(String.class);
    final var editingCardResponse = objectMapper.readValue(body, EditingCardResponse.class);
    final var preAuthRequest =
        new PreAuthRequest(
            editingCardResponse.getData().getSessionToken(),
            editingCardResponse.getData().getCardToken(),
            editingCardRequest.getCvv());
    paytenSessionScope.setCardToken(editingCardResponse.getData().getCardToken());
    paytenSessionScope.setPaymentId(editingCardResponse.getData().getPaymentId());
    return restClient
        .post()
        .uri("/provision/pre-auth-page")
        .body(preAuthRequest)
        .retrieve()
        .body(String.class);
  }

  @PostMapping("/verifycard")
  @ResponseBody
  public String verifyCard(@ModelAttribute("verifyCardRequest") VerifyCardRequest verifyCardRequest)
      throws JsonProcessingException {
    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    final var body =
        restClient
            .post()
            .uri("/cards/verification")
            .body(VerifyCardRequest.builder().cardId(verifyCardRequest.getCardId()).build())
            .retrieve()
            .body(String.class);
    final var verifyCardResponse = objectMapper.readValue(body, VerifyCardResponse.class);
    final var preAuthRequest =
        new PreAuthRequest(
            verifyCardResponse.getData().getSessionToken(),
            verifyCardResponse.getData().getCardToken(),
            verifyCardRequest.getCvv());
    paytenSessionScope.setCardToken(verifyCardResponse.getData().getCardToken());
    paytenSessionScope.setPaymentId(verifyCardResponse.getData().getPaymentId());
    return restClient
        .post()
        .uri("/provision/pre-auth-page")
        .body(preAuthRequest)
        .retrieve()
        .body(String.class);
  }

  @GetMapping("/verifycard")
  public String verifyCard(Model model) {
    final var request = new VerifyCardRequest("308e3226-e826-4286-8057-c69b915597cd", "000");
    model.addAttribute("verifyCardRequest", request);
    return "verifycard";
  }
}
