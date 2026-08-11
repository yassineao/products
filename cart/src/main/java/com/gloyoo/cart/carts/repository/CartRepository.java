package com.gloyoo.cart.carts.repository;

import com.gloyoo.cart.carts.entity.Carts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository <Carts, UUID> {
    Optional<Carts> findByUserId(UUID userId);

    Optional<Carts> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserId(UUID userId);
}
