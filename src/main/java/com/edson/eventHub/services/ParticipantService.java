package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edson.eventHub.entities.Participant;
import com.edson.eventHub.repository.ParticipantRepository;

/**
 * Classe de serviço para gerenciar participantes de eventos.
 * Esta classe fornece métodos para lidar com a lógica de negócio relacionada aos participantes.
 */
@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    /**
     * Constrói um ParticipantService com o repositório necessário.
     *
     * @param participantRepository O repositório para acesso aos dados de participantes.
     */
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    /**
     * Recupera todos os participantes.
     *
     * @return uma lista de todos os participantes.
     */
    public List<Participant> findAll() {
        return participantRepository.findAll();
    }

    /**
     * Encontra um participante pelo seu ID.
     *
     * @param id O ID do participante a ser encontrado.
     * @return um Optional contendo o participante encontrado, ou um Optional vazio se não for encontrado.
     */
    public Optional<Participant> findById(Long id) {
        return participantRepository.findById(id);
    }

    /**
     * Salva um novo participante ou atualiza um existente.
     *
     * @param participant A entidade de participante a ser salva.
     * @return a entidade de participante salva.
     */
    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }

    /**
     * Deleta um participante pelo seu ID.
     *
     * @param id O ID do participante a ser deletado.
     */
    public void deleteById(Long id) {
        participantRepository.deleteById(id);
    }
}
