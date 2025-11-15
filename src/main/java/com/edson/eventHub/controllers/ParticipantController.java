package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.Participant;
import com.edson.eventHub.services.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantController {
    
    private final ParticipantService participantService;
    // LOG 1: Declaração do Logger
    private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public ResponseEntity<List<Participant>> getAll() {
        logger.info("Buscando todos os participantes");
        try {
            List<Participant> participants = participantService.findAll();
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os participantes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        logger.info("Buscando participante com ID: {}", id);
        try {
            Optional<Participant> participant = participantService.findById(id);
            if (participant.isPresent()) {
                logger.info("Participante com ID: {} encontrado.", id);
                return ResponseEntity.ok(participant.get());
            } else {
                logger.warn("Participante com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar participante com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Participant> create(@RequestBody Participant participant) {
        // Loga o email, que é um identificador único
        logger.info("Criando novo participante com email: {}", participant.getEmail());
        try {
            Participant savedParticipant = participantService.save(participant);
            logger.info("Participante criado com sucesso. ID: {}", savedParticipant.getId());
            // Retorna 201 Created (correto para POST)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
        } catch (Exception e) {
            logger.error("Erro ao criar participante com email: {}", participant.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participant> update(@PathVariable Long id, @RequestBody Participant participantDetails) {
        logger.info("Iniciando atualização para participante com ID: {}", id);
        try {
            Optional<Participant> participantOpt = participantService.findById(id);
            
            if (!participantOpt.isPresent()) {
                logger.warn("Falha ao atualizar. Participante com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }

            Participant participant = participantOpt.get();
            participant.setName(participantDetails.getName());
            participant.setEmail(participantDetails.getEmail());
            participant.setPhone(participantDetails.getPhone());

            Participant updated = participantService.save(participant);
            logger.info("Participante com ID: {} atualizado com sucesso.", updated.getId());
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            logger.error("Erro ao atualizar participante com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do participante com ID: {}", id);
        try {
            if (!participantService.findById(id).isPresent()) {
                logger.warn("Falha ao excluir. Participante com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
            
            participantService.deleteById(id);
            logger.info("Participante com ID: {} excluído com sucesso.", id);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (Exception e) {
            logger.error("Erro ao excluir participante com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}