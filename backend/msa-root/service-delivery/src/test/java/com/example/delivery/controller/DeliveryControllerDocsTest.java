package com.example.delivery.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.delivery.dto.DeliveryDto;
import com.example.delivery.security.RoleCheckInterceptor;
import com.example.delivery.service.DeliveryService;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** DeliveryControllerDocsTest 타입입니다. */
@ExtendWith(RestDocumentationExtension.class)
class DeliveryControllerDocsTest {

  private MockMvc mockMvc;
  private DeliveryService deliveryService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    deliveryService = mock(DeliveryService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new DeliveryController(deliveryService))
            .addInterceptors(new RoleCheckInterceptor())
            .setControllerAdvice(new RestExceptionHandler())
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void createDocumentsSuccessfulDeliveryCreation() throws Exception {
    when(deliveryService.createDelivery(eq("order-1"), eq("courier-a"), eq("SCHEDULED")))
        .thenReturn(sampleDelivery());

    mockMvc
        .perform(
            post("/api/v1/deliveries")
                .header("X-User-Role", "STORE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "orderId": "order-1",
                      "address": "Seoul",
                      "courier": "courier-a"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("delivery-1"))
        .andDo(
            document(
                "deliveries-create-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("X-User-Role").description("Requester role (STORE or ADMIN)")),
                requestFields(
                    fieldWithPath("orderId").description("Order id"),
                    fieldWithPath("address").description("Delivery address").optional(),
                    fieldWithPath("courier").description("Courier name").optional()),
                responseFields(deliveryResponseFields())));
  }

  @Test
  void createDocumentsForbiddenWhenRoleHeaderMissing() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "orderId": "order-1"
                    }
                    """))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error").value("역할 정보가 없습니다."))
        .andDo(
            document(
                "deliveries-create-forbidden-missing-role",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("error").description("Authorization error"))));
  }

  @Test
  void getDocumentsSuccessfulLookup() throws Exception {
    when(deliveryService.findById("delivery-1")).thenReturn(Optional.of(sampleDelivery()));

    mockMvc
        .perform(get("/api/v1/deliveries/{id}", "delivery-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.orderId").value("order-1"))
        .andDo(
            document(
                "deliveries-get-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("Delivery id")),
                responseFields(deliveryResponseFields())));
  }

  @Test
  void getDocumentsNotFoundResponse() throws Exception {
    when(deliveryService.findById("missing-delivery")).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/v1/deliveries/{id}", "missing-delivery"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404))
        .andDo(
            document(
                "deliveries-get-not-found",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("Delivery id")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  private DeliveryDto sampleDelivery() {
    return new DeliveryDto(
        "delivery-1",
        "order-1",
        "courier-a",
        "SCHEDULED",
        3000,
        Instant.parse("2026-03-13T00:00:00Z"));
  }

  private org.springframework.restdocs.payload.FieldDescriptor[] deliveryResponseFields() {
    return new org.springframework.restdocs.payload.FieldDescriptor[] {
      fieldWithPath("success").description("Whether request succeeded"),
      fieldWithPath("code").description("Response code"),
      fieldWithPath("message").description("Response message"),
      fieldWithPath("data.id").description("Delivery id"),
      fieldWithPath("data.orderId").description("Order id"),
      fieldWithPath("data.courier").description("Courier"),
      fieldWithPath("data.status").description("Delivery status"),
      fieldWithPath("data.deliveryFee").description("Delivery fee"),
      fieldWithPath("data.scheduledAt").description("Scheduled time")
    };
  }
}
