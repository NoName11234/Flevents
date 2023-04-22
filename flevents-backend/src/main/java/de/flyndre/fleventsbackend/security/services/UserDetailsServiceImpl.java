package de.flyndre.fleventsbackend.security.services;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    FleventsAccountRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        FleventsAccount user = userRepository.findById(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with UUID: " + uuid));

        return UserDetailsImpl.build(user);
    }

}
