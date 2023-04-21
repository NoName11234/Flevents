package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.controllerServices.EventControllerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the Controller for the REST-API path "/api/events".
 * It provides an interface regarding events.
 */

@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {

private EventControllerService eventControllerService;
private final ModelMapper mapper;
   public EventController(EventControllerService eventControllerService, ModelMapper mapper){
      this.eventControllerService = eventControllerService;
      this.mapper = mapper;
   }

   /**
    * Returns a list of all existing events.
    * @return list of all events
    */
   @GetMapping
   public List<EventInformation> getEvents() {
      return eventControllerService.getEvents().stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
   }

   /**
    * Returns the event with the given id.
    * @param eventId the id of the event
    * @return event with the given id
    */
   @GetMapping("/{eventId}")
   public EventInformation getEventById(@PathVariable String eventId){
      return mapper.map(eventControllerService.getEventById(eventId),EventInformation.class);
   }

   /**
    * Delets the event with the given id.
    * @param eventId the id of the event to delete
    * @return ResponseEntity with the http status code
    */
   @DeleteMapping("/{eventId}")
   public ResponseEntity deleteEvent(@PathVariable String eventId){
      eventControllerService.deleteEvent(eventId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   /**
    * Returns a list of all attendees from the specified event.
    * @param eventId the id of the event to get the list of attendees from
    * @return ResponseEntity with a list with the attendees of the event and the http status
    */
   @GetMapping("/{eventId}/attendees")
   public ResponseEntity getAttendees(@PathVariable String eventId){
      return new ResponseEntity<>(eventControllerService.getAttendees(eventId).stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
   }

   /**
    * Returns a list of all organizers from the specified event.
    * @param eventId the id of the event to get the list of organizers from
    * @return ResponseEntity with a list with the organizers of the event and the http status code
    */
   @GetMapping("/{eventId}/organizers")
   public ResponseEntity getOrganizers(@PathVariable String eventId){
      return new ResponseEntity<>(eventControllerService.getOrganizers(eventId).stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
   }


   /**
    * not implemented yet
    * @param eventId the id of the event to get the attachments from
    * @return a list with the URIs of the attachments
    */
   @GetMapping("/{eventId}/attachments")
   public List<URI> getAttachment(@PathVariable String eventId){
      //TODO: Implement
      return new ArrayList<>();
   }

   /**
    * Overwrites the event specified with a given id with the specified event.
    * @param eventId the id of the event to be set
    * @param event the event to be set to the given id
    * @return ResponseEntity with the event object and the http status code
    */
   @PostMapping("/{eventId}")
   public ResponseEntity setEventById(@PathVariable String eventId, @RequestBody Event event){
      return new ResponseEntity<>(mapper.map(eventControllerService.setEventById(eventId,event),EventInformation.class),HttpStatus.OK);
   }

   /**
    * Sends an invitation email to the given email with a link to register with the specified role to the specified event.
    * @param eventId the id of the event to send an invitation to
    * @param email the email to send the invitation link to
    * @param role the role which gets assigend to the invited person
    * @return ResponseEntity with the http status code and an optional error message
    */
   @PostMapping("/{eventId}/invite")
   public ResponseEntity inviteToEvent(@PathVariable String eventId, @RequestParam() String email,@RequestParam EventRole role){
      try{
         eventControllerService.inviteToEvent(eventId, email, role);
         return new ResponseEntity<>(HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Adds the specified account to the specified event. The account will have the specified role.
    * @param eventId the id of the event to add the account to
    * @param accountId the id of the account to be added
    * @param token the token in the invitation link to verify the invitation
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/add-account/{accountId}")
   public ResponseEntity addAccountToEvent(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam(required = false) String token){
      eventControllerService.acceptInvitation(eventId, accountId, token);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    *Changes the role of a specified account in an event.
    * @param eventId the id of the event with the account
    * @param accountId the id of the account which role has to be changed
    * @param fromRole the role of the account before the change
    * @param toRole the role to change the account to
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam EventRole fromRole, @RequestParam EventRole toRole){
      eventControllerService.changeRole(eventId, accountId, fromRole,toRole);
      return new ResponseEntity(HttpStatus.OK);
   }


   //@PostMapping("/{eventId}/create-account")
   /*public ResponseEntity createAndAddAccountToEvent(@PathVariable String eventId, @RequestBody String eMail){
      return eventControllerService.createAndAddAccountToEvent(eventId, eMail);
   }*/


   /**
    * Adds an anonymous account to an event.
    * @param eventId the id of the event to add the anonymous account to
    * @param account the anonymous account to be added
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/add-account/add-anonymous")
   public ResponseEntity addAnonymousAccountToEvent(@PathVariable String eventId, @RequestBody FleventsAccount account){
      eventControllerService.addAnonymousAccountToEvent(eventId, account);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    * Removes a specified account from the specified event. The account needs to have the specified role.
    * @param eventId the id of the event to remove the account from
    * @param accountId the id of the account to be removed from the event
    * @param role the role of the account
    * @return ResponseEntity with the http status code and an optional error message
    */
   @PostMapping("/{eventId}/remove-account/{accountId}")
   public ResponseEntity removeAccountToEvent(@PathVariable String eventId, @PathVariable String accountId, @RequestParam EventRole role){
      try{
         eventControllerService.removeAccountFromEvent(eventId,accountId,role);
         return new ResponseEntity(HttpStatus.OK);
      }catch (NoSuchElementException ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
      }
   }

   /**
    * not implemented yet
    * @param eventId the id of the event to check in
    * @param accountId the id of the account to be checked in
    * @return HttpStatus whether the process was successfully or not
    */
   @PostMapping("/{eventId}/attendees/check-in/{accountId}")
   public HttpStatus attendeesCheckIn(@PathVariable String eventId, @PathVariable String accountId){
      try{
         //TODO: Implement
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }

   /**
    * not implemented yet
    * @param eventId the id of the event to add an attachment to
    * @return HttpStatus whether the process was successfully or not
    */
   @PostMapping("/{eventId}/attachments")
   public HttpStatus addAttachment(@PathVariable String eventId){
      try{
         //TODO: Implement
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }

   /**
    * not implemented yet
    * @param eventId the id of the event to delete an attachment from
    * @return HttpStatus whether the process was successfully or not
    */
   @DeleteMapping("/{eventId}/attachments")
   public HttpStatus deleteAttachment(@PathVariable String eventId){
      try{
         //TODO: Implement
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }
}
