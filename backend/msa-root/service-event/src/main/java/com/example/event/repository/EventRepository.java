package com.example.event.repository;

import com.example.event.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository for Event entities.
 */
public interface EventRepository extends CrudRepository<Event, String> {

}
