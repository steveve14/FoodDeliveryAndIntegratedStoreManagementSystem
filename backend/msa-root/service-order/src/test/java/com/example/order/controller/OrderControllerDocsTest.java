package com.example.order.controller;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.order.dto.FrontendCustomerOrderSummaryDto;
import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.security.RoleCheckInterceptor;
import com.example.order.service.OrderService;
import java.time.Instant;
import java.util.List;
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

@ExtendWith(RestDocumentationExtension.class)
class OrderControllerDocsTest {

  private MockMvc mockMvc;
  private OrderService orderService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    orderService = mock(OrderService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new OrderController(orderService))
            .addInterceptors(new RoleCheckInterceptor())
            .setControllerAdvice(new RestExceptionHandler())
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void createDocumentsSuccessfulOrderCreation() throws Exception {
    when(orderService.createOrder(eq("user-1"), any(List.class), eq("CREATED"))).thenReturn(sampleOrder());

    mockMvc
        .perform(
            post("/api/v1/orders")
                .header("X-User-Id", "user-1")
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "userId": "ignored-by-controller",
                      "items": [
                        {
                          "productId": "menu-1",
                          "quantity": 2,
                          "price": 12000
                        }
                      ],
                      "address": "Seoul",
                      "totalAmount": 24000
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("order-1"))
        .andExpect(jsonPath("$.data.status").value("CREATED"))
        .andDo(
            document(
                "orders-create-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("X-User-Id").description("Requester user id"),
                    headerWithName("X-User-Role").description("Requester role")),
                requestFields(
                    fieldWithPath("userId").description("User id in request body").optional(),
                    fieldWithPath("items").description("Order item list"),
                    fieldWithPath("items[].productId").description("Product id"),
                    fieldWithPath("items[].quantity").description("Quantity"),
                    fieldWithPath("items[].price").description("Snapshot price"),
                    fieldWithPath("address").description("Delivery address"),
                    fieldWithPath("totalAmount").description("Total amount")),
                responseFields(orderResponseFields())));
  }

  @Test
  void createDocumentsValidationError() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/orders")
                .header("X-User-Id", "user-1")
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "items": [],
                      "address": "",
                      "totalAmount": -1
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400))
        .andDo(
            document(
                "orders-create-validation-error",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Validation error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void getMyOrdersDocumentsSuccessResponse() throws Exception {
    when(orderService.findByUserId("user-1")).thenReturn(List.of(sampleOrder()));

    mockMvc
        .perform(get("/api/v1/orders/my").header("X-User-Id", "user-1").header("X-User-Role", "USER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id").value("order-1"))
        .andDo(
            document(
                "orders-my-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("X-User-Id").description("Requester user id"),
                    headerWithName("X-User-Role").description("Requester role")),
                responseFields(orderListResponseFields())));
  }

  @Test
  void getFrontendCustomerSummariesDocumentsSuccessResponse() throws Exception {
    when(orderService.getFrontendCustomerSummaries())
        .thenReturn(List.of(new FrontendCustomerOrderSummaryDto("user-1", 12L, Instant.parse("2026-03-13T00:00:00Z"), "vip")));

    mockMvc
        .perform(get("/api/v1/orders/frontend/customer-summaries"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].userId").value("user-1"))
        .andDo(
            document(
                "orders-frontend-customer-summaries-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data[].userId").description("User id"),
                    fieldWithPath("data[].ordersCount").description("Order count"),
                    fieldWithPath("data[].lastOrderAt").description("Last order time"),
                    fieldWithPath("data[].grade").description("Customer grade"))));
  }

  @Test
  void updateStatusDocumentsConflictResponse() throws Exception {
    when(orderService.updateStatus("order-1", "DONE"))
        .thenThrow(new IllegalStateException("Cannot transition from CREATED to DONE"));

    mockMvc
        .perform(
            patch("/api/v1/orders/{id}/status", "order-1")
                .header("X-User-Role", "STORE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "status": "DONE"
                    }
                    """))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(409))
        .andDo(
            document(
                "orders-update-status-conflict",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("Order id")),
                requestHeaders(headerWithName("X-User-Role").description("Requester role (STORE or ADMIN)")),
                requestFields(fieldWithPath("status").description("New order status")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("State transition error"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void getDocumentsNotFoundResponse() throws Exception {
    when(orderService.findById("missing-order")).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/v1/orders/{id}", "missing-order"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404))
        .andDo(
            document(
                "orders-get-not-found",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("Order id")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void getMyOrdersDocumentsForbiddenWhenRoleHeaderMissing() throws Exception {
    mockMvc
        .perform(get("/api/v1/orders/my").header("X-User-Id", "user-1"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.error").exists())
        .andDo(
            document(
                "orders-my-forbidden-missing-role",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("error").description("Authorization error"))));
  }

  private List<OrderItemDto> sampleItems() {
    return List.of(new OrderItemDto("menu-1", 2, 12000));
  }

  private OrderDto sampleOrder() {
    return new OrderDto(
        "order-1",
        "user-1",
        "store-1",
        24000,
        sampleItems(),
        "CREATED",
        Instant.parse("2026-03-13T00:00:00Z"));
  }

  private org.springframework.restdocs.payload.FieldDescriptor[] orderResponseFields() {
    return new org.springframework.restdocs.payload.FieldDescriptor[] {
      fieldWithPath("success").description("Whether request succeeded"),
      fieldWithPath("code").description("Response code"),
      fieldWithPath("message").description("Response message"),
      fieldWithPath("data.id").description("Order id"),
      fieldWithPath("data.userId").description("User id"),
      fieldWithPath("data.storeId").description("Store id"),
      fieldWithPath("data.totalAmount").description("Total amount"),
      fieldWithPath("data.items[].productId").description("Product id"),
      fieldWithPath("data.items[].quantity").description("Quantity"),
      fieldWithPath("data.items[].price").description("Snapshot price"),
      fieldWithPath("data.status").description("Order status"),
      fieldWithPath("data.createdAt").description("Created time")
    };
  }

  private org.springframework.restdocs.payload.FieldDescriptor[] orderListResponseFields() {
    return new org.springframework.restdocs.payload.FieldDescriptor[] {
      fieldWithPath("success").description("Whether request succeeded"),
      fieldWithPath("code").description("Response code"),
      fieldWithPath("message").description("Response message"),
      fieldWithPath("data[].id").description("Order id"),
      fieldWithPath("data[].userId").description("User id"),
      fieldWithPath("data[].storeId").description("Store id"),
      fieldWithPath("data[].totalAmount").description("Total amount"),
      fieldWithPath("data[].items[].productId").description("Product id"),
      fieldWithPath("data[].items[].quantity").description("Quantity"),
      fieldWithPath("data[].items[].price").description("Snapshot price"),
      fieldWithPath("data[].status").description("Order status"),
      fieldWithPath("data[].createdAt").description("Created time")
    };
  }
}
