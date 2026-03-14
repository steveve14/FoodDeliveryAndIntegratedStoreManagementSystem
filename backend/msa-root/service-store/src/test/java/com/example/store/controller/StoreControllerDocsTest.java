package com.example.store.controller;

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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.store.dto.CreateStoreRequest;
import com.example.store.dto.StoreDto;
import com.example.store.security.RoleCheckInterceptor;
import com.example.store.service.StoreService;
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

/** StoreControllerDocsTest 타입입니다. */
@ExtendWith(RestDocumentationExtension.class)
class StoreControllerDocsTest {

  private MockMvc mockMvc;
  private StoreService storeService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    storeService = mock(StoreService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new StoreController(storeService))
            .addInterceptors(new RoleCheckInterceptor())
            .setControllerAdvice(new RestExceptionHandler())
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void createDocumentsSuccessfulStoreCreation() throws Exception {
    StoreDto store = sampleStore();

    when(storeService.createStore(any(CreateStoreRequest.class), eq("owner-1"), eq("STORE")))
        .thenReturn(store);

    mockMvc
        .perform(
            post("/api/v1/stores")
                .header("X-User-Id", "owner-1")
                .header("X-User-Role", "STORE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "Tasty Place",
                      "address": "Seoul",
                      "phone": "010-1234-5678",
                      "category": "KOREAN",
                      "status": "OPEN",
                      "latitude": 37.123,
                      "longitude": 127.456,
                      "minOrderAmount": 15000,
                      "ratingAvg": 4.7,
                      "description": "Korean food",
                      "openingHours": "09:00-21:00",
                      "ownerId": "owner-1"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("store-1"))
        .andExpect(jsonPath("$.data.ownerId").value("owner-1"))
        .andDo(
            document(
                "stores-create-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("X-User-Id").description("Requester user id"),
                    headerWithName("X-User-Role").description("Requester role (STORE or ADMIN)")),
                requestFields(
                    fieldWithPath("name").description("Store name"),
                    fieldWithPath("address").description("Store address"),
                    fieldWithPath("phone").description("Phone").optional(),
                    fieldWithPath("category").description("Category").optional(),
                    fieldWithPath("status").description("Status").optional(),
                    fieldWithPath("latitude").description("Latitude").optional(),
                    fieldWithPath("longitude").description("Longitude").optional(),
                    fieldWithPath("minOrderAmount").description("Min order amount").optional(),
                    fieldWithPath("ratingAvg").description("Average rating").optional(),
                    fieldWithPath("description").description("Store description").optional(),
                    fieldWithPath("openingHours").description("Opening hours").optional(),
                    fieldWithPath("ownerId").description("Owner id").optional()),
                responseFields(storeResponseFields())));
  }

  @Test
  void createDocumentsForbiddenWhenRoleHeaderMissing() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/stores")
                .header("X-User-Id", "owner-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "Tasty Place",
                      "address": "Seoul"
                    }
                    """))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error").value("역할 정보가 없습니다."))
        .andDo(
            document(
                "stores-create-forbidden-missing-role",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("error").description("Authorization error"))));
  }

  @Test
  void listDocumentsStoreCollection() throws Exception {
    when(storeService.list(eq("KOREAN"), eq("OPEN"))).thenReturn(List.of(sampleStore()));

    mockMvc
        .perform(get("/api/v1/stores").param("category", "KOREAN").param("status", "OPEN"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].id").value("store-1"))
        .andDo(
            document(
                "stores-list-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("category").description("Category filter").optional(),
                    parameterWithName("status").description("Status filter").optional()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data[].id").description("Store id"),
                    fieldWithPath("data[].name").description("Store name"),
                    fieldWithPath("data[].address").description("Store address"),
                    fieldWithPath("data[].phone").description("Phone").optional(),
                    fieldWithPath("data[].category").description("Category").optional(),
                    fieldWithPath("data[].status").description("Status").optional(),
                    fieldWithPath("data[].latitude").description("Latitude").optional(),
                    fieldWithPath("data[].longitude").description("Longitude").optional(),
                    fieldWithPath("data[].minOrderAmount")
                        .description("Min order amount")
                        .optional(),
                    fieldWithPath("data[].ratingAvg").description("Average rating").optional(),
                    fieldWithPath("data[].description").description("Store description").optional(),
                    fieldWithPath("data[].openingHours").description("Opening hours").optional(),
                    fieldWithPath("data[].ownerId").description("Owner id"),
                    fieldWithPath("data[].eta").description("ETA").optional(),
                    fieldWithPath("data[].reviewCount").description("Review count").optional(),
                    fieldWithPath("data[].deliveryFee").description("Delivery fee info").optional(),
                    fieldWithPath("data[].heroIcon").description("Hero icon code").optional(),
                    fieldWithPath("data[].tags[]").description("Tags").optional(),
                    fieldWithPath("data[].bestseller").description("Bestseller").optional(),
                    fieldWithPath("data[].promo").description("Promo text").optional())));
  }

  @Test
  void getDocumentsNotFoundResponse() throws Exception {
    when(storeService.findById("missing-store")).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/v1/stores/{id}", "missing-store"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(404))
        .andDo(
            document(
                "stores-get-not-found",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("Store id")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void getMyStoreDocumentsSuccessResponse() throws Exception {
    when(storeService.findByOwnerId("owner-1")).thenReturn(Optional.of(sampleStore()));

    mockMvc
        .perform(
            get("/api/v1/stores/me").header("X-User-Id", "owner-1").header("X-User-Role", "STORE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value("store-1"))
        .andDo(
            document(
                "stores-me-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("X-User-Id").description("Requester user id"),
                    headerWithName("X-User-Role").description("Requester role")),
                responseFields(storeResponseFields())));
  }

  private StoreDto sampleStore() {
    return new StoreDto(
        "store-1",
        "Tasty Place",
        "Seoul",
        "010-1234-5678",
        "KOREAN",
        "OPEN",
        37.123,
        127.456,
        15000L,
        4.7,
        "Korean food",
        "09:00-21:00",
        "owner-1",
        "20-30 min",
        15,
        "Free delivery",
        "i-lucide-store",
        List.of("korean", "lunch"),
        "Bibimbap",
        "Quick add from bestseller.");
  }

  private org.springframework.restdocs.payload.FieldDescriptor[] storeResponseFields() {
    return new org.springframework.restdocs.payload.FieldDescriptor[] {
      fieldWithPath("success").description("Whether request succeeded"),
      fieldWithPath("code").description("Response code"),
      fieldWithPath("message").description("Response message"),
      fieldWithPath("data.id").description("Store id"),
      fieldWithPath("data.name").description("Store name"),
      fieldWithPath("data.address").description("Store address"),
      fieldWithPath("data.phone").description("Phone").optional(),
      fieldWithPath("data.category").description("Category").optional(),
      fieldWithPath("data.status").description("Status").optional(),
      fieldWithPath("data.latitude").description("Latitude").optional(),
      fieldWithPath("data.longitude").description("Longitude").optional(),
      fieldWithPath("data.minOrderAmount").description("Min order amount").optional(),
      fieldWithPath("data.ratingAvg").description("Average rating").optional(),
      fieldWithPath("data.description").description("Store description").optional(),
      fieldWithPath("data.openingHours").description("Opening hours").optional(),
      fieldWithPath("data.ownerId").description("Owner id"),
      fieldWithPath("data.eta").description("ETA").optional(),
      fieldWithPath("data.reviewCount").description("Review count").optional(),
      fieldWithPath("data.deliveryFee").description("Delivery fee info").optional(),
      fieldWithPath("data.heroIcon").description("Hero icon code").optional(),
      fieldWithPath("data.tags[]").description("Tags").optional(),
      fieldWithPath("data.bestseller").description("Bestseller").optional(),
      fieldWithPath("data.promo").description("Promo text").optional()
    };
  }
}
