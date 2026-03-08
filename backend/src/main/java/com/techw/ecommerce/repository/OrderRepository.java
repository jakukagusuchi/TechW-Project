package com.techw.ecommerce.repository;

import com.techw.ecommerce.model.Order;
import com.techw.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByUserId(UUID userId);
}
