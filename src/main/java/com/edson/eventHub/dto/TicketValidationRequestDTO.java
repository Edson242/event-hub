package com.edson.eventHub.dto;

public record TicketValidationRequestDTO(
    String ticketCode,
    Long eventId
) {}