package com.example.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.event.entity.Event;
import com.example.event.repository.EventRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock private EventRepository eventRepository;

  @InjectMocks private EventService eventService;

  @Test
  void createEventPersistsAndReturnsDto() {
    when(eventRepository.save(any(Event.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, Event.class));

    var result = eventService.createEvent("ORDER_CREATED", "{\"orderId\":\"1\"}");

    assertThat(result.getType()).isEqualTo("ORDER_CREATED");
    assertThat(result.getPayload()).contains("orderId");

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
    verify(eventRepository).save(captor.capture());
    assertThat(captor.getValue().getCreatedAt()).isNotNull();
  }

  @Test
  void findByIdMapsEntityToDto() {
    Instant createdAt = Instant.parse("2026-03-13T00:00:00Z");
    Event event =
        Event.builder()
            .id("event-1")
            .type("ORDER_CREATED")
            .payload("payload")
            .createdAt(createdAt)
            .isNewEntity(false)
            .build();

    when(eventRepository.findById("event-1")).thenReturn(Optional.of(event));

    var result = eventService.findById("event-1");

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo("event-1");
    assertThat(result.get().getCreatedAt()).isEqualTo(createdAt);
  }
}
