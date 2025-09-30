package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.entities.DiscountCoupon;
import com.edson.eventHub.services.DiscountCouponService;

@RestController
@RequestMapping("/discount-coupons")
public class DiscountCouponController {
    private final DiscountCouponService DiscountCouponService;

    public DiscountCouponController(DiscountCouponService DiscountCouponService) {
        this.DiscountCouponService = DiscountCouponService;
    }

    @GetMapping
    public List<DiscountCoupon> getAll() {
        return DiscountCouponService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountCoupon> getById(@PathVariable Long id) {
        Optional<DiscountCoupon> DiscountCoupon = DiscountCouponService.findById(id);
        return DiscountCoupon.map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public DiscountCoupon create(@RequestBody DiscountCoupon DiscountCoupon) {
        return DiscountCouponService.save(DiscountCoupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountCoupon> update(@PathVariable Long id, @RequestBody DiscountCoupon DiscountCouponDetails) {
        Optional<DiscountCoupon> DiscountCouponOpt = DiscountCouponService.findById(id);
        if (!DiscountCouponOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        DiscountCoupon DiscountCoupon = DiscountCouponOpt.get();
        DiscountCoupon.setCode(DiscountCouponDetails.getCode());
        DiscountCoupon.setDiscountValue(DiscountCouponDetails.getDiscountValue());
        DiscountCoupon.setDiscountType(DiscountCouponDetails.getDiscountType());
        DiscountCoupon.setValidUntil(DiscountCouponDetails.getValidUntil());
        DiscountCoupon.setMaxUses(DiscountCouponDetails.getMaxUses());
        DiscountCoupon.setUses(DiscountCouponDetails.getUses());

        DiscountCoupon updated = DiscountCouponService.save(DiscountCoupon);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!DiscountCouponService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        DiscountCouponService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
