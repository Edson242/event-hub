package com.edson.eventHub.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime; // MUDANÇA
import java.util.List; // ADICIONADO
import java.util.Objects; // ADICIONADO

@Entity
@Table(name = "participants")
@Getter // ADICIONADO (Lombok)
@Setter // ADICIONADO (Lombok)
@NoArgsConstructor // ADICIONADO (Lombok)
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Relacionamento: Muitos participantes podem estar ligados a uma conta de usuário.
     * Nulo se for "guest checkout".
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    /**
     * MUDANÇA: Usando @CreationTimestamp para ser gerenciado pelo Hibernate
     * e OffsetDateTime para 'timestamp with time zone'.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * MUDANÇA: Campo 'updated_at' que faltava.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    
    /**
     * MELHORIA: Relacionamento inverso para ver todos os ingressos 
     * deste participante.
     */
    @OneToMany(mappedBy = "participant")
    private List<Ticket> tickets;

    // O método onCreate() e o @PrePersist foram removidos.
    // Os Getters/Setters manuais foram removidos em favor do Lombok.

    /**
     * MUDANÇA: Implementação segura de equals e hashCode
     * (Importante por causa do @OneToMany)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}