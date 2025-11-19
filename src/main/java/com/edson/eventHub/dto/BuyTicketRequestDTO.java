package com.edson.eventHub.dto;

public record BuyTicketRequestDTO(
    Long ticketTypeId,
    Long userId
) {}