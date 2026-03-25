package com.techw.ecommerce.controller;

import com.techw.ecommerce.dto.ApiResponse;
import com.techw.ecommerce.exception.ResourceNotFoundException;
import com.techw.ecommerce.model.Brand;
import com.techw.ecommerce.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Brand>> createBrand(@RequestBody Brand brand) {
        return ResponseEntity.ok(ApiResponse.created(brandRepository.save(brand)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(@PathVariable UUID id, @RequestBody Brand brand) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand", "id", id);
        }
        brand.setId(id);
        return ResponseEntity.ok(ApiResponse.success(brandRepository.save(brand)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable UUID id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand", "id", id);
        }
        brandRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));
    }
}
