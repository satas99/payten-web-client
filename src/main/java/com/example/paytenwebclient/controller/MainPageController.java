package com.example.paytenwebclient.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

  @GetMapping("/addcard")
  public String addCard(Model model) {
//    final var request =
//        new AddingCardRequest(
//            "3",
//            "Atmo test",
//            "Atmo test",
//            "customer@gmail.com",
//            "5422433412",
//            "04/2027",
//            "Atmo test",
//            "5406697543211173",
//            "http://localhost:8081/3D-result",
//            "423");
//      final var request =    new AddingCardRequest(
//            "7",
//            "Atmo test",
//            "Atmo test",
//            "customer@gmail.com",
//            "5422433412",
//            "12/26",
//            "Atmo test",
//            "5163103002982563",
//            "http://localhost:8081/3D-result",
//            "000");
    final var request = new AddingCardRequest(
            "8",
            "yasemin atmoTest ",
            "Bob atmoTest",
            "customer@gmail.com",
            "5422433412",
            "12/30",
            "atmoTest",
            "5571135571135575",
            "http://localhost:8081/3D-result",
            "000");
//    final var request = new AddingCardRequest(
//            "5",
//            "Bob test ",
//            "Bob atmoTest",
//            "customer@gmail.com",
//            "5422433412",
//            "12/40",
//            "atmoTest",
//            "5400617030400291",
//            "http://localhost:8081/3D-result",
//            "000");
//    final var request = new AddingCardRequest(
//            "10",
//            "atmo test ",
//            "0406 atmoTest",
//            "customer@gmail.com",
//            "5422433412",
//            "12/40",
//            "yeni kart",
//            "4506347028991897",
//            "http://localhost:8081/3D-result",
//            "000");
    model.addAttribute("addingCardRequest", request);
    return "addcard";
  }

    @PostMapping("/addcard")
    @ResponseBody
    public String addCard(@ModelAttribute("addingCardRequest") AddingCardRequest addingCardRequest) throws JsonProcessingException {
        final var restClient = RestClient.builder().baseUrl("http://localhost:8080/api/v1").build();
        final var body = restClient.post().uri("/cards/add").body(AddingCardRequest
                .builder()
                .accountId("8")
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
        return restClient.post().uri("/provision/pre-auth-page").body(preAuthRequest).retrieve().body(String.class);
    }
//kard pan gonderdigimde farklı kartın tokenini alıp basarılı provizyon alıyor acık olabilir!!!

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
        final var response = restClient.post().uri("/provision/verify-pre-auth")
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
    final var paymentPreauthRequest =
        PaymentPreAuthRequest.builder()
            .paymentId(String.valueOf(new Random().nextInt(160) + 1008))
            .cardId("e90bf5df-ac33-4fd4-a8eb-2384be743dde")
            .amount("1126")
            .build();

    final var response =
        restClient
            .post()
            .uri("/provision/open")
            .body(paymentPreauthRequest)
            .retrieve()
            .body(String.class);

    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    final var paymentPreauthResponse =
        objectMapper.readValue(response, PaymentPreAuthResponse.class);
    model.addAttribute(
        "result",
        paymentPreauthResponse.getData().getResponseCode().equals("00")
            ? "Provizyon alma basarili"
            : "Provizyon alma sirasinda hata " + paymentPreauthResponse.getData().getErrorMsg());

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
                                editingCardRequest
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
                        editingCardRequest.getCvv()
                );
        paytenSessionScope.setCardToken(editingCardResponse.getData().getCardToken());
        paytenSessionScope.setPaymentId(editingCardResponse.getData().getPaymentId());
        return restClient
                .post()
                .uri("/provision/pre-auth-page")
                .body(preAuthRequest)
                .retrieve()
                .body(String.class);
    }
}
