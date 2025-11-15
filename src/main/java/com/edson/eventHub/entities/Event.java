package com.edson.eventHub.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime; // MUDANÇA (import removido de LocalDateTime)
import java.util.Objects; // ADICIONADO

@Getter
@Setter
@NoArgsConstructor // Boa prática adicionar
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_time", nullable = false)
    private OffsetDateTime eventDate; // Isto estava correto!

    @Column
    private String location;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    /**
     * MUDANÇA: Corrigido para OffsetDateTime e @CreationTimestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * MUDANÇA: Corrigido para OffsetDateTime e @UpdateTimestamp
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Os métodos @PrePersist e @PreUpdate foram removidos.
    
    // Os getters e setters manuais foram removidos (Lombok cuida disso).

    // ADICIONADO: Boa prática de equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}