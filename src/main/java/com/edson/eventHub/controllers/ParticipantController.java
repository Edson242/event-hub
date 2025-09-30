package com.edson.eventHub.controllers;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.Participant;
import com.edson.eventHub.services.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public List<Participant> getAll() {
        return participantService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        Optional<Participant> participant = participantService.findById(id);
        return participant.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Participant create(@RequestBody Participant participant) {
        return participantService.save(participant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participant> update(@PathVariable Long id, @RequestBody Participant participantDetails) {
        Optional<Participant> participantOpt = participantService.findById(id);
        if (!participantOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Participant participant = participantOpt.get();
        participant.setName(participantDetails.getName());
        participant.setEmail(participantDetails.getEmail());
        participant.setPhone(participantDetails.getPhone());

        Participant updated = participantService.save(participant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!participantService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        participantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
