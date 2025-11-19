package com.edson.eventHub.repository;

import com.edson.eventHub.entities.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE TicketType t SET t.quantity = t.quantity - 1 WHERE t.id = :id AND t.quantity > 0")
    int decrementQuantity(Long id);
}