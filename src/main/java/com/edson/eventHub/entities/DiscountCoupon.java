package com.edson.eventHub.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime; // MUDANÇA
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp; // ADICIONADO

import com.edson.eventHub.enums.DiscountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "discount_coupons")
@Getter // ADICIONADO
@Setter // ADICIONADO
@NoArgsConstructor // ADICIONADO
public class DiscountCoupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    /**
     * MUDANÇA: Usando Enum para segurança de tipo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 10)
    private DiscountType discountType; 

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "max_uses", columnDefinition = "integer default 0")
    private Integer maxUses; 

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer uses = 0;

    /**
     * MUDANÇA: Corrigido para OffsetDateTime e @CreationTimestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * MELHORIA: Campo adicionado para consistência do schema.
     * (Requer alteração no seu banco de dados)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // O método onCreate() e o @PrePersist foram removidos.
    // Os getters/setters manuais foram removidos em favor do Lombok.
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountCoupon that = (DiscountCoupon) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}