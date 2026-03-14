package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.FrontendMailDto;
import com.example.userservice.dto.FrontendMemberDto;
import com.example.userservice.dto.FrontendNotificationDto;
import com.example.userservice.dto.FrontendUserDto;
import com.example.userservice.service.FrontendDataService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** FrontendDataController 타입입니다. */
@RestController
@RequestMapping("/api/v1/users/frontend")
public class FrontendDataController {

  private final FrontendDataService frontendDataService;

  /** 프런트엔드 데이터 컨트롤러를 생성합니다. */
  public FrontendDataController(FrontendDataService frontendDataService) {
    this.frontendDataService = frontendDataService;
  }

  /** 고객 목록 데이터를 조회합니다. */
  @GetMapping("/customers")
  public ResponseEntity<ApiResponse<List<FrontendUserDto>>> getCustomers() {
    return ResponseEntity.ok(ApiResponse.ok(frontendDataService.getCustomers()));
  }

  @GetMapping("/members")
  public ResponseEntity<ApiResponse<List<FrontendMemberDto>>> getMembers() {
    return ResponseEntity.ok(ApiResponse.ok(frontendDataService.getMembers()));
  }

  @GetMapping("/mails")
  public ResponseEntity<ApiResponse<List<FrontendMailDto>>> getMails() {
    return ResponseEntity.ok(ApiResponse.ok(frontendDataService.getMails()));
  }

  @GetMapping("/notifications")
  public ResponseEntity<ApiResponse<List<FrontendNotificationDto>>> getNotifications() {
    return ResponseEntity.ok(ApiResponse.ok(frontendDataService.getNotifications()));
  }
}
