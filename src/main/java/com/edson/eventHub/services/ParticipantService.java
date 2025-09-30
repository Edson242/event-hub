package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edson.eventHub.entities.Participant;
import com.edson.eventHub.repository.ParticipantRepository;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> findAll() {
        return participantRepository.findAll();
    }

    public Optional<Participant> findById(Long id) {
        return participantRepository.findById(id);
    }

    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }

    public void deleteById(Long id) {
        participantRepository.deleteById(id);
    }
}
