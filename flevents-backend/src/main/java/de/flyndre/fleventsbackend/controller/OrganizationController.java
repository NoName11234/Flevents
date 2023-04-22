package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.controllerServices.OrganizationControllerService;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the Controller for the REST-API path "/api/organizations".
 * It provides an interface regarding organizations.
 */

@RestController
@CrossOrigin   //TODO: Brauchts das wirklich?
@RequestMapping("/api/organizations")
public class OrganizationController {
   private OrganizationControllerService organizationControllerService;
   private final ModelMapper mapper;

   public OrganizationController(OrganizationControllerService organizationControllerService, ModelMapper mapper){
      this.organizationControllerService = organizationControllerService;
      this.mapper = mapper;
   }


   /**
    * Creates an event in the specified organization with the specified account with the role organizer.
    * Allows access only for admins and organizers of the specified organization.
    * @param organizationId the id of the organization to create the event in
    * @param event the event to be created
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the event as an object and the http status code
    */
   @PostMapping("/{organizationId}/create-event")
   public ResponseEntity createEvent(@PathVariable String organizationId, @RequestBody Event event, Authentication auth){

      boolean grantedAccess = organizationControllerService.getGranted(auth, organizationId, Arrays.asList(OrganizationRole.admin,OrganizationRole.organizer));
      UserDetailsImpl authUser = (UserDetailsImpl) auth.getPrincipal();
      if(grantedAccess){
      return new ResponseEntity(mapper.map(organizationControllerService.createEvent(organizationId, event, authUser.getId()),EventInformation.class), HttpStatus.CREATED);
      }

      return new ResponseEntity(null, HttpStatus.UNAUTHORIZED);

   }

   /**
    * Returns a list of the organizations existing in the database.
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with a list of organizations and the http status code
    */
   @GetMapping
   public ResponseEntity getOrganizations(Authentication auth){
      return new ResponseEntity(organizationControllerService.getOrganizations().stream().map(organization -> mapper.map(organization, OrganizationInformation.class)).collect(Collectors.toList()),HttpStatus.OK);
   }

   /**
    * Returns a single organization with the given id.
    * Allows only access to members and above of the specified organization
    * @param organizationId the id of the organization to be returned
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the organization object and the http status code
    */
   @GetMapping("/{organizationId}")
   public ResponseEntity getOrganization(@PathVariable String organizationId, Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin,OrganizationRole.organizer,OrganizationRole.member))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      Organization organization= organizationControllerService.getOrganizationById(organizationId);
      OrganizationInformation information = mapper.map(organization, OrganizationInformation.class);
      return new ResponseEntity(information,HttpStatus.OK);
   }

   /**
    * Returns a list of events from a single organization specified by an id.
    * Allows only access to members and above of the specified organization
    * @param organizationId the id of the organization to get the events from
    * @param auth the Authentication generated out of a barer token.
    * @return List<EventInformation> list of events
    */
   @GetMapping("/{organizationId}/events")
   public ResponseEntity getEvents(@PathVariable String organizationId,Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin,OrganizationRole.organizer,OrganizationRole.member))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      return new ResponseEntity<>(organizationControllerService.getEvents(organizationId).stream().map(event -> mapper.map(event, EventInformation.class)).collect(Collectors.toList()),HttpStatus.OK);
   }

   /**
    * Returns a list of account information objects from the accounts of an organization specified by its id.
    * Allows only access to organizer and above of the specified organizationAllows
    * @param organizationId the id of the organization to get the account information from.
    * @param auth the Authentication generated out of a barer token.
    * @return List<AccountInformation> list of account information
    */
   @GetMapping("/{organizationId}/accounts")
   public ResponseEntity getAccounts(@PathVariable String organizationId,Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin,OrganizationRole.organizer))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      return new ResponseEntity(organizationControllerService.getAccounts(organizationId).stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList()),HttpStatus.OK);
   }

   /**
    * Creates a new organization out of the given organization object, not all values have to be set.
    * @param organisation the organization to be created
    * @param email an email address to which an invitation to become the first admin is sent
    * @return ResponseEntity with the organization object and the http status code
    */
   @PostMapping
   public ResponseEntity createOrganisation(@RequestBody Organization organisation, @RequestParam @NotNull String email){
      //todo: implement authorization for platform admin
      if(!email.matches(".*@.*\\..*")){
         return new ResponseEntity<>("Please provide a valid email address.",HttpStatus.BAD_REQUEST);
      }
      if(organisation.getName()==null||organisation.getName().isBlank()){
         return new ResponseEntity("Please provide a name for the organization.",HttpStatus.BAD_REQUEST);
      }
      try {
         Organization organization = organizationControllerService.createOrganisation(organisation, email);
         return new ResponseEntity(mapper.map(organization, OrganizationInformation.class), HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Overwrites an existing organization object with the given organization object.
    * Allows only access to admins of the specified organization.
    * @param organizationId the id of the organization to be overwritten
    * @param organisation the organization object with the new organization information
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the new organization object and the http status code
    */
   @PostMapping("/{organizationId}")
   public ResponseEntity editOrganisation(@PathVariable String organizationId, @RequestBody Organization organisation, Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      return new ResponseEntity<>(mapper.map(organizationControllerService.editOrganisation(organizationId, organisation), OrganizationInformation.class),HttpStatus.OK);
   }

   /**
    * Sends an invitation link in a mail to a specified organization.
    * Allows only access to admins of the specified organization.
    * @param organizationId the id of the organization to send an invitation link to
    * @param email the email to send the invitation to
    * @param role the role to set the account with the invitation to
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code and optional an error code
    */
   @PostMapping("/{organizationId}/invite")
   public ResponseEntity<String> sendInvitation(@PathVariable String organizationId, @RequestParam String email,@RequestParam OrganizationRole role,Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      try {
         organizationControllerService.sendInvitation(organizationId, email, role);
         return  new ResponseEntity<>(HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Adds an account to an organization after the account owner accepted an invitation.
    * Allows access for all users with a valid barer token.
    * @param organizationId the id of the organization to add the account to
    * @param token the token to verify the invitation
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/add-account")
   public ResponseEntity acceptInvitation(@PathVariable String organizationId,@RequestParam String token,Authentication auth){
      UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
      organizationControllerService.acceptInvitation(organizationId, details.getId(), token);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    * Removes a specified account from a specified organization.
    * Allows only access to admin of the specified organization
    * @param organizationId the id of the organization to remove the account from
    * @param accountId the id of the account to be removed
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/remove-account/{accountId}")
   public ResponseEntity removeAccount(@PathVariable String organizationId, @PathVariable String accountId, Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      organizationControllerService.removeAccount(organizationId, accountId);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    * Changes the role of a specified account in a specified organization.
    * Allows access only to admins of the specified organization
    * @param organizationId the id of the organization where to change the accounts role
    * @param accountId the id of the account which role has to be changed
    * @param fromRole the role of the account before the change
    * @param toRole the role to change the account to
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String organizationId, @PathVariable String accountId,@RequestParam OrganizationRole fromRole,@RequestParam OrganizationRole toRole, Authentication auth){
      if(!organizationControllerService.getGranted(auth,organizationId,Arrays.asList(OrganizationRole.admin))){
         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      organizationControllerService.changeRole(organizationId, accountId, fromRole,toRole);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   /**
    * A specified account leaves a specified organization.
    * @param organizationId the id of the organization to remove the account from
    * @param accountId the id of the account to be removed
    * @return ReponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/leave-organisation/{acountId}")
   public ResponseEntity leaveOrganization(@PathVariable String organizationId, @PathVariable String accountId){
      organizationControllerService.leaveOrganization(organizationId, accountId);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}
