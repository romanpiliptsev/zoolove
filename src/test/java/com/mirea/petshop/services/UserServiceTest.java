package com.mirea.petshop.services;

import com.mirea.petshop.models.User;
import com.mirea.petshop.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private IUserRepository iUserRepository;
    @Captor
    private ArgumentCaptor<User> captor;
    private User user1, user2, user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setEmail("email");
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setRole("role");

        user2 = new User();
        user2.setId(2);
        user2.setEmail("email");
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setRole("role");

        user3 = new User();
        user3.setId(3);
        user3.setEmail("email");
        user3.setUsername("username3");
        user3.setPassword("password");
        user3.setRole("role");
    }

    @Test
    void loadUserByUsername() {
        Mockito.when(iUserRepository.findByUsername("username2")).thenReturn(user2);
        assertEquals(user2, iUserRepository.findByUsername("username2"));
    }

    @Test
    void create() {
        String email = "email", username = "username", password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        userService.saveUser(user);
        Mockito.verify(iUserRepository).save(captor.capture());
        User captured = captor.getValue();
        assertEquals("username", captured.getUsername());
    }

    @Test
    void getAll() {
        Mockito.when(iUserRepository.findAll()).thenReturn(List.of(user1, user2, user3));
        assertEquals(List.of(user1, user2, user3), iUserRepository.findAll());
    }

    @Test
    void saveUser() {
        userService.saveUser(user1);
        Mockito.verify(iUserRepository).save(captor.capture());
        User captured = captor.getValue();
        assertEquals("username1", captured.getUsername());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1);
        Mockito.verify(iUserRepository).deleteById(1);
    }
}
