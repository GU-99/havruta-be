package kr.growup.havrutabe.common.security;


import lombok.Builder;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetail implements UserDetails  {

    @Getter
    private final Long id;

    private final String email;

    private final String password;

    @Builder
    public CustomUserDetail(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
