package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.controllerServices.OrganizationControllerService;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the Controller for the REST-API path "/api/organizations".
 * It provides functionality regarding organizations.
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
    * @param organizationId the id of the organization to create the event in
    * @param event the event to be created
    * @param accountId the account to be the default organizer of the created event
    * @return ResponseEntity with the event as an object and the http status code
    */
   @PostMapping("/{organizationId}/create-event")
   public ResponseEntity createEvent(@PathVariable String organizationId, @RequestBody Event event, @RequestParam String accountId) {
      return new ResponseEntity(mapper.map(organizationControllerService.createEvent(organizationId, event, accountId),EventInformation.class), HttpStatus.CREATED);
   }

   /**
    * Returns a list of the organizations existing in the database.
    * @return ResponseEntity with a list of organizations and the http status code
    */
   @GetMapping
   public ResponseEntity getOrganizations(){
      return new ResponseEntity(organizationControllerService.getOrganizations().stream().map(organization -> mapper.map(organization, OrganizationInformation.class)).collect(Collectors.toList()),HttpStatus.OK);
   }

   /**
    * Returns a single organization with the given id.
    * @param organizationId the id of the organization to be returned
    * @return ResponseEntity with the organization object and the http status code
    */
   @GetMapping("/{organizationId}")
   public ResponseEntity getOrganization(@PathVariable String organizationId){
      Organization organization= organizationControllerService.getOrganizationById(organizationId);
      OrganizationInformation information = mapper.map(organization, OrganizationInformation.class);
      return new ResponseEntity(information,HttpStatus.OK);
   }

   /**
    * Returns a list of events from a single organization specified by an id.
    * @param organizationId the id of the organization to get the events from
    * @return List<EventInformation> list of events
    */
   @GetMapping("/{organizationId}/events")
   public List<EventInformation> getEvents(@PathVariable String organizationId){
      return organizationControllerService.getEvents(organizationId).stream().map(event -> mapper.map(event, EventInformation.class)).collect(Collectors.toList());
   }

   /**
    * Returns a list of account information objects from the accounts of an organization specified by its id.
    * @param organizationId the id of the organization to get the account information from
    * @return List<AccountInformation> list of account information
    */
   @GetMapping("/{organizationId}/accounts")
   public List<AccountInformation> getAccounts(@PathVariable String organizationId){
      return organizationControllerService.getAccounts(organizationId).stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList());
   }

   /**
    * Creates a new organization out of the given organization object, not all values have to be set.
    * @param organisation the organization to be created
    * @param email an email address to which an invitation to become the first admin is sent
    * @return ResponseEntity with the organization object and the http status code
    */
   @PostMapping
   public ResponseEntity createOrganisation(@RequestBody Organization organisation, @RequestParam @NotNull String email){
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
    * @param organizationId the id of the organization to be overwritten
    * @param organisation the organization object with the new organization information
    * @return ResponseEntity with the new organization object and the http status code
    */
   @PostMapping("/{organizationId}")
   public ResponseEntity editOrganisation(@PathVariable String organizationId, @RequestBody Organization organisation){
      return new ResponseEntity<>(mapper.map(organizationControllerService.editOrganisation(organizationId, organisation), OrganizationInformation.class),HttpStatus.OK);
   }

   /**
    * Sends an invitation link in a mail to a specified organization.
    * @param organizationId the id of the organization to send a invitation link to
    * @param email the email to send the invitation to
    * @param role the role to set the account with the invitation to
    * @return ResponseEntity with the http status code and optional an error code
    */
   @PostMapping("/{organizationId}/invite")
   public ResponseEntity<String> sendInvitation(@PathVariable String organizationId, @RequestParam String email,@RequestParam OrganizationRole role){
      try {
         organizationControllerService.sendInvitation(organizationId, email, role);
         return  new ResponseEntity<>(HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Adds an account to an organization after the account owner accepted an invitation.
    * @param organizationId the id of the organization to add the account to
    * @param userId the id of the account to be added to the organization
    * @param token the token to verify the invitation
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/add-account/{userId}")
   public ResponseEntity acceptInvitation(@PathVariable String organizationId,@PathVariable String userId,@RequestParam String token){
      organizationControllerService.acceptInvitation(organizationId, userId, token);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    * Removes a specified account from a specified organization.
    * @param organizationId the id of the organization to remove the account from
    * @param accountId the id of the account to be removed
    * @return ReponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/remove-account/{accountId}")
   public ResponseEntity removeAccount(@PathVariable String organizationId, @PathVariable String accountId){
      organizationControllerService.removeAccount(organizationId, accountId);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    * Changes the role of a specified account in a specified organization.
    * @param organizationId the id of the organization where to change the accounts role
    * @param accountId the id of the account which role has to be changed
    * @param fromRole the role of the account before the change
    * @param toRole the role to change the account to
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{organizationId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String organizationId, @PathVariable String accountId,@RequestParam OrganizationRole fromRole,@RequestParam OrganizationRole toRole){
      organizationControllerService.changeRole(organizationId, accountId, fromRole,toRole);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}
