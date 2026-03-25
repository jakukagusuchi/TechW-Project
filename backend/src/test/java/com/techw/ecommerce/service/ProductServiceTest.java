package com.techw.ecommerce.service;

import com.techw.ecommerce.dto.CreateProductRequest;
import com.techw.ecommerce.exception.ResourceNotFoundException;
import com.techw.ecommerce.model.Brand;
import com.techw.ecommerce.model.Category;
import com.techw.ecommerce.model.Product;
import com.techw.ecommerce.repository.BrandRepository;
import com.techw.ecommerce.repository.CategoryRepository;
import com.techw.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private Brand testBrand;
    private CreateProductRequest testRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("Electronics");

        testBrand = new Brand();
        testBrand.setId(UUID.randomUUID());
        testBrand.setName("TechBrand");

        testProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .description("A test product")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(50)
                .category(testCategory)
                .brand(testBrand)
                .imageUrl("https://example.com/img.jpg")
                .isActive(true)
                .build();

        testRequest = new CreateProductRequest();
        testRequest.setName("Test Product");
        testRequest.setDescription("A test product");
        testRequest.setPrice(BigDecimal.valueOf(99.99));
        testRequest.setStockQuantity(50);
        testRequest.setCategoryId(testCategory.getId());
        testRequest.setBrandId(testBrand.getId());
        testRequest.setImageUrl("https://example.com/img.jpg");
    }

    @Test
    @DisplayName("getAllProducts - returns paginated products")
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(testProduct));
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Product> result = productService.getAllProducts(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getProductById - returns product when found")
    void testGetProductById_Success() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(testProduct.getId());

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
    }

    @Test
    @DisplayName("getProductById - throws ResourceNotFoundException when not found")
    void testGetProductById_NotFound() {
        UUID fakeId = UUID.randomUUID();
        when(productRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(fakeId));
    }

    @Test
    @DisplayName("createProduct - creates product with category and brand")
    void testCreateProduct_Success() {
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(brandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        Product result = productService.createProduct(testRequest);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(testCategory, result.getCategory());
        assertEquals(testBrand, result.getBrand());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct - throws when category not found")
    void testCreateProduct_CategoryNotFound() {
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(testRequest));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProduct - throws when brand not found")
    void testCreateProduct_BrandNotFound() {
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(brandRepository.findById(testBrand.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(testRequest));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct - updates existing product")
    void testUpdateProduct_Success() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(brandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        testRequest.setName("Updated Product");
        testRequest.setPrice(BigDecimal.valueOf(149.99));

        Product result = productService.updateProduct(testProduct.getId(), testRequest);

        assertEquals("Updated Product", result.getName());
        assertEquals(BigDecimal.valueOf(149.99), result.getPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct - throws when product not found")
    void testUpdateProduct_NotFound() {
        UUID fakeId = UUID.randomUUID();
        when(productRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(fakeId, testRequest));
    }

    @Test
    @DisplayName("deleteProduct - deletes existing product")
    void testDeleteProduct_Success() {
        when(productRepository.existsById(testProduct.getId())).thenReturn(true);

        productService.deleteProduct(testProduct.getId());

        verify(productRepository).deleteById(testProduct.getId());
    }

    @Test
    @DisplayName("deleteProduct - throws when product not found")
    void testDeleteProduct_NotFound() {
        UUID fakeId = UUID.randomUUID();
        when(productRepository.existsById(fakeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(fakeId));
        verify(productRepository, never()).deleteById(any());
    }
}
