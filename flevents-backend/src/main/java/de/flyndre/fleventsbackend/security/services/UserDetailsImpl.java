package de.flyndre.fleventsbackend.security.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.OrganizationAccount;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(FleventsAccount user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for(int i = 0; i < user.getEvents().size(); i++){
            authorities.add(new SimpleGrantedAuthority(user.getEvents().get(i).getEvent().getUuid().toLowerCase() + "." + user.getEvents().get(i).getRole().toString().toLowerCase()));
        }
        for(int i = 0; i < user.getOrganisations().size(); i++){
            authorities.add(new SimpleGrantedAuthority(user.getOrganisations().get(i).getOrganization().getUuid().toLowerCase() + "." + user.getOrganisations().get(i).getRole().toString().toLowerCase()));
        }


        return new UserDetailsImpl(
                user.getUuid(),
                user.getFirstname(),
                user.getEmail(),
                user.getSecret(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return Objects.equals(id, user.id);
    }
}
