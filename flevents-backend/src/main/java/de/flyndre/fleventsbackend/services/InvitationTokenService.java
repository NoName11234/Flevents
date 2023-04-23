package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.InvitationToken;
import de.flyndre.fleventsbackend.repositories.InvitationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * This Service provides logic and usage for the InvitationToken repository.
 * It provides methods for manipulating the data of these repositories.
 * @author Lukas Burkhardt
 * @version $I$
 */

@Service
public class InvitationTokenService {
    private final InvitationTokenRepository tokenRepository;

    public InvitationTokenService(InvitationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Saves the given token to the database.
     * @param token the token to be saved
     * @return the saved InvitationToken
     */
    public InvitationToken saveToken(InvitationToken token){
        return tokenRepository.save(token);
    }

    /**
     * Checks whether the given token exists in the database. Throws an Exception if the token does not exist.
     * @param token the token to be validated
     * @return the validated Token
     */
    public InvitationToken validate(String token){
        Optional<InvitationToken> optional = tokenRepository.findById(token);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Token not found");
        }
        return optional.get();
    }

    /**
     * Deletes the given token out of the database.
     * @param token the token to be deleted
     */
    public void deleteToken(InvitationToken token){
        tokenRepository.delete(token);
    }
}
