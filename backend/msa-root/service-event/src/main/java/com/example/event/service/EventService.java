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

  /** 이벤트 서비스 인스턴스를 생성합니다. */
  public EventService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /** 새로운 이벤트를 생성하고 저장합니다. */
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

  /** ID로 이벤트를 조회합니다. */
  public Optional<EventDto> findById(String id) {
    return eventRepository
        .findById(id)
        .map(e -> new EventDto(e.getId(), e.getType(), e.getPayload(), e.getCreatedAt()));
  }
}
