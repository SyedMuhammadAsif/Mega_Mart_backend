package com.megamart.cartserver.repository;

import com.megamart.cartserver.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUserId(String userId);
} 