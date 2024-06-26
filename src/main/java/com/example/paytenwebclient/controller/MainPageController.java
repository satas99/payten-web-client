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
            "00d44eae-e935-411f-9449-3fad23ca8ced", "1000", UUID.randomUUID().toString());
    model.addAttribute("provisionRequest", request);

    return "preauth";
  }


  @GetMapping("/postauth")
  public String postauth(Model model) {

    final var request =
        new PostauthRequest(
            "00d44eae-e935-411f-9449-3fad23ca8ced", "100", "07fa8558-c812-44c6-9469-b77fdb7cbefd");
    model.addAttribute("postauthRequest", request);

    return "postauth";
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
            "3f3d7188-c7ef-4cfb-925e-ca33f3b5957b",
            "04/27",
            "my edited new card",
            "http://localhost:8081/3D-result",
            "423",
            "1");
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
            .uri("/provisions/verify-pre-auth")
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
        restClient.post().uri("/provisions/open").body(provisionReq).retrieve().body(String.class);

    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    final var paymentPreauthResponse = objectMapper.readValue(response, ProvisionResponse.class);
    model.addAttribute("success", paymentPreauthResponse.getData().isSuccess());
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
                                    .accountId("1")
                                    .cardId(editingCardRequest.getCardId())
                                    .cardSaveName(editingCardRequest.getCardSaveName())
                                    .cardExpiry(editingCardRequest.getCardExpiry())
                                    .cvv(editingCardRequest.getCvv())
                                    .build())
                    .retrieve()
                    .body(String.class);

    return body;
  }

  @PostMapping("/verifycard")
  @ResponseBody
  public String verifyCard(@ModelAttribute("verifyCardRequest") VerifyCardRequest verifyCardRequest)
      throws JsonProcessingException {
    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    return restClient
            .post()
            .uri("/cards/verify")
            .body(VerifyCardRequest.builder().cardId(verifyCardRequest.getCardId()).build())
            .retrieve()
            .body(String.class);
  }

  @GetMapping("/verifycard")
  public String verifyCard(Model model) {
    final var request = new VerifyCardRequest("308e3226-e826-4286-8057-c69b915597cd", "000");
    model.addAttribute("verifyCardRequest", request);
    return "verifycard";
  }

  @PostMapping("/postauth")
  public String postauth(
          @ModelAttribute("postauthRequest") PostauthRequest postauthRequest, Model model)
          throws JsonProcessingException {

    final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
    final var posauthReq =
            PostauthRequest
                    .builder()
                    .cardId(postauthRequest.getCardId())
                    .rentalId(postauthRequest.getRentalId())
                    .amount(postauthRequest.getAmount())
                    .build();

    final var response =
            restClient.post().uri("/payments/postauth").body(posauthReq).retrieve().body(String.class);

    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    final var paymentPostauthResponse = objectMapper.readValue(response, PostauthResponse.class);
    model.addAttribute("success", paymentPostauthResponse.getData().isSuccess());
    return "postauthResult";
  }
}
