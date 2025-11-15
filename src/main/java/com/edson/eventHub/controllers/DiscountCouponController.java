package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.DiscountCoupon;
import com.edson.eventHub.services.DiscountCouponService;

@RestController
@RequestMapping("/discount-coupons")
public class DiscountCouponController {
    private final DiscountCouponService discountCouponService;
    private static final Logger logger = LoggerFactory.getLogger(DiscountCouponController.class);

    public DiscountCouponController(DiscountCouponService discountCouponService) {
        this.discountCouponService = discountCouponService;
    }

    @GetMapping
    public ResponseEntity<List<DiscountCoupon>> getAll() {
        logger.info("Buscando todos os cupons de desconto");
        try {
            List<DiscountCoupon> coupons = discountCouponService.findAll();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os cupons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountCoupon> getById(@PathVariable Long id) {
        logger.info("Buscando cupom com ID: {}", id);
        try {
            Optional<DiscountCoupon> coupon = discountCouponService.findById(id);
            if (coupon.isPresent()) {
                logger.info("Cupom com ID: {} encontrado.", id);
                return ResponseEntity.ok(coupon.get());
            } else {
                logger.warn("Cupom com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar cupom com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<DiscountCoupon> create(@RequestBody DiscountCoupon discountCoupon) {
        logger.info("Criando novo cupom com código: {}", discountCoupon.getCode());
        try {
            DiscountCoupon savedCoupon = discountCouponService.save(discountCoupon);
            logger.info("Cupom criado com sucesso. ID: {}", savedCoupon.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
        } catch (Exception e) {
            logger.error("Erro ao criar cupom com código: {}", discountCoupon.getCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountCoupon> update(@PathVariable Long id, @RequestBody DiscountCoupon discountCouponDetails) {
        logger.info("Iniciando atualização para cupom com ID: {}", id);
        try {
            Optional<DiscountCoupon> couponOpt = discountCouponService.findById(id);
            
            if (!couponOpt.isPresent()) {
                logger.warn("Falha ao atualizar. Cupom com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }

            DiscountCoupon coupon = couponOpt.get();
            // Atualiza os campos
            coupon.setCode(discountCouponDetails.getCode());
            coupon.setDiscountValue(discountCouponDetails.getDiscountValue());
            coupon.setDiscountType(discountCouponDetails.getDiscountType());
            coupon.setValidUntil(discountCouponDetails.getValidUntil());
            coupon.setMaxUses(discountCouponDetails.getMaxUses());
            coupon.setUses(discountCouponDetails.getUses());

            DiscountCoupon updated = discountCouponService.save(coupon);
            logger.info("Cupom com ID: {} atualizado com sucesso.", updated.getId());
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            logger.error("Erro ao atualizar cupom com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do cupom com ID: {}", id);
        try {
            if (!discountCouponService.findById(id).isPresent()) {
                logger.warn("Falha ao excluir. Cupom com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            }
            
            discountCouponService.deleteById(id);
            logger.info("Cupom com ID: {} excluído com sucesso.", id);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (Exception e) {
            logger.error("Erro ao excluir cupom com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}