package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edson.eventHub.entities.DiscountCoupon;
import com.edson.eventHub.repository.DiscountCouponRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Classe de serviço para gerenciar cupons de desconto.
 * Esta classe lida com a lógica de negócio para criar, recuperar, atualizar e deletar cupons de desconto.
 */
@Service
public class DiscountCouponService {
    private final DiscountCouponRepository discountCouponRepository;

    /**
     * Constrói um DiscountCouponService com o repositório necessário.
     *
     * @param discountCouponRepository O repositório para acesso aos dados de cupons de desconto.
     */
    public DiscountCouponService(DiscountCouponRepository discountCouponRepository) {
        this.discountCouponRepository = discountCouponRepository;
    }

    /**
     * Recupera todos os cupons de desconto.
     *
     * @return uma lista de todos os cupons de desconto.
     */
    public List<DiscountCoupon> findAll() {
        return discountCouponRepository.findAll();
    }

    /**
     * Encontra um cupom de desconto pelo seu ID.
     *
     * @param id O ID do cupom de desconto a ser encontrado.
     * @return um Optional contendo o cupom encontrado, ou um Optional vazio se não for encontrado.
     */
    public Optional<DiscountCoupon> findById(Long id) {
        return discountCouponRepository.findById(id);
    }

    /**
     * Salva um novo cupom de desconto ou atualiza um existente.
     *
     * @param discountCoupon A entidade de cupom de desconto a ser salva.
     * @return a entidade de cupom de desconto salva.
     */
    public DiscountCoupon save(DiscountCoupon discountCoupon) {
        return discountCouponRepository.save(discountCoupon);
    }

    /**
     * Atualiza um cupom de desconto existente com novos detalhes.
     *
     * @param id O ID do cupom a ser atualizado.
     * @param couponDetails O objeto contendo os novos detalhes para o cupom.
     * @return o cupom de desconto atualizado.
     * @throws EntityNotFoundException se nenhum cupom for encontrado com o ID fornecido.
     */
    @Transactional
    public DiscountCoupon update(Long id, DiscountCoupon couponDetails) {
        DiscountCoupon coupon = discountCouponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cupom de desconto não encontrado com o ID: " + id));

        coupon.setCode(couponDetails.getCode());
        coupon.setDiscountValue(couponDetails.getDiscountValue());
        coupon.setDiscountType(couponDetails.getDiscountType());
        coupon.setValidUntil(couponDetails.getValidUntil());
        coupon.setMaxUses(couponDetails.getMaxUses());
        
        return discountCouponRepository.save(coupon);
    }

    /**
     * Deleta um cupom de desconto pelo seu ID.
     *
     * @param id O ID do cupom a ser deletado.
     * @throws EntityNotFoundException se nenhum cupom for encontrado com o ID fornecido.
     */
    public void deleteById(Long id) {
        if (!discountCouponRepository.existsById(id)) {
            throw new EntityNotFoundException("Cupom de desconto não encontrado com o ID: " + id);
        }
        discountCouponRepository.deleteById(id);
    }
}