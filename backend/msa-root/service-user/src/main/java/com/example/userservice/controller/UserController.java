package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.security.RequireRole;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/users")
/** REST controller exposing user management endpoints. */
/** UserController 타입입니다. */
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody @Valid CreateUserRequest req) {
    UserDto dto = userService.register(req);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping
  @RequireRole({"ADMIN"})
  public ResponseEntity<ApiResponse<UserDto>> getByEmail(@RequestParam String email) {
    UserDto dto = userService.findByEmail(email);
    if (dto == null) {
      return ResponseEntity.status(404).body(ApiResponse.error(404, "Not found"));
    }
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping("/{id}/profile")
  public ResponseEntity<ApiResponse<com.example.userservice.dto.UserProfileDto>> getProfile(
      @PathVariable String id,
      @RequestHeader("X-User-Id") String requesterId,
      @RequestHeader("X-User-Role") String requesterRole) {
    ensureProfileAccess(id, requesterId, requesterRole);
    com.example.userservice.dto.UserProfileDto dto = userService.getProfile(id);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @PutMapping("/{id}/profile")
  public ResponseEntity<ApiResponse<com.example.userservice.dto.UserProfileDto>> updateProfile(
      @PathVariable String id,
      @RequestHeader("X-User-Id") String requesterId,
      @RequestHeader("X-User-Role") String requesterRole,
      @RequestBody com.example.userservice.dto.UpdateProfileRequest req) {
    ensureProfileAccess(id, requesterId, requesterRole);
    com.example.userservice.dto.UserProfileDto dto = userService.updateProfile(id, req);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<com.example.userservice.dto.UserProfileDto>> uploadAvatar(
      @PathVariable String id,
      @RequestHeader("X-User-Id") String requesterId,
      @RequestHeader("X-User-Role") String requesterRole,
      @RequestPart("file") MultipartFile file) {
    ensureProfileAccess(id, requesterId, requesterRole);
    com.example.userservice.dto.UserProfileDto dto = userService.updateAvatar(id, file);
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping("/avatars/{filename:.+}")
  public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
    // Binary file response endpoint: intentionally not wrapped by ApiResponse.
    try {
      Resource resource = userService.loadAvatar(filename);
      String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());
      MediaType mediaType =
          contentType != null
              ? MediaType.parseMediaType(contentType)
              : MediaType.APPLICATION_OCTET_STREAM;
      return ResponseEntity.ok()
          .contentType(mediaType)
          .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
          .body(resource);
    } catch (NoSuchElementException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  @RequireRole({"ADMIN"})
  public ResponseEntity<ApiResponse<Object>> withdraw(@PathVariable String id) {
    userService.withdraw(id);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  private void ensureProfileAccess(String targetUserId, String requesterId, String requesterRole) {
    if ("ADMIN".equals(requesterRole)) {
      return;
    }
    if (!targetUserId.equals(requesterId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }
  }
}
