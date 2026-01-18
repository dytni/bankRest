package com.example.bankcards.service;

import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("ivanov");
        testUser.setFirstName("Ivan");
    }

    @Test
    @DisplayName("Создание пользователя: ошибка, если имя занято")
    void createUser_ShouldThrowException_WhenUserExists() {
        SignUpRequest request = new SignUpRequest("ivanov", "pass123", "Ivan", "Ivanov", null, null);
        when(userRepository.existsByUsername("ivanov")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Обновление профиля: частичное обновление полей")
    void updateUser_Profile_ShouldUpdateOnlyNonNullFields() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            SignUpRequest updateRequest = new SignUpRequest(null, null, "NewName", null, null, null);

            userService.updateUser(updateRequest);

            assertEquals("NewName", testUser.getFirstName());
            assertEquals("ivanov", testUser.getUsername()); // Не изменилось
            verify(userRepository).save(testUser);
        }
    }

    @Test
    @DisplayName("Удаление: проверка вызова репозитория по ID")
    void deleteUser_ById_Success() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}
