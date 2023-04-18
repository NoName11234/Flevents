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
    * @return list of all events
    */
   @GetMapping
   public List<EventInformation> getEvents() {
      return eventControllerService.getEvents().stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
   }

   /**
    * @param eventId the id of the event
    * @return information of the event with the given id
    */
   @GetMapping("/{eventId}")
   public EventInformation getEventById(@PathVariable String eventId){
      return mapper.map(eventControllerService.getEventById(eventId),EventInformation.class);
   }

   /**
    * @param eventId the id of the event to delete
    * @return the status whether deleting was succesfull or not
    */
   @DeleteMapping("/{eventId}")
   public ResponseEntity deleteEvent(@PathVariable String eventId){
      eventControllerService.deleteEvent(eventId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   /**
    * @param eventId the id of the event to get the list of attendees from
    * @return ResponseEntity with a list with the attendees of the event
    */
   @GetMapping("/{eventId}/attendees")
   public ResponseEntity getAttendees(@PathVariable String eventId){
      return new ResponseEntity<>(eventControllerService.getAttendees(eventId).stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
   }

   /**
    * @param eventId the id of the event to get the list of organizers from
    * @return ResponseEntity with a list with the organizers of the event
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
    * sets the event of a given id to the specified event
    * @param eventId the id of the event to be set
    * @param event the event to be set to the given id
    * @return ResponseEntity with information of the process
    */
   @PostMapping("/{eventId}")
   public ResponseEntity setEventById(@PathVariable String eventId, @RequestBody Event event){
      return new ResponseEntity<>(mapper.map(eventControllerService.setEventById(eventId,event),EventInformation.class),HttpStatus.OK);
   }

   /**
    * sends an invitation email to the given email with a link to register with the specified role to the specified event
    * @param eventId the id of the event to send an invitation to
    * @param email the email to send the invitation link to
    * @param role the role which gets assigend to the invited person
    * @return ResponseEntity with information of the process
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
    * @param eventId the id of the event to add the account to
    * @param accountId the id of the account to be added
    * @param token the token in the invitation link to verify the invitation
    * @return ResponseEntity with information of the process
    */
   @PostMapping("/{eventId}/add-account/{accountId}")
   public ResponseEntity addAccountToEvent(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam(required = false) String token){
      eventControllerService.addAccountToEvent(eventId, accountId, token);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    *
    * @param eventId
    * @param accountId
    * @param fromRole
    * @param toRole
    * @return
    */
   @PostMapping("/{eventId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam EventRole fromRole, @RequestParam EventRole toRole){
      eventControllerService.changeRole(eventId, accountId, fromRole,toRole);
      return new ResponseEntity(HttpStatus.OK);
   }


   /**
    * diabled at the moment
    * @param eventId  the id of the event to create the account in
    * @param eMail the email of the account to be created
    * @return PesponseEntity with information of the process
    */
   //@PostMapping("/{eventId}/create-account")
   /*public ResponseEntity createAndAddAccountToEvent(@PathVariable String eventId, @RequestBody String eMail){
      return eventControllerService.createAndAddAccountToEvent(eventId, eMail);
   }*/


   /**
    * @param eventId the id of the event to add the anonymous account to
    * @param account the anonymous account to be added
    * @return HttpStatus with the information whether the process was successfull
    */
   @PostMapping("/{eventId}/add-account/add-anonymous")
   public ResponseEntity addAnonymousAccountToEvent(@PathVariable String eventId, @RequestBody FleventsAccount account){
      eventControllerService.addAnonymousAccountToEvent(eventId, account);
      return new ResponseEntity(HttpStatus.OK);
   }

   /**
    * @param eventId the id of the event to remove the account from
    * @param accountId the id of the account to be removed from the event
    * @param role the role of the account
    * @return ResponseEntity with information of the process
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
    * @return HttpStatus whether the process was successfull or not
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
    * @return HttpStatus whether the process was successfull or not
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
    * @return HttpStatus whether the process was successfull or not
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
