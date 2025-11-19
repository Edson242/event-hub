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

import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar cupons de desconto.
 * Fornece endpoints para criar, recuperar, atualizar e deletar cupons de desconto.
 */
@RestController
@RequestMapping("/discount-coupons")
public class DiscountCouponController {
    private final DiscountCouponService discountCouponService;
    private static final Logger logger = LoggerFactory.getLogger(DiscountCouponController.class);

    public DiscountCouponController(DiscountCouponService discountCouponService) {
        this.discountCouponService = discountCouponService;
    }

    /**
     * GET /discount-coupons : Recupera todos os cupons de desconto.
     *
     * @return um ResponseEntity com status 200 (OK) e a lista de todos os cupons no corpo.
     */
    @GetMapping
    public ResponseEntity<List<DiscountCoupon>> getAll() {
        logger.info("Buscando todos os cupons de desconto");
        List<DiscountCoupon> coupons = discountCouponService.findAll();
        return ResponseEntity.ok(coupons);
    }

    /**
     * GET /discount-coupons/{id} : Recupera um cupom de desconto específico pelo seu ID.
     *
     * @param id o ID do cupom a ser recuperado.
     * @return um ResponseEntity com status 200 (OK) e o cupom no corpo, ou 404 (Not Found) se não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiscountCoupon> getById(@PathVariable Long id) {
        logger.info("Buscando cupom com ID: {}", id);
        Optional<DiscountCoupon> coupon = discountCouponService.findById(id);
        if (coupon.isPresent()) {
            logger.info("Cupom com ID: {} encontrado.", id);
            return ResponseEntity.ok(coupon.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /discount-coupons : Cria um novo cupom de desconto.
     *
     * @param discountCoupon o objeto do cupom a ser criado, passado no corpo da requisição. Deve ser válido.
     * @return um ResponseEntity com status 201 (Created) e o cupom recém-criado no corpo.
     */
    @PostMapping
    public ResponseEntity<DiscountCoupon> create(@Valid @RequestBody DiscountCoupon discountCoupon) {
        logger.info("Criando novo cupom com código: {}", discountCoupon.getCode());
        DiscountCoupon savedCoupon = discountCouponService.save(discountCoupon);
        logger.info("Cupom criado com sucesso. ID: {}", savedCoupon.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
    }

    /**
     * PUT /discount-coupons/{id} : Atualiza um cupom de desconto existente.
     *
     * @param id o ID do cupom a ser atualizado.
     * @param discountCouponDetails o objeto do cupom atualizado, passado no corpo da requisição. Deve ser válido.
     * @return um ResponseEntity com status 200 (OK) e o cupom atualizado no corpo.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiscountCoupon> update(@PathVariable Long id, @Valid @RequestBody DiscountCoupon discountCouponDetails) {
        logger.info("Iniciando atualização para cupom com ID: {}", id);
        DiscountCoupon updatedCoupon = discountCouponService.update(id, discountCouponDetails);
        logger.info("Cupom com ID: {} atualizado com sucesso.", updatedCoupon.getId());
        return ResponseEntity.ok(updatedCoupon);
    }

    /**
     * DELETE /discount-coupons/{id} : Deleta um cupom de desconto pelo seu ID.
     *
     * @param id o ID do cupom a ser deletado.
     * @return um ResponseEntity com status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Iniciando exclusão do cupom com ID: {}", id);
        discountCouponService.deleteById(id);
        logger.info("Cupom com ID: {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build();
    }
}