package com.edson.eventHub.entities.dto;

import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class DiscountCouponDTO {
    private Long id;
    private String code;
    private BigDecimal discountValue;
    private String discountType; // "percent" ou "fixed"
    private LocalDate validUntil;
    private Integer maxUses;
    private Integer uses;
}
