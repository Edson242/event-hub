package com.edson.eventHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edson.eventHub.entities.Participant;

// ParticipantRepository.java
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
