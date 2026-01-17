package com.example.bankcards.entity;

import com.example.bankcards.util.EncryptionConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Convert(converter = EncryptionConverter.class)
    @Column(nullable = false)
    private String lastName;

    @Convert(converter = EncryptionConverter.class)
    @Column(nullable = false)
    private String firstName;

    @Convert(converter = EncryptionConverter.class)
    @Column(nullable = false)
    private String secondName;

    @Column(nullable = false)
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    @Override
    public Set<Role> getAuthorities() {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
