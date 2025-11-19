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

import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar participantes de eventos.
 * Nota: Este controlador é básico e pode exigir uma lógica mais complexa para um cenário do mundo real,
 * como vincular participantes a usuários e eventos.
 */
@RestController
@RequestMapping("/participants")
public class ParticipantController {
    
    private final ParticipantService participantService;
    private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * GET /participants : Recupera todos os participantes.
     *
     * @return um ResponseEntity com status 200 (OK) e a lista de todos os participantes no corpo.
     */
    @GetMapping
    public ResponseEntity<List<Participant>> getAll() {
        logger.info("Buscando todos os participantes");
        List<Participant> participants = participantService.findAll();
        return ResponseEntity.ok(participants);
    }

    /**
     * GET /participants/{id} : Recupera um participante específico pelo seu ID.
     *
     * @param id o ID do participante a ser recuperado.
     * @return um ResponseEntity com status 200 (OK) e o participante no corpo, ou 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        logger.info("Buscando participante com ID: {}", id);
        Optional<Participant> participant = participantService.findById(id);
        if (participant.isPresent()) {
            logger.info("Participante com ID: {} encontrado.", id);
            return ResponseEntity.ok(participant.get());
        } else {
            logger.warn("Participante com ID: {} não encontrado.", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /participants : Cria um novo participante.
     *
     * @param participant o objeto de participante a ser criado.
     * @return um ResponseEntity com status 201 (Created) e o novo participante no corpo.
     */
    @PostMapping
    public ResponseEntity<Participant> create(@Valid @RequestBody Participant participant) {
        logger.info("Criando novo participante com email: {}", participant.getEmail());
        Participant savedParticipant = participantService.save(participant);
        logger.info("Participante criado com sucesso. ID: {}", savedParticipant.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
    }

    /**
     * PUT /participants/{id} : Atualiza um participante existente.
     *
     * @param id o ID do participante a ser atualizado.
     * @param participantDetails os dados atualizados do participante.
     * @return um ResponseEntity com status 200 (OK) e o participante atualizado, ou 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participant> update(@PathVariable Long id, @Valid @RequestBody Participant participantDetails) {
        logger.info("Iniciando atualização para participante com ID: {}", id);
        return participantService.findById(id)
            .map(participant -> {
                participant.setName(participantDetails.getName());
                participant.setEmail(participantDetails.getEmail());
                participant.setPhone(participantDetails.getPhone());
                Participant updated = participantService.save(participant);
                logger.info("Participante com ID: {} atualizado com sucesso.", updated.getId());
                return ResponseEntity.ok(updated);
            })
            .orElseGet(() -> {
                logger.warn("Falha ao atualizar. Participante com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * DELETE /participants/{id} : Deleta um participante pelo seu ID.
     *
     * @param id o ID do participante a ser deletado.
     * @return um ResponseEntity com status 204 (No Content) ou 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do participante com ID: {}", id);
        if (!participantService.findById(id).isPresent()) {
            logger.warn("Falha ao excluir. Participante com ID: {} não encontrado.", id);
            return ResponseEntity.notFound().build();
        }
        participantService.deleteById(id);
        logger.info("Participante com ID: {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build();
    }
}