package com.edson.eventHub.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.edson.eventHub.enums.Role;

import java.time.OffsetDateTime; // MUDANÇA: de LocalDateTime para OffsetDateTime
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Getter // MUDANÇA: Substituí @Data
@Setter // MUDANÇA: Substituí @Data
@NoArgsConstructor // MUDANÇA: Adicionado construtor padrão
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // MUDANÇA: de Integer para Long

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) // Boa prática: definir 'nullable = false' para Role
    private Role role;

    @CreationTimestamp // MUDANÇA: Mais limpo que @PrePersist
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt; // MUDANÇA: Tipo de dado corrigido

    @UpdateTimestamp // MUDANÇA: Campo 'updated_at' que faltava
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt; // MUDANÇA: Tipo de dado corrigido
    
    @OneToMany(mappedBy = "user")
    private List<Participant> participations;

    // (O @PrePersist foi removido pois @CreationTimestamp faz o trabalho)
    
    //-----------------------------------------------------//
    // MÉTODOS OBRIGATÓRIOS DA INTERFACE UserDetails
    //-----------------------------------------------------//
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Se 'role' puder ser nulo, você precisa tratar isso aqui
        if (this.role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // Padrão, pode ser customizado depois com campos no banco (ex: isEnabled)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // MUDANÇA: Implementação segura de equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}