package de.flyndre.fleventsbackend.controller;


import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.controllerServices.PlatformControllerService;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationPreview;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * This Class is the Controller for the REST-API path "/api/platform".
 * It provides an interface for all operations that belong to administrate the platform itself
 * @author Lukas Burkhardt
 * @since 2.3.1
 * @version $I$
 */
@RestController
@RequestMapping("/api/platform")
public class PlatformController {

    private final PlatformControllerService platformControllerService;
    private final ModelMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(PlatformController.class);
    private static ResourceBundle strings = ResourceBundle.getBundle("ConfigStrings");
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
            return new ResponseEntity<>(strings.getString("platformController.NoValidMail"),HttpStatus.BAD_REQUEST);
        }
        if(organizationPreview.getName()==null||organizationPreview.getName().isBlank()){
            return new ResponseEntity(strings.getString("platformController.NoValidOrganizationName"),HttpStatus.BAD_REQUEST);
        }
        if(organizationPreview.getCustomerNumber()==null||organizationPreview.getCustomerNumber().isBlank()){
            return new ResponseEntity(strings.getString("platformController.NoCustomerNumber"),HttpStatus.BAD_REQUEST);
        }
        if(organizationPreview.getPhoneContact()==null||organizationPreview.getPhoneContact().isBlank()||!organizationPreview.getPhoneContact().matches("(0|\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1))\\d{1,14}$")){
            return new ResponseEntity(strings.getString("platformController.NoValidPhoneContact"),HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(
                    mapper.map(
                            platformControllerService.createOrganisation(mapper.map(organizationPreview, Organization.class),email)
                            , OrganizationInformation.class)
                    , HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(strings.getString("logger.InternalError"),e);
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
            logger.error(strings.getString("platformController.OrganizationNotFound"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
