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

   @GetMapping
   public List<EventInformation> getEvents() {
      return eventControllerService.getEvents();
   }

   @GetMapping("/{eventId}")
   public EventInformation getEventById(@PathVariable String eventId){
      return eventControllerService.getEventById(eventId);
   }

   @DeleteMapping("/{eventId}")
   public HttpStatus deleteEvent(@PathVariable String eventId){
      return eventControllerService.deleteEvent(eventId);
   }
   @GetMapping("/{eventId}/attendees")
   public ResponseEntity getAttendees(@PathVariable String eventId){
      return eventControllerService.getAttendees(eventId);
   }
   @GetMapping("/{eventId}/organizers")
   public ResponseEntity getOrganizers(@PathVariable String eventId){
      return eventControllerService.getOrganizers(eventId);
   }


   @GetMapping("/{eventId}/attachments")
   public List<URI> getAttachment(@PathVariable String eventId){
      //TODO: Implement
      return new ArrayList<>();
   }

   @PostMapping
   public ResponseEntity createEvent(@RequestBody Event event){
      return eventControllerService.createEvent(event);
   }

   @PostMapping("/{eventId}")
   public ResponseEntity setEventById(@PathVariable String eventId, @RequestBody Event event){
      return eventControllerService.setEventById(eventId,event);
   }

   @PostMapping("/{eventId}/invite")
   public ResponseEntity inviteToEvent(@PathVariable String eventId, @RequestParam() String email,@RequestParam EventRole role){
      return eventControllerService.inviteToEvent(eventId, email, role);
   }

   @PostMapping("/{eventId}/add-account/{accountId}")
   public ResponseEntity addAccountToEvent(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam(required = false) String token){
      return eventControllerService.addAccountToEvent(eventId, accountId, token);
   }

   @PostMapping("/{eventId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam EventRole role){
      return eventControllerService.changeRole(eventId, accountId, role);
   }


   //@PostMapping("/{eventId}/create-account")
   public ResponseEntity createAndAddAccountToEvent(@PathVariable String eventId, @RequestBody String eMail){
      return eventControllerService.createAndAddAccountToEvent(eventId, eMail);
   }


   @PostMapping("/{eventId}/add-account/add-anonymous")
   public HttpStatus addAnonymousAccountToEvent(@PathVariable String eventId, @RequestBody FleventsAccount account){
      return eventControllerService.addAnonymousAccountToEvent(eventId, account);
   }

   @PostMapping("/{eventId}/remove-account/{accountId}")
   public ResponseEntity removeAccountToEvent(@PathVariable String eventId, @PathVariable String accountId, @RequestParam EventRole role){
      return eventControllerService.removeAccountToEvent(eventId, accountId, role);
   }

   @PostMapping("/{eventId}/attendees/check-in/{accountId}")
   public HttpStatus attendeesCheckIn(@PathVariable String eventId, @PathVariable String accountId){
      try{
         //TODO: Implement
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }

   @PostMapping("/{eventId}/attachments")
   public HttpStatus addAttachment(@PathVariable String eventId){
      try{
         //TODO: Implement
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }
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
