package com.example.userservice.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.userservice.dto.AuthUserDto;
import com.example.userservice.service.UserService;
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
class AuthControllerTest {

  private MockMvc mockMvc;
  private UserService userService;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    userService = mock(UserService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new AuthController(userService))
            .setControllerAdvice(new RestExceptionHandler())
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void authenticateReturnsUserWhenCredentialsMatch() throws Exception {
    when(userService.authenticate(eq("user@example.com"), eq("password")))
        .thenReturn(new AuthUserDto("user-1", "user@example.com", "Tester", "USER"));

    mockMvc
        .perform(
            post("/api/v1/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "user@example.com",
                      "password": "password"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("user-1"))
        .andExpect(jsonPath("$.data.roles").value("USER"))
        .andDo(
            document(
                "users-authenticate-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").description("User email for authentication"),
                    fieldWithPath("password").description("User password for authentication")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data.id").description("User id"),
                    fieldWithPath("data.email").description("User email"),
                    fieldWithPath("data.name").description("User name"),
                    fieldWithPath("data.roles").description("User roles"))));
  }

  @Test
  void authenticateReturnsUnauthorizedWhenCredentialsDoNotMatch() throws Exception {
    when(userService.authenticate(eq("user@example.com"), eq("bad-password"))).thenReturn(null);

    mockMvc
        .perform(
            post("/api/v1/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "user@example.com",
                      "password": "bad-password"
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(401))
        .andDo(
            document(
                "users-authenticate-unauthorized",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").description("User email for authentication"),
                    fieldWithPath("password").description("Invalid password")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void authenticateValidatesRequestBody() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "not-an-email",
                      "password": ""
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400))
        .andDo(
            document(
                "users-authenticate-validation-error",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").description("Invalid email format"),
                    fieldWithPath("password").description("Blank password")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Validation error message"),
                    fieldWithPath("data").description("null on failure"))));
  }
}
