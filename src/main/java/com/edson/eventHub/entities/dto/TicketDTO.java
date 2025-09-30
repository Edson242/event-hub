package com.edson.eventHub.entities.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private Long eventId;
    private Long participantId;
    private String ticketCode;
    private String status; // "ativo", "usado", "cancelado"
    private LocalDateTime issuedAt;
}
