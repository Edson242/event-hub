package com.edson.eventHub.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.dto.BuyTicketRequestDTO;
import com.edson.eventHub.dto.TicketValidationRequestDTO;
import com.edson.eventHub.dto.TicketValidationResponseDTO;
import com.edson.eventHub.entities.Ticket;
import com.edson.eventHub.services.TicketService;

import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar tickets.
 * Fornece endpoints para comprar, validar e gerenciar tickets.
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {
    
    private final TicketService ticketService;
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * GET /tickets : Recupera todos os tickets. (Somente Admin)
     *
     * @return um ResponseEntity com a lista de todos os tickets.
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAll() {
        logger.info("Buscando todos os tickets");
        return ResponseEntity.ok(ticketService.findAll());
    }

    /**
     * GET /tickets/{id} : Recupera um ticket específico pelo seu ID. (Somente Admin)
     *
     * @param id o ID do ticket a ser recuperado.
     * @return um ResponseEntity com o ticket, ou 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        logger.info("Buscando ticket com ID: {}", id);
        return ticketService.findById(id)
                .map(ticket -> {
                    logger.info("Ticket com ID: {} encontrado.", id);
                    return ResponseEntity.ok(ticket);
                })
                .orElseGet(() -> {
                    logger.warn("Ticket com ID: {} não encontrado.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * POST /tickets/buy : Compra um ticket para um usuário.
     *
     * @param request DTO contendo o ID do usuário e o ID do tipo de ticket.
     * @return um ResponseEntity com o ticket criado, ou um bad request se a compra falhar.
     */
    @PostMapping("/buy")
    public ResponseEntity<?> buy(@Valid @RequestBody BuyTicketRequestDTO request) {
        logger.info("Iniciando compra. User ID: {}, Tipo: {}", request.userId(), request.ticketTypeId());
        
        try {
            Ticket savedTicket = ticketService.buyTicket(request.ticketTypeId(), request.userId());
            logger.info("Ticket comprado com sucesso. ID: {}", savedTicket.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
            
        } catch (RuntimeException e) {
            logger.warn("Tentativa de compra falhou: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * POST /tickets/validate : Valida um ticket para um evento.
     *
     * @param request DTO contendo o código do ticket e o ID do evento.
     * @return um ResponseEntity com o resultado da validação.
     */
    @PostMapping("/validate")
    public ResponseEntity<TicketValidationResponseDTO> validate(@Valid @RequestBody TicketValidationRequestDTO request) {
        logger.info("Iniciando validação para o ticket com código: {}", request.ticketCode());
        TicketValidationResponseDTO response = ticketService.validateTicket(request);
        if (response.valid()) {
            logger.info("Validação do ticket {} bem-sucedida.", request.ticketCode());
        } else {
            logger.warn("Validação do ticket {} falhou: {}", request.ticketCode(), response.message());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /tickets/{id} : Atualiza um ticket. (Somente Admin)
     *
     * @param id o ID do ticket a ser atualizado.
     * @param ticketDetails os dados atualizados do ticket.
     * @return um ResponseEntity com o ticket atualizado, ou 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @Valid @RequestBody Ticket ticketDetails) {
        logger.info("Iniciando atualização do ticket com ID: {}", id);
        return ticketService.findById(id)
            .map(ticket -> {
                ticket.setEvent(ticketDetails.getEvent());
                ticket.setParticipant(ticketDetails.getParticipant());
                ticket.setTicketCode(ticketDetails.getTicketCode());
                ticket.setStatus(ticketDetails.getStatus());
                Ticket updatedTicket = ticketService.save(ticket);
                logger.info("Ticket com ID: {} atualizado com sucesso.", id);
                return ResponseEntity.ok(updatedTicket);
            })
            .orElseGet(() -> {
                logger.warn("Falha ao atualizar. Ticket com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * DELETE /tickets/{id} : Deleta um ticket pelo seu ID. (Somente Admin)
     *
     * @param id o ID do ticket a ser deletado.
     * @return um ResponseEntity com status 204 (No Content) ou 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do ticket com ID: {}", id);
        if (ticketService.findById(id).isPresent()) {
            ticketService.deleteById(id);
            logger.info("Ticket com ID: {} excluído com sucesso.", id);
            return ResponseEntity.noContent().build();
        }
        logger.warn("Falha ao excluir. Ticket com ID: {} não encontrado.", id);
        return ResponseEntity.notFound().build();
    }
}