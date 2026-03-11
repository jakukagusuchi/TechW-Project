package com.techw.ecommerce.service;

import com.techw.ecommerce.dto.CartDto;
import com.techw.ecommerce.dto.CartItemRequest;
import com.techw.ecommerce.model.*;
import com.techw.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>())
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .isActive(true)
                .build();
    }

    @Test
    void testGetCartByUserEmail() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        CartDto result = cartService.getCartByUserEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getUserEmail());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testAddItemToCart() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        CartItemRequest request = new CartItemRequest();
        request.setProductId(testProduct.getId());
        request.setQuantity(2);

        CartDto result = cartService.addItemToCart(testUser.getEmail(), request);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        assertEquals("Test Product", result.getItems().get(0).getProductName());
    }

    @Test
    void testCheckout() {
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .product(testProduct)
                .quantity(2)
                .build();
        testCart.getItems().add(cartItem);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = cartService.checkout(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(1, result.getItems().size());
        assertEquals(BigDecimal.valueOf(200.0), result.getTotalAmount());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());

        verify(cartRepository).save(testCart); // Verify cart was saved (cleared)
        assertTrue(testCart.getItems().isEmpty(), "Cart should be empty after checkout");
    }
}
