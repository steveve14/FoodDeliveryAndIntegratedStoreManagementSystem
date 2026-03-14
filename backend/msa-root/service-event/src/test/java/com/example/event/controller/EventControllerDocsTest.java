package com.example.event.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

import com.example.event.dto.EventDto;
import com.example.event.service.EventService;
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

/** EventControllerDocsTest 타입입니다. */
@ExtendWith(RestDocumentationExtension.class)
class EventControllerDocsTest {

  private MockMvc mockMvc;
  private EventService eventService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    eventService = mock(EventService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new EventController(eventService))
            .setControllerAdvice(new RestExceptionHandler())
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void createDocumentsSuccessfulEventCreation() throws Exception {
    when(eventService.createEvent(eq("ORDER_CREATED"), eq("order-1"))).thenReturn(sampleEvent());

    mockMvc
        .perform(
            post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "type": "ORDER_CREATED",
                      "payload": "order-1"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value("event-1"))
        .andDo(
            document(
                "events-create-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("type").description("Event type"),
                    fieldWithPath("payload").description("Event payload string").optional()),
                responseFields(eventResponseFields())));
  }

  @Test
  void createDocumentsValidationError() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "type": "",
                      "payload": "payload"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andDo(
            document(
                "events-create-validation-error",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Validation error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void getDocumentsNotFoundResponse() throws Exception {
    when(eventService.findById("missing-event")).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/v1/events/{id}", "missing-event"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404))
        .andDo(
            document(
                "events-get-not-found",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("Event id")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  private EventDto sampleEvent() {
    return new EventDto(
        "event-1",
        "ORDER_CREATED",
        "{\"orderId\":\"order-1\"}",
        Instant.parse("2026-03-13T00:00:00Z"));
  }

  private org.springframework.restdocs.payload.FieldDescriptor[] eventResponseFields() {
    return new org.springframework.restdocs.payload.FieldDescriptor[] {
      fieldWithPath("success").description("Whether request succeeded"),
      fieldWithPath("code").description("Response code"),
      fieldWithPath("message").description("Response message"),
      fieldWithPath("data.id").description("Event id"),
      fieldWithPath("data.type").description("Event type"),
      fieldWithPath("data.payload").description("Event payload"),
      fieldWithPath("data.createdAt").description("Created time")
    };
  }
}
