package com.example.event.repository;

import com.example.event.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Event 엔티티를 조회하고 저장하는 저장소입니다. */
@Repository
public interface EventRepository extends CrudRepository<Event, String> {}
