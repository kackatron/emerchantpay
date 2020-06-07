package com.payment.system.security;

import com.payment.system.dao.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserDetailsImpl is an object that represents an User after authentication.
 * In case of successful authentication UserDetailsImpl will contain the credential info for the given user plus
 * all the specific data that the Application may need.
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final Long id;

    private final String name;

    private final String password;

    private final String email;

    private final String status;

    private final String description;

    private final double totalTransactionSum;

    private final Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(Long id, String name, String password, String email, String description, String status, double totalTransactionSum,
                            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.description = description;
        this.status = status;
        this.totalTransactionSum = totalTransactionSum;
        this.authorities = authorities;
    }

    public static UserDetailsImpl getUserDetailsImpl(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getEmail(),
                user.getDescription(),
                user.getStatus().toString(),
                user.getTotalTransactionSum(),
                authorities);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalTransactionSum() {
        return totalTransactionSum;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return id.equals(user.id);
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", totalTransactionSum=" + totalTransactionSum +
                ", authorities=" + authorities +
                '}';
    }
}
