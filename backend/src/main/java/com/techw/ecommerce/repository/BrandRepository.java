package com.techw.ecommerce.repository;

import com.techw.ecommerce.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
