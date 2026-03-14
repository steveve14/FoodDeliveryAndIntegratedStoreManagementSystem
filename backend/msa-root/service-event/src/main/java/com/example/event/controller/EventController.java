package com.example.event.controller;

import com.example.event.dto.ApiResponse;
import com.example.event.dto.EventDto;
import com.example.event.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** EventController 타입입니다. */
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

  private final EventService eventService;

  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<EventDto>> create(
      @RequestBody @Valid com.example.event.dto.CreateEventRequest req) {
    EventDto dto = eventService.createEvent(req.getType(), req.getPayload());
    return ResponseEntity.ok(ApiResponse.ok(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EventDto>> get(@PathVariable String id) {
    return eventService
        .findById(id)
        .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
        .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error(404, "Not found")));
  }
}
