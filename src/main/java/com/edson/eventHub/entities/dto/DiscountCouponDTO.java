package com.edson.eventHub.entities.dto;

import java.time.LocalDate;

import com.edson.eventHub.enums.DiscountType;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DiscountCouponDTO {
    private Long id;
    private String code;
    private BigDecimal discountValue;
    private DiscountType discountType; 
    private LocalDate validUntil;
    private Integer maxUses;
    private Integer uses;
}
