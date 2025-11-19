package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edson.eventHub.entities.Event;
import com.edson.eventHub.repository.EventRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Classe de serviço responsável pela lógica de negócio relacionada a eventos.
 * Lida com operações como criar, recuperar, atualizar e deletar eventos.
 */
@Service
public class EventService {
    private final EventRepository eventRepository;

    /**
     * Constrói um EventService com o repositório necessário.
     *
     * @param eventRepository O repositório para acesso aos dados de eventos.
     */
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Recupera todos os eventos do banco de dados.
     *
     * @return uma lista de todos os eventos.
     */
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    /**
     * Encontra um evento pelo seu ID.
     *
     * @param id O ID do evento a ser encontrado.
     * @return um Optional contendo o evento encontrado, ou um Optional vazio se não for encontrado.
     */
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    /**
     * Salva um novo evento ou atualiza um existente.
     *
     * @param event A entidade de evento a ser salva.
     * @return a entidade de evento salva.
     */
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Atualiza um evento existente com novos detalhes.
     *
     * @param id O ID do evento a ser atualizado.
     * @param eventDetails O objeto contendo os novos detalhes para o evento.
     * @return o evento atualizado.
     * @throws EntityNotFoundException se nenhum evento for encontrado com o ID fornecido.
     */
    @Transactional
    public Event update(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com o ID: " + id));

        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setEventDate(eventDetails.getEventDate());
        event.setLocation(eventDetails.getLocation());
        event.setMaxParticipants(eventDetails.getMaxParticipants());

        return eventRepository.save(event);
    }

    /**
     * Deleta um evento pelo seu ID.
     *
     * @param id O ID do evento a ser deletado.
     * @throws EntityNotFoundException se nenhum evento for encontrado com o ID fornecido.
     */
    public void deleteById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Evento não encontrado com o ID: " + id);
        }
        eventRepository.deleteById(id);
    }

	public EventRepository getEventRepository() {
		return eventRepository;
	}
}
