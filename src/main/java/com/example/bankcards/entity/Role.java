package com.example.bankcards.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
