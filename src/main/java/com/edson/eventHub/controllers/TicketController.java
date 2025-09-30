package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.Ticket;
import com.edson.eventHub.services.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAll() {
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        Optional<Ticket> ticket = ticketService.findById(id);
        return ticket.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ticket create(@RequestBody Ticket ticket) {
        return ticketService.save(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        Optional<Ticket> ticketOpt = ticketService.findById(id);
        if (!ticketOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Ticket ticket = ticketOpt.get();
        ticket.setEvent(ticketDetails.getEvent());
        ticket.setParticipant(ticketDetails.getParticipant());
        ticket.setTicketCode(ticketDetails.getTicketCode());
        ticket.setStatus(ticketDetails.getStatus());

        Ticket updated = ticketService.save(ticket);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ticketService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ticketService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
