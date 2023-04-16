package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.EventRegistration;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import de.flyndre.fleventsbackend.repositories.EventRepository;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import de.flyndre.fleventsbackend.services.EMailService;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import de.flyndre.fleventsbackend.services.EventControllerService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {

private EventControllerService eventControllerService;
   public EventController(EventControllerService eventControllerService){
      this.eventControllerService = eventControllerService;
   }

   /**
    * @return list of all events
    */
   @GetMapping
   public List<EventInformation> getEvents() {
      return eventControllerService.getEvents();
   }

   /**
    * @param eventId the id of the event
    * @return information of the event with the given id
    */
   @GetMapping("/{eventId}")
   public EventInformation getEventById(@PathVariable String eventId){
      return eventControllerService.getEventById(eventId);
   }

   /**
    * @param eventId the id of the event to delete
    * @return the status whether deleting was succesfull or not
    */
   @DeleteMapping("/{eventId}")
   public HttpStatus deleteEvent(@PathVariable String eventId){
      return eventControllerService.deleteEvent(eventId);
   }

   /**
    * @param eventId the id of the event to get the list of attendees from
    * @return ResponseEntity with a list with the attendees of the event
    */
   @GetMapping("/{eventId}/attendees")
   public ResponseEntity getAttendees(@PathVariable String eventId){
      return eventControllerService.getAttendees(eventId);
   }

   /**
    * @param eventId the id of the event to get the list of organizers from
    * @return ResponseEntity with a list with the organizers of the event
    */
   @GetMapping("/{eventId}/organizers")
   public ResponseEntity getOrganizers(@PathVariable String eventId){
      return eventControllerService.getOrganizers(eventId);
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
    * creates an event
    * @param event the event to be created
    * @return ResponseEntity with information of the created event
    */
   @PostMapping
   public ResponseEntity createEvent(@RequestBody Event event){
      return eventControllerService.createEvent(event);
   }

   /**
    * sets the event of a given id to the specified event
    * @param eventId the id of the event to be set
    * @param event the event to be set to the given id
    * @return ResponseEntity with information of the process
    */
   @PostMapping("/{eventId}")
   public ResponseEntity setEventById(@PathVariable String eventId, @RequestBody Event event){
      return eventControllerService.setEventById(eventId,event);
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
      return eventControllerService.inviteToEvent(eventId, email, role);
   }

   /**
    * @param eventId the id of the event to add the account to
    * @param accountId the id of the account to be added
    * @param token the token in the invitation link to verify the invitation
    * @return ResponseEntity with information of the process
    */
   @PostMapping("/{eventId}/add-account/{accountId}")
   public ResponseEntity addAccountToEvent(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam(required = false) String token){
      return eventControllerService.addAccountToEvent(eventId, accountId, token);
   }

   /**
    * @param eventId the id of the event where the role of the account has to be changed
    * @param accountId the id of the account where the role has to be changed
    * @param role the role to change the role of the account to
    * @return ResponseEntity with information of the process
    */
   @PostMapping("/{eventId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam EventRole role){
      return eventControllerService.changeRole(eventId, accountId, role);
   }


   /**
    * diabled at the moment
    * @param eventId  the id of the event to create the account in
    * @param eMail the email of the account to be created
    * @return PesponseEntity with information of the process
    */
   //@PostMapping("/{eventId}/create-account")
   public ResponseEntity createAndAddAccountToEvent(@PathVariable String eventId, @RequestBody String eMail){
      return eventControllerService.createAndAddAccountToEvent(eventId, eMail);
   }


   /**
    * @param eventId the id of the event to add the anonymous account to
    * @param account the anonymous account to be added
    * @return HttpStatus with the information whether the process was successfull
    */
   @PostMapping("/{eventId}/add-account/add-anonymous")
   public HttpStatus addAnonymousAccountToEvent(@PathVariable String eventId, @RequestBody FleventsAccount account){
      return eventControllerService.addAnonymousAccountToEvent(eventId, account);
   }

   /**
    * @param eventId the id of the event to remove the account from
    * @param accountId the id of the account to be removed from the event
    * @param role the role of the account
    * @return ResponseEntity with information of the process
    */
   @PostMapping("/{eventId}/remove-account/{accountId}")
   public ResponseEntity removeAccountToEvent(@PathVariable String eventId, @PathVariable String accountId, @RequestParam EventRole role){
      return eventControllerService.removeAccountToEvent(eventId, accountId, role);
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
