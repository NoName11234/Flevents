package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.InvitationToken;
import de.flyndre.fleventsbackend.Models.Role;
import de.flyndre.fleventsbackend.repositories.InvitationTokenRepository;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;

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
    private static ResourceBundle strings = ResourceBundle.getBundle("ConfigStrings");

    /**
     * Creates and saves the given token to the database.
     * @param invitedToId an uuid of the event or organization the token should be valid for.
     * @param role the role that an invited account should get when accepting the invitation.
     * @return the created InvitationToken
     */
    public InvitationToken createToken(String invitedToId,Role role){
        return tokenRepository.save(new InvitationToken(null,role.toString(),invitedToId));
    }

    /**
     * Checks whether the given token exists in the database. Throws an Exception if the token does not exist.
     * @param token the token to be validated
     * @return the validated Token
     */
    public InvitationToken validate(String token,String invitedToId) throws InvalidAttributesException {
        Optional<InvitationToken> optional = tokenRepository.findById(token);
        if(!optional.isPresent()){
            throw new NoSuchElementException(strings.getString("invitationTokenService.TokenNotFound"));
        }
        if(!optional.get().getInvitedToId().equals(invitedToId)){
            throw new InvalidAttributesException(strings.getString("invitationTokenService.WrongTokenForMail"));
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
