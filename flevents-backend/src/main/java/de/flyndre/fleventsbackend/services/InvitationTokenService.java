package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.InvitationToken;
import de.flyndre.fleventsbackend.repositories.InvitationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class InvitationTokenService {
    private final InvitationTokenRepository tokenRepository;

    public InvitationTokenService(InvitationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public InvitationToken saveToken(InvitationToken token){
        return tokenRepository.save(token);
    }

    public InvitationToken validate(String token){
        Optional<InvitationToken> optional = tokenRepository.findById(token);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Token not found");
        }
        return optional.get();
    }

    /**
     * Delete the given token
     * @param token
     */
    public void deleteToken(InvitationToken token){
        tokenRepository.delete(token);
    }
}
