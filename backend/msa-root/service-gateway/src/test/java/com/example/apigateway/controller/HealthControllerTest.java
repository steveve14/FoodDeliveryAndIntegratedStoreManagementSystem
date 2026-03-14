package com.example.apigateway.controller;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

/** HealthControllerTest 타입입니다. */
@ExtendWith(RestDocumentationExtension.class)
class HealthControllerTest {

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    webTestClient =
        bindToController(new HealthController())
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void infoReturnsServiceMetadata() {
    webTestClient
        .get()
        .uri("/api/v1/info")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.success")
        .isEqualTo(true)
        .jsonPath("$.data.service")
        .isEqualTo("service-gateway")
        .jsonPath("$.data.status")
        .isEqualTo("UP")
        .consumeWith(
            document(
                "gateway-info-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
  }
}
