package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.Ticket;
import com.edson.eventHub.services.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    
    private final TicketService ticketService;
    // LOG 1: Declaração do Logger
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAll() {
        logger.info("Buscando todos os tickets");
        try {
            List<Ticket> tickets = ticketService.findAll();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os tickets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        logger.info("Buscando ticket com ID: {}", id);
        try {
            Optional<Ticket> ticket = ticketService.findById(id);
            if (ticket.isPresent()) {
                logger.info("Ticket com ID: {} encontrado.", id);
                return ResponseEntity.ok(ticket.get());
            } else {
                logger.warn("Ticket com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar ticket com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
        // Loga o código, que é um identificador útil
        logger.info("Criando novo ticket com código: {}", ticket.getTicketCode());
        try {
            Ticket savedTicket = ticketService.save(ticket);
            logger.info("Ticket criado com sucesso. ID: {}", savedTicket.getId());
            // Retorna 201 Created (correto para POST)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
        } catch (Exception e) {
            logger.error("Erro ao criar ticket com código: {}", ticket.getTicketCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        logger.info("Iniciando atualização para ticket com ID: {}", id);
        try {
            Optional<Ticket> ticketOpt = ticketService.findById(id);
            
            if (!ticketOpt.isPresent()) {
                logger.warn("Falha ao atualizar. Ticket com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }

            Ticket ticket = ticketOpt.get();
            ticket.setEvent(ticketDetails.getEvent());
            ticket.setParticipant(ticketDetails.getParticipant());
            ticket.setTicketCode(ticketDetails.getTicketCode());
            ticket.setStatus(ticketDetails.getStatus());

            Ticket updated = ticketService.save(ticket);
            logger.info("Ticket com ID: {} atualizado com sucesso.", updated.getId());
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            logger.error("Erro ao atualizar ticket com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do ticket com ID: {}", id);
        try {
            if (!ticketService.findById(id).isPresent()) {
                logger.warn("Falha ao excluir. Ticket com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
            
            ticketService.deleteById(id);
            logger.info("Ticket com ID: {} excluído com sucesso.", id);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (Exception e) {
            logger.error("Erro ao excluir ticket com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}