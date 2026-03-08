package com.techw.ecommerce.config;

import com.techw.ecommerce.model.Cart;
import com.techw.ecommerce.model.User;
import com.techw.ecommerce.repository.CartRepository;
import com.techw.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initialize Admin Account
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(User.Role.ADMIN)
                    .build();
            admin = userRepository.save(admin);

            Cart cart = Cart.builder().user(admin).build();
            cartRepository.save(cart);
            System.out.println("Admin account created: admin@gmail.com / admin");
        }

        // Initialize Regular User Account
        if (!userRepository.existsByEmail("user@gmail.com")) {
            User user = User.builder()
                    .email("user@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("Regular")
                    .lastName("User")
                    .role(User.Role.USER)
                    .build();
            user = userRepository.save(user);

            Cart cart = Cart.builder().user(user).build();
            cartRepository.save(cart);
            System.out.println("User account created: user@gmail.com / password");
        }
    }
}
