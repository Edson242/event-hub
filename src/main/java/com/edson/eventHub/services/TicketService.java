package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edson.eventHub.dto.TicketValidationRequestDTO;
import com.edson.eventHub.dto.TicketValidationResponseDTO;
import com.edson.eventHub.entities.Event;
import com.edson.eventHub.entities.Ticket;
import com.edson.eventHub.entities.TicketType;
import com.edson.eventHub.entities.User;
import com.edson.eventHub.enums.TicketStatus;
import com.edson.eventHub.repository.TicketRepository;
import com.edson.eventHub.repository.TicketTypeRepository;
import com.edson.eventHub.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Classe de serviço para lidar com a lógica de negócio relacionada a tickets.
 * Isso inclui comprar, validar e gerenciar tickets.
 */
@Service
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final UserRepository userRepository;

    /**
     * Constrói um TicketService com os repositórios necessários.
     *
     * @param ticketRepository      O repositório para acesso aos dados de tickets.
     * @param ticketTypeRepository  O repositório para acesso aos dados de tipos de ticket.
     * @param userRepository        O repositório para acesso aos dados de usuários.
     */
    public TicketService(TicketRepository ticketRepository, 
                         TicketTypeRepository ticketTypeRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Processa a compra de um ticket para um usuário e tipo de ticket específicos.
     * Este método é transacional e lida com o decremento de estoque e verificação de capacidade.
     *
     * @param ticketTypeId O ID do tipo de ticket sendo comprado.
     * @param userId O ID do usuário que está comprando o ticket.
     * @return o Ticket recém-criado e salvo.
     * @throws RuntimeException se os ingressos estiverem esgotados, o evento atingir a capacidade máxima, ou o tipo de ticket/usuário não for encontrado.
     */
    @Transactional 
    public Ticket buyTicket(Long ticketTypeId, Long userId) {
        int updated = ticketTypeRepository.decrementQuantity(ticketTypeId);
        if (updated == 0) {
            throw new RuntimeException("Ingressos esgotados para este tipo!");
        }

        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de ingresso não encontrado"));
        
        Event event = ticketType.getEvent(); 

        if (event.getMaxParticipants() != null) {
            long totalVendidos = ticketRepository.countByEventId(event.getId());
            
            if (totalVendidos >= event.getMaxParticipants()) {
                throw new RuntimeException("O evento atingiu a capacidade máxima de participantes!");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Ticket ticket = new Ticket();
        ticket.setTicketType(ticketType);
        ticket.setEvent(event);
        // FIXME: O participante não está sendo definido. O usuário que compra é o participante?
        ticket.setParticipant(null); 
        
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticket.setStatus(TicketStatus.ATIVO);

        return ticketRepository.save(ticket);
    }

    /**
     * Valida um ticket com base no seu código e no ID do evento.
     * Se o ticket for válido e ativo, seu status é alterado para USADO.
     *
     * @param request O DTO contendo o código do ticket e o ID do evento.
     * @return um DTO com o resultado da validação.
     */
    public TicketValidationResponseDTO validateTicket(TicketValidationRequestDTO request) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketCode(request.ticketCode());

        if (ticketOpt.isEmpty()) {
            return new TicketValidationResponseDTO(false, "Ticket não encontrado.", null, null);
        }

        Ticket ticket = ticketOpt.get();

        if (!ticket.getEvent().getId().equals(request.eventId())) {
            return new TicketValidationResponseDTO(false, "Ticket inválido para este evento.", null, null);
        }

        if (ticket.getStatus() == TicketStatus.CANCELADO) {
            return new TicketValidationResponseDTO(false, "Ticket CANCELADO.", ticket.getParticipant().getName(), ticket.getTicketType().getName());
        }

        if (ticket.getStatus() == TicketStatus.USADO) {
            return new TicketValidationResponseDTO(false, "Ticket JÁ UTILIZADO.", ticket.getParticipant().getName(), ticket.getTicketType().getName());
        }

        if (ticket.getStatus() == TicketStatus.ATIVO) {
            ticket.setStatus(TicketStatus.USADO);
            ticketRepository.save(ticket);
            
            return new TicketValidationResponseDTO(
                true, 
                "Acesso LIBERADO!", 
                ticket.getParticipant().getName(), 
                ticket.getTicketType().getName()
            );
        }
        
        return new TicketValidationResponseDTO(false, "Status desconhecido.", null, null);
    }

    /**
     * Recupera todos os tickets.
     *
     * @return uma lista de todos os tickets.
     */
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    /**
     * Encontra um ticket pelo seu ID.
     *
     * @param id O ID do ticket a ser encontrado.
     * @return um Optional contendo o ticket encontrado, ou um Optional vazio se não for encontrado.
     */
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     * Salva um novo ticket ou atualiza um existente.
     *
     * @param ticket A entidade de ticket a ser salva.
     * @return a entidade de ticket salva.
     */
    @Transactional
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /**
     * Deleta um ticket pelo seu ID.
     *
     * @param id O ID do ticket a ser deletado.
     */
    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }
}