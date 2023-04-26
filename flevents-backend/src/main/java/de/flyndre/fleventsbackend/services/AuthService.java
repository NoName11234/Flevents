package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.PlatformAdminRole;
import de.flyndre.fleventsbackend.Models.Role;
import de.flyndre.fleventsbackend.security.jwt.JwtUtils;
import de.flyndre.fleventsbackend.security.payload.response.JwtResponse;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * This ist the Service-Class for Authentication and Authorization.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * @param account the Account that should be Authorized
     * @param password the given password for checking
     * @return JwtResponse which contains the Token and Roles
     */
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

    /**
     * @param auth the current logged-in User
     * @param roles the roles that are allowed
     * @return boolean if the User is allowed or not
     */
    public boolean validateRights(Authentication auth, List<Role> roles, String allowedUuid){
        for(GrantedAuthority authorization : auth.getAuthorities()){
            if(authorization.getAuthority().equals(PlatformAdminRole.platformAdmin.toString())){
                return true;
            }
            for(Role role : roles){
                if(authorization.getAuthority().equals(allowedUuid+"."+role)){
                    return true;
                }
            }
        }
        return false;
    }
}
