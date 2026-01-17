package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.request.SignUpRequest;
import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody SignUpRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(updateRequest));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/profile")
    public ResponseEntity<UserResponse> deleteCurrentUser() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest createRequest) {
        return  ResponseEntity.status(HttpStatus.OK).body(userService.createUser(createRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @RequestBody UserCreateRequest createRequest,
            @PathVariable Long id
    ) {
        return  ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(createRequest, id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
        return  ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_all")
    public ResponseEntity<Page<UserResponse>> getAll(
            @PageableDefault(sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable
    ){
        return  ResponseEntity.status(HttpStatus.OK).body(userService.findAll(pageable));
    }

}
