package com.example.event.service;

import com.example.event.dto.EventDto;
import com.example.event.entity.Event;
import com.example.event.repository.EventRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** EventService 타입입니다. */
@Service
public class EventService {
  private final EventRepository eventRepository;

  public EventService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @Transactional
  public EventDto createEvent(String type, String payload) {
    Event e =
        Event.builder()
            .id(java.util.UUID.randomUUID().toString())
            .type(type)
            .payload(payload)
            .createdAt(Instant.now())
            .isNewEntity(true)
            .build();
    Event saved = eventRepository.save(e);
    return new EventDto(saved.getId(), saved.getType(), saved.getPayload(), saved.getCreatedAt());
  }

  public Optional<EventDto> findById(String id) {
    return eventRepository
        .findById(id)
        .map(e -> new EventDto(e.getId(), e.getType(), e.getPayload(), e.getCreatedAt()));
  }
}
