package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.Role;
import de.flyndre.fleventsbackend.services.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireControllerService {
    private final AuthService authService;

    public QuestionnaireControllerService(AuthService authService){

        this.authService = authService;
    }
    /**
     * Validate if the given Authentication matches to the given roles for the given event id.
     * @param auth the Authentication to validate.
     * @param uuid the id of the event in which context the validation should be done.
     * @param roles the event roles that should match.
     * @return true if the given parameters match, false if not.
     */
    public boolean getGranted(Authentication auth, String uuid, List<Role> roles){
        return authService.validateRights(auth, roles, uuid);
    }
}
