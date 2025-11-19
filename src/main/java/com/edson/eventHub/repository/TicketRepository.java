package com.edson.eventHub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edson.eventHub.entities.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	long countByEventId(Long id);

	Optional<Ticket> findByTicketCode(String ticketCode);
}
