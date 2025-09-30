package com.edson.eventHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edson.eventHub.entities.Ticket;


// TicketRepository.java
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
