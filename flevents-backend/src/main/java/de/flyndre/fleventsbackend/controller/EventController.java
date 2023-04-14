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


   private final EventRepository eventRepository;
   private final FleventsAccountRepository accountRepository;
   private final EventRegistrationRepository eventRegistrationRepository;
   private final ModelMapper mapper;
   private final EMailService eMailService;
   public EventController(ModelMapper mapper, EventRepository eventRepository, FleventsAccountRepository accountRepository, EventRegistrationRepository eventRegistrationRepository, EMailServiceImpl eMailService){
      this.mapper=mapper;
      this.eventRepository = eventRepository;
      this.accountRepository = accountRepository;
      this.eventRegistrationRepository = eventRegistrationRepository;
      this.eMailService = eMailService;
   }

   @GetMapping
   public List<EventInformation> getEvents() {
      List<Event> events = eventRepository.findAll();
      return events.stream().map((event) -> mapper.map(event, EventInformation.class)).collect(Collectors.toList());
   }

   @GetMapping("/{eventId}")
   public EventInformation getEventById(@PathVariable String eventId){
      Event event = eventRepository.findById(eventId).get();
      return mapper.map(event, EventInformation.class);
   }

   @DeleteMapping("/{eventId}")
   public HttpStatus deleteEvent(@PathVariable String eventId){
      Event ev = eventRepository.findById(eventId).get();
      eventRegistrationRepository.deleteAll(ev.getAttendees());
      eventRepository.delete(eventRepository.findById(eventId).get());
      return HttpStatus.OK;
   }
   @GetMapping("/{eventId}/attendees")
   public ResponseEntity getAttendees(@PathVariable String eventId){
      //TODO: Implement
      List<EventRegistration> registrations = eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.guest);
      registrations.addAll(eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.invited));
      registrations.addAll(eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.attendee));
      registrations.addAll(eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.tutor));
      return new ResponseEntity(registrations.stream().map(registration ->{
              AccountInformation information = mapper.map(registration.getAccount(),AccountInformation.class);
              information.setRole(registration.getRole());
              return information;
      }).collect(Collectors.toList()),HttpStatus.OK);
   }
   @GetMapping("/{eventId}/organizers")
   public ResponseEntity getOrganizers(@PathVariable String eventId){
      //TODO: Implement
      List<EventRegistration> registrations = eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.organizer);
      return new ResponseEntity(registrations.stream().map(registration ->{
         AccountInformation information = mapper.map(registration.getAccount(),AccountInformation.class);
         information.setRole(registration.getRole());
         return information;
      }).collect(Collectors.toList()),HttpStatus.OK);
   }


   @GetMapping("/{eventId}/attachments")
   public List<URI> getAttachment(@PathVariable String eventId){
      //TODO: Implement
      return new ArrayList<>();
   }

   @PostMapping
   public ResponseEntity createEvent(@RequestBody Event event){
      //TODO: Implement
      event.setUuid(null);
      return new ResponseEntity<>(mapper.map(eventRepository.save(event),EventInformation.class),HttpStatus.CREATED);
   }

   @PostMapping("/{eventId}")
   public ResponseEntity setEventById(@PathVariable String eventId, @RequestBody Event event){
      try{
         Event srcEvent = eventRepository.findById(eventId).get();
         srcEvent.merge(event);
         return new ResponseEntity<>(mapper.map(eventRepository.save(srcEvent),EventInformation.class),HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   @PostMapping("/{eventId}/invite")
   public ResponseEntity inviteToEvent(@PathVariable String eventId, @RequestParam() String email,@RequestParam EventRole role){
      //TODO: Implement
      Optional<Event> event = eventRepository.findById(eventId);
      Optional<FleventsAccount> accountOptional = accountRepository.findByEmail(email);
      if(event.isEmpty()){
         return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }
      FleventsAccount account = null;
      if(accountOptional.isPresent()){
         account=accountOptional.get();
      }else {
         account= accountRepository.save(new FleventsAccount(null, null,null,email,null,null,null,null));
      }
      EventRegistration registration = eventRegistrationRepository.save(new EventRegistration(null,event.get(),account,EventRole.invited));
      try {
         eMailService.sendEventInvitaion(event.get(),email, registration.getId() +role.toString());
      } catch (MessagingException e) {
         return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
      }


      return new ResponseEntity<>(HttpStatus.OK);

   }
   @PostMapping("/{eventId}/add-account/{accountId}")
   public ResponseEntity addAccountToEvent(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam(required = false) String token){

         //TODO:
      if(token==null){
         Event event = eventRepository.findById(eventId).get();
         FleventsAccount account = accountRepository.findById(accountId).get();
         List<FleventsAccount> accounts = event.getOrganization().getAccounts().stream().map(organizationAccount -> organizationAccount.getAccount()).collect(Collectors.toList());
         if(accounts.contains(account)){
            eventRegistrationRepository.save(new EventRegistration(null,event,account,EventRole.attendee));
            return new ResponseEntity(HttpStatus.OK);
         }
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      Optional<EventRegistration> optionalEventRegistration = eventRegistrationRepository.findById(token.substring(0,32));
      if(!optionalEventRegistration.isPresent()){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      EventRole role = EventRole.valueOf(token.substring(32));
      if(eventRegistrationRepository.findByAccount_UuidAndEvent_UuidAndRole(accountId,eventId,role).isPresent()) {
         return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }
         FleventsAccount account = accountRepository.findById(accountId).get();
         EventRegistration registration = optionalEventRegistration.get();
         registration.setAccount(account);
         registration.setRole(role);
         eventRegistrationRepository.save(registration);
         return new ResponseEntity<>(HttpStatus.OK);
   }

   @PostMapping("/{eventId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam EventRole role){
      EventRole currEventRole = EventRole.guest;
      Event event = eventRepository.findById(eventId).get();
      for(int i = 0; i < event.getAttendees().size(); i++){
         if(event.getAttendees().get(i).getAccount().getUuid().equals(accountId) && event.getAttendees().get(i).getRole() != EventRole.organizer){
            currEventRole = event.getAttendees().get(i).getRole();
         }
      }
      EventRegistration reg = eventRegistrationRepository.findByAccount_UuidAndEvent_UuidAndRole(accountId, eventId, currEventRole).get();
      reg.setRole(role);
      eventRegistrationRepository.save(reg);

      return new ResponseEntity<>(HttpStatus.OK);
   }


   //@PostMapping("/{eventId}/create-account")
   public ResponseEntity createAndAddAccountToEvent(@PathVariable String eventId, @RequestBody String eMail){

      //TODO: Implement
      if(accountRepository.findByEmail(eMail).isPresent()){
         return new ResponseEntity<>("Email exists already",HttpStatus.BAD_REQUEST);
      }
      FleventsAccount account = new FleventsAccount();
      account.setEmail(eMail);
      account = accountRepository.save(account);
      Event event = eventRepository.findById(eventId).get();

      eventRegistrationRepository.save(new EventRegistration(null,event,account,EventRole.guest));
      return new ResponseEntity<>(account,HttpStatus.OK);

   }


   @PostMapping("/{eventId}/add-account/add-anonymous")
   public HttpStatus addAnonymousAccountToEvent(@PathVariable String eventId, @RequestBody FleventsAccount account){
      try{
         //TODO:
         account.setUuid(null);
         Event event = eventRepository.findById(eventId).get();
         eventRegistrationRepository.save(new EventRegistration(null,event,account,EventRole.guest));
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }
   @PostMapping("/{eventId}/remove-account/{accountId}")
   public ResponseEntity removeAccountToEvent(@PathVariable String eventId, @PathVariable String accountId, @RequestParam EventRole role){
      Optional<EventRegistration> registration= eventRegistrationRepository.findByAccount_UuidAndEvent_UuidAndRole(accountId, eventId, role);
      if(registration.isEmpty()){
         return new ResponseEntity<>("Found no account with these specification on this event.",HttpStatus.BAD_REQUEST);
      }
      if(EventRole.organizer.equals(role)
              && eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.organizer).size()<=1
      ){
         return new ResponseEntity<>("Can't delete last organizer",HttpStatus.BAD_REQUEST);
      }
      eventRegistrationRepository.delete(registration.get());
      return new ResponseEntity<>("Deleted!",HttpStatus.OK);
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
