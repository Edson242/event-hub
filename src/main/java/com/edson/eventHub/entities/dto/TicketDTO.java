package com.edson.eventHub.entities.dto;

import java.time.LocalDateTime;

import com.edson.eventHub.enums.TicketStatus;

import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private Long eventId;
    private Long participantId;
    private String ticketCode;
    private TicketStatus status;
    private LocalDateTime issuedAt;
}
