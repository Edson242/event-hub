package com.edson.eventHub.dto;

public record TicketValidationResponseDTO(
    boolean valid,
    String message,
    String participantName,
    String ticketType
) {}