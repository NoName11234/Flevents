package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.security.jwt.JwtUtils;
import de.flyndre.fleventsbackend.security.payload.response.JwtResponse;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    public JwtResponse authorize(FleventsAccount account, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(account.getUuid(), password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    public boolean validateRights(Authentication auth, ArrayList<String> roles, String allowedUuid){
        for(GrantedAuthority authorization : auth.getAuthorities()){
            for(String role : roles){
                if(authorization.getAuthority().equals(allowedUuid+"."+role) || authorization.getAuthority().equals(allowedUuid+"."+role)){
                    return true;
                }
            }
        }
        return false;
    }
}
