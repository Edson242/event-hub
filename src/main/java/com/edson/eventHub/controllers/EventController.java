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

@RestController
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    // LOG 1: Declaração do Logger
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        logger.info("Buscando todos os eventos");
        try {
            List<Event> events = eventService.findAll();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os eventos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        logger.info("Buscando evento com ID: {}", id);
        try {
            Optional<Event> event = eventService.findById(id);
            if (event.isPresent()) {
                logger.info("Evento com ID: {} encontrado.", id);
                return ResponseEntity.ok(event.get());
            } else {
                logger.warn("Evento com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar evento com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        logger.info("Criando novo evento com título: {}", event.getTitle());
        try {
            Event savedEvent = eventService.save(event);
            logger.info("Evento criado com sucesso. ID: {}", savedEvent.getId());
            // Retorna 201 Created (correto para POST)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } catch (Exception e) {
            // Loga o título para saber qual evento falhou
            logger.error("Erro ao criar evento: {}", event.getTitle(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event eventDetails) {
        logger.info("Iniciando atualização para evento com ID: {}", id);
        try {
            Optional<Event> eventOpt = eventService.findById(id);
            
            if (!eventOpt.isPresent()) {
                logger.warn("Falha ao atualizar. Evento com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }

            Event event = eventOpt.get();
            event.setTitle(eventDetails.getTitle());
            event.setDescription(eventDetails.getDescription());
            event.setEventDate(eventDetails.getEventDate());
            event.setLocation(eventDetails.getLocation());
            event.setMaxParticipants(eventDetails.getMaxParticipants());

            Event updated = eventService.save(event);
            logger.info("Evento com ID: {} atualizado com sucesso.", updated.getId());
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            logger.error("Erro ao atualizar evento com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do evento com ID: {}", id);
        try {
            if (!eventService.findById(id).isPresent()) {
                logger.warn("Falha ao excluir. Evento com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
            
            eventService.deleteById(id);
            logger.info("Evento com ID: {} excluído com sucesso.", id);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (Exception e) {
            logger.error("Erro ao excluir evento com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}