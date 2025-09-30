package com.edson.eventHub.entities.dto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private String location;
    private Integer maxParticipants;
}