package com.example.bankcards.service;

import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.SecurityUtils;
import com.example.bankcards.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserResponse findByUsername(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDto);
    }

    @Transactional
    public UserResponse createUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username())) {
            throw new UserAlreadyExistsException(
                    String.format("Username '%s' is already taken", signUpRequest.username())
            );
        }
        User user = new User();
        user.setUsername(signUpRequest.username());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setFirstName(signUpRequest.firstName());
        user.setLastName(signUpRequest.lastName());
        user.setSecondName(signUpRequest.secondName());
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest createRequest) {
        if (userRepository.existsByUsername(createRequest.username())) {
            throw new UserAlreadyExistsException(
                    String.format("Username '%s' is already taken", createRequest.username())
            );
        }
        User user = new User();
        user.setUsername(createRequest.username());
        user.setPassword(passwordEncoder.encode(createRequest.password()));
        user.setFirstName(createRequest.firstName());
        user.setLastName(createRequest.lastName());
        user.setSecondName(createRequest.secondName());
        user.setRoles(Collections.singleton(createRequest.role()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponse updateUser(SignUpRequest updateRequest) {
        User user = SecurityUtils.getCurrentUser();

        if (updateRequest.firstName() != null) user.setFirstName(updateRequest.firstName());
        if (updateRequest.lastName() != null) user.setLastName(updateRequest.lastName());
        if (updateRequest.secondName() != null) user.setSecondName(updateRequest.secondName());
        if (updateRequest.password() != null && !updateRequest.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.password()));
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponse updateUser(UserCreateRequest updateRequest, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (updateRequest.role() != null) user.setRoles(Collections.singleton(updateRequest.role()));
        if (updateRequest.firstName() != null) user.setFirstName(updateRequest.firstName());
        if (updateRequest.lastName() != null) user.setLastName(updateRequest.lastName());
        if (updateRequest.secondName() != null) user.setSecondName(updateRequest.secondName());
        if (updateRequest.password() != null && !updateRequest.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.password()));
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponse deleteUser() {
        User userToDelete = SecurityUtils.getCurrentUser();
        userRepository.delete(userToDelete);

        return userMapper.toDto(userToDelete);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
