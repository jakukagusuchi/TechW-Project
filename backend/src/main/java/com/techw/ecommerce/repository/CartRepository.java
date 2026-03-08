package com.techw.ecommerce.repository;

import com.techw.ecommerce.model.Cart;
import com.techw.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
