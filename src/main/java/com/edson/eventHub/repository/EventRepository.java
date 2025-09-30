package com.edson.eventHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edson.eventHub.entities.Event;

// EventRepository.java
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
