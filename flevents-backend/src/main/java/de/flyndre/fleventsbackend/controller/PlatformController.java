package de.flyndre.fleventsbackend.controller;


import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.controllerServices.PlatformControllerService;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationPreview;
import de.flyndre.fleventsbackend.security.payload.request.LoginRequest;
import de.flyndre.fleventsbackend.security.payload.request.LogoutRequest;
import de.flyndre.fleventsbackend.services.AuthService;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * This Class is the Controller for the REST-API path "/api/platform".
 * It provides an interface for all operations that belong to administrate the platform itself
 * @author Lukas Burkhardt
 * @since 2.3.1
 * @version $I$
 */
@RestController
@CrossOrigin
@RequestMapping("/api/platform")
public class PlatformController {

    private final PlatformControllerService platformControllerService;
    private final ModelMapper mapper;
    public PlatformController(PlatformControllerService platformControllerService, ModelMapper mapper) {

        this.platformControllerService = platformControllerService;
        this.mapper = mapper;
    }

    /**
     * Creates an Organization out of an organizationPreview
     * Allows only access if you are a platformAdmin
     * @param organizationPreview the organizationPreview filled with all information to create the organization.
     * @param auth the Authentication generated out of a barer token.
     * @return the created Organization alongside an 201 Created a or an error if something went wrong.
     */
    @PostMapping("/organizations")
    public ResponseEntity createOrganization(@RequestBody OrganizationPreview organizationPreview,@RequestParam @NotNull String email, Authentication auth) {
        if (!platformControllerService.getGranted(auth)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        if(!email.matches(".*@.*\\..*")){
            return new ResponseEntity<>("Please provide a valid email address.",HttpStatus.BAD_REQUEST);
        }
        if(organizationPreview.getName()==null||organizationPreview.getName().isBlank()){
            return new ResponseEntity("Please provide a name for the organization.",HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(
                    mapper.map(
                            platformControllerService.createOrganisation(mapper.map(organizationPreview, Organization.class),email)
                            , OrganizationInformation.class)
                    , HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an Organization an all associated information from the system.
     * Allows only access if you are a platformAdmin
     * @param organizationId the id of the organization to delete.
     * @param auth the Authentication generated out of a barer token.
     * @return Ok if successfully deleted or an error if something went wrong.
     */
    @DeleteMapping("/organizations/{organizationId}")
    public ResponseEntity deleteOrganization(@PathVariable String organizationId, Authentication auth){
        if(!platformControllerService.getGranted(auth)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try{
            platformControllerService.deleteOrganization(organizationId);
            return new ResponseEntity(HttpStatus.OK);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logs in a platformAccount
     * @param request the request with a username and a secret
     * @return a valid jwt token alongside an Ok or an error if something went wrong
     */
    //@PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request){
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * Invalidate the given jwt token
     * Allows only access if you are a platformAdmin
     * @param request a LogoutRequest containing the jwt token
     * @param auth the Authentication generated out of a barer token.
     * @return an Ok or an error if something went wrong
     */
    //@PostMapping("/logout")
    public ResponseEntity logout(@RequestBody LogoutRequest request,Authentication auth){
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Refresh a token if its time is over.
     * Allows only access if you are a platformAdmin
     * @param auth the Authentication generated out of a barer token.
     * @return a new token alongside an Ok or an error if something went wrong
     */
    //@PostMapping("/refresh")
    public ResponseEntity refresh(Authentication auth){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
