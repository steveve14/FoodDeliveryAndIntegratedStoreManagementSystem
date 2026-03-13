package com.example.auth.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.auth.dto.LoginResult;
import com.example.auth.dto.TokenResponse;
import com.example.auth.grpc.client.UserGrpcClient;
import com.example.auth.security.JwtProvider;
import com.example.auth.service.AuthenticationService;
import com.example.userservice.grpc.UserResponse;
import jakarta.servlet.http.Cookie;
import java.lang.reflect.Field;
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
  private AuthenticationService authService;
  private JwtProvider jwtProvider;
  private UserGrpcClient userGrpcClient;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
    authService = mock(AuthenticationService.class);
    jwtProvider = mock(JwtProvider.class);
    userGrpcClient = mock(UserGrpcClient.class);

    AuthController controller = new AuthController(authService, jwtProvider, userGrpcClient);
    setField(controller, "accessTokenValidityMs", 3600000L);
    setField(controller, "refreshTokenValidityMs", 1209600000L);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new RestExceptionHandler())
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  void loginDocumentsSuccessfulAuthentication() throws Exception {
    TokenResponse tokens = new TokenResponse("access-token-value", "refresh-token-value");
    LoginResult result = new LoginResult(tokens, "user-1", "user@example.com", "Tester", "USER");

    when(authService.login(eq("user@example.com"), eq("password123"))).thenReturn(result);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "user@example.com",
                      "password": "password123"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("user-1"))
        .andExpect(jsonPath("$.data.role").value("USER"))
        .andExpect(cookie().value("access-token", "access-token-value"))
        .andExpect(cookie().value("refresh-token", "refresh-token-value"))
        .andDo(
            document(
                "auth-login-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").description("User email for login"),
                    fieldWithPath("password").description("User password for login")),
                responseHeaders(
                    headerWithName("Set-Cookie").description("access-token and refresh-token httpOnly cookies")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data.id").description("User id"),
                    fieldWithPath("data.email").description("User email"),
                    fieldWithPath("data.name").description("User name"),
                    fieldWithPath("data.role").description("User role"))));
  }

  @Test
  void signupDocumentsSuccessfulRegistration() throws Exception {
    TokenResponse tokens = new TokenResponse("signup-access-token", "signup-refresh-token");
    LoginResult result = new LoginResult(tokens, "user-2", "new@example.com", "New User", "USER");

    when(authService.signup(eq("new@example.com"), eq("password123"), eq("New User"))).thenReturn(result);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "new@example.com",
                      "password": "password123",
                      "name": "New User"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.email").value("new@example.com"))
        .andExpect(cookie().value("access-token", "signup-access-token"))
        .andExpect(cookie().value("refresh-token", "signup-refresh-token"))
        .andDo(
            document(
                "auth-signup-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").description("Email for signup"),
                    fieldWithPath("password").description("Password for signup"),
                    fieldWithPath("name").description("Name for signup")),
                responseHeaders(
                    headerWithName("Set-Cookie").description("access-token and refresh-token httpOnly cookies")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data.id").description("Created user id"),
                    fieldWithPath("data.email").description("Created user email"),
                    fieldWithPath("data.name").description("Created user name"),
                    fieldWithPath("data.role").description("Created user role"))));
  }

  @Test
  void refreshDocumentsSuccessfulTokenRefresh() throws Exception {
    when(authService.refresh(eq("refresh-token-value")))
        .thenReturn(new TokenResponse("refreshed-access-token", "refreshed-refresh-token"));
    when(jwtProvider.getUserIdFromToken("refreshed-access-token")).thenReturn("user-3");
    when(jwtProvider.getRolesFromToken("refreshed-access-token")).thenReturn("STORE");
    when(userGrpcClient.getUserById("user-3"))
        .thenReturn(
            UserResponse.newBuilder()
                .setFound(true)
                .setUserId("user-3")
                .setEmail("store@example.com")
                .setName("Store Owner")
                .setRoles("STORE")
                .build());

    mockMvc
        .perform(post("/api/v1/auth/refresh").cookie(new Cookie("refresh-token", "refresh-token-value")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value("user-3"))
        .andExpect(jsonPath("$.data.role").value("STORE"))
        .andExpect(cookie().value("access-token", "refreshed-access-token"))
        .andExpect(cookie().value("refresh-token", "refreshed-refresh-token"))
        .andDo(
            document(
                "auth-refresh-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseHeaders(
                    headerWithName("Set-Cookie").description("refreshed access-token and refresh-token cookies")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data.id").description("User id from token"),
                    fieldWithPath("data.email").description("User email"),
                    fieldWithPath("data.name").description("User name"),
                    fieldWithPath("data.role").description("User role"))));
  }

  @Test
  void refreshDocumentsMissingCookieError() throws Exception {
    mockMvc
        .perform(post("/api/v1/auth/refresh"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400))
        .andDo(
            document(
                "auth-refresh-missing-cookie",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Error message"),
                    fieldWithPath("data").description("null on failure"))));
  }

  @Test
  void logoutDocumentsCookieCleanup() throws Exception {
    doNothing().when(authService).logout("refresh-token-value");

    mockMvc
        .perform(post("/api/v1/auth/logout").cookie(new Cookie("refresh-token", "refresh-token-value")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(cookie().maxAge("access-token", 0))
        .andExpect(cookie().maxAge("refresh-token", 0))
        .andDo(
            document(
                "auth-logout-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseHeaders(
                    headerWithName("Set-Cookie").description("expired access-token and refresh-token cookies")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data").description("always null"))));
  }

  @Test
  void socialLoginDocumentsSuccessfulFlow() throws Exception {
    TokenResponse tokens = new TokenResponse("social-access-token", "social-refresh-token");
    LoginResult result = new LoginResult(tokens, "user-4", "social@example.com", "Social User", "USER");

    when(authService.socialLogin(eq("google"), eq("google-id-token"))).thenReturn(result);

    mockMvc
        .perform(post("/api/v1/auth/social?provider=google&token=google-id-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.email").value("social@example.com"))
        .andExpect(cookie().value("access-token", "social-access-token"))
        .andExpect(cookie().value("refresh-token", "social-refresh-token"))
        .andDo(
            document(
                "auth-social-login-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("provider").description("Social login provider name"),
                    parameterWithName("token").description("Provider-issued login token")),
                responseHeaders(
                    headerWithName("Set-Cookie").description("access-token and refresh-token httpOnly cookies")),
                responseFields(
                    fieldWithPath("success").description("Whether request succeeded"),
                    fieldWithPath("code").description("Response code"),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data.id").description("User id"),
                    fieldWithPath("data.email").description("User email"),
                    fieldWithPath("data.name").description("User name"),
                    fieldWithPath("data.role").description("User role"))));
  }

  @Test
  void loginDocumentsValidationError() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/login")
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
                "auth-login-validation-error",
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

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = AuthController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
