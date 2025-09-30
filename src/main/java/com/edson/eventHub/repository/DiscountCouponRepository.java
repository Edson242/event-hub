package com.edson.eventHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edson.eventHub.entities.DiscountCoupon;

@Repository
public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Long> {
}