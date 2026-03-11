package com.techw.ecommerce.service;

import com.techw.ecommerce.dto.UserDto;
import com.techw.ecommerce.model.User;
import com.techw.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers() {
        User user1 = User.builder().id(UUID.randomUUID()).email("user1@test.com").role(User.Role.USER).build();
        User user2 = User.builder().id(UUID.randomUUID()).email("user2@test.com").role(User.Role.ADMIN).build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getEmail());
        assertEquals("user2@test.com", result.get(1).getEmail());
    }

    @Test
    void testUpdateUserRole() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("user@test.com").role(User.Role.USER).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserDto result = userService.updateUserRole(userId, User.Role.ADMIN);

        assertNotNull(result);
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository).save(user);
    }
}
