package com.edson.eventHub.entities.dto;
import lombok.Data;

@Data
public class ParticipantDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
}