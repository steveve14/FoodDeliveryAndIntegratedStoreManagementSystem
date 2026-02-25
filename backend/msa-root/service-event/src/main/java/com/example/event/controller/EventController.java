package com.example.event.controller;

import com.example.event.dto.EventDto;
import com.example.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventDto> create(@RequestBody com.example.event.dto.CreateEventRequest req) {
        EventDto dto = eventService.createEvent(req.getType(), req.getPayload());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> get(@PathVariable Long id) {
        return eventService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
