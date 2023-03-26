package com.example.testedsecurity.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.example.testedsecurity.properties.RoleProperties.ROLE_PREFIX;
import static com.example.testedsecurity.properties.UserProperties.ID;
import static com.example.testedsecurity.properties.UserProperties.USER;

@Entity(name = USER)
@Table(name = USER)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
    uniqueConstraints = @UniqueConstraint(name = "user_role_constraint", columnNames = {"user_id", "roles"}))
    private Collection<Role> roles;

    private String password;

    @Column(unique = true)
    private String username;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(
                role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.name())).collect(Collectors.toList());
    }

}
