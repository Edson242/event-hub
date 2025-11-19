package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.Event;
import com.edson.eventHub.services.EventService;

import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar eventos.
 * Fornece endpoints para criar, recuperar, atualizar e deletar eventos.
 */
@RestController
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * GET /events : Recupera todos os eventos.
     *
     * @return um ResponseEntity com status 200 (OK) e a lista de todos os eventos no corpo.
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        logger.info("Buscando todos os eventos");
        List<Event> events = eventService.findAll();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /events/{id} : Recupera um evento específico pelo seu ID.
     *
     * @param id o ID do evento a ser recuperado.
     * @return um ResponseEntity com status 200 (OK) e o evento no corpo, ou 404 (Not Found) se o evento não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        logger.info("Buscando evento com ID: {}", id);
        Optional<Event> event = eventService.findById(id);
        if (event.isPresent()) {
            logger.info("Evento com ID: {} encontrado.", id);
            return ResponseEntity.ok(event.get());
        } else {
            // O log de warning agora é tratado pelo GlobalExceptionHandler ao lançar EntityNotFoundException
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /events : Cria um novo evento.
     *
     * @param event o objeto de evento a ser criado, passado no corpo da requisição. Deve ser válido.
     * @return um ResponseEntity com status 201 (Created) e o evento recém-criado no corpo.
     */
    @PostMapping
    public ResponseEntity<Event> create(@Valid @RequestBody Event event) {
        logger.info("Criando novo evento com título: {}", event.getTitle());
        Event savedEvent = eventService.save(event);
        logger.info("Evento criado com sucesso. ID: {}", savedEvent.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    /**
     * PUT /events/{id} : Atualiza um evento existente.
     *
     * @param id o ID do evento a ser atualizado.
     * @param eventDetails o objeto de evento atualizado, passado no corpo da requisição. Deve ser válido.
     * @return um ResponseEntity com status 200 (OK) e o evento atualizado no corpo.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody Event eventDetails) {
        logger.info("Iniciando atualização para evento com ID: {}", id);
        Event updatedEvent = eventService.update(id, eventDetails);
        logger.info("Evento com ID: {} atualizado com sucesso.", updatedEvent.getId());
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * DELETE /events/{id} : Deleta um evento pelo seu ID.
     *
     * @param id o ID do evento a ser deletado.
     * @return um ResponseEntity com status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do evento com ID: {}", id);
        eventService.deleteById(id);
        logger.info("Evento com ID: {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build();
    }
}