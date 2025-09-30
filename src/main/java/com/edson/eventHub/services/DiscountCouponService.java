package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edson.eventHub.entities.DiscountCoupon;
import com.edson.eventHub.repository.DiscountCouponRepository;

@Service
public class DiscountCouponService {
    private final DiscountCouponRepository discountCouponRepository;

    public DiscountCouponService(DiscountCouponRepository discountCouponRepository) {
        this.discountCouponRepository = discountCouponRepository;
    }

    public List<DiscountCoupon> findAll() {
        return discountCouponRepository.findAll();
    }

    public Optional<DiscountCoupon> findById(Long id) {
        return discountCouponRepository.findById(id);
    }

    public DiscountCoupon save(DiscountCoupon discountCoupon) {
        return discountCouponRepository.save(discountCoupon);
    }

    public void deleteById(Long id) {
        discountCouponRepository.deleteById(id);
    }
}
