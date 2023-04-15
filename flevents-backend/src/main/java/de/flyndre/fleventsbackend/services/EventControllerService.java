package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.EventRegistration;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventControllerService {
    private EventService eventService;
    private FleventsAccountService fleventsAccountService;
    public EventControllerService(EventService eventService, FleventsAccountService fleventsAccountService){
        this.eventService = eventService;
        this.fleventsAccountService = fleventsAccountService;
    }
    public List<EventInformation> getEvents() {
        return eventService.getEvents();
    }

    public EventInformation getEventById(String eventId){
        return eventService.getEventInformationById(eventId);
    }

    public HttpStatus deleteEvent(String eventId){
        return eventService.deleteEvent(eventId);
    }

    public ResponseEntity getAttendees(String eventId){
        return eventService.getAttendees(eventId);
    }

    public ResponseEntity getOrganizers(String eventId){
        return eventService.getOrganizers(eventId);
    }

    public ResponseEntity createEvent(Event event){
        return eventService.createEvent(event);
    }

    public ResponseEntity setEventById(String eventId, Event event){
        return eventService.setEventById(eventId,event);
    }

    public ResponseEntity inviteToEvent(String eventId, String email, EventRole role) {
        Optional<Event> event = eventService.getEventById(eventId);
        Optional<FleventsAccount> accountOptional = fleventsAccountService.getAccountByMail(email);
        if(event.isEmpty()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        FleventsAccount account = null;
        if(accountOptional.isPresent()){
            account=accountOptional.get();
        }else {
            account =fleventsAccountService.saveAccount(new FleventsAccount(null, null,null,email,null,null,null,null));
        }

        return eventService.inviteToEvent(email, role, account, event);
    }

    public ResponseEntity addAccountToEvent(String eventId, String accountId, String token){
        FleventsAccount account = fleventsAccountService.getUser(accountId).get();
        return eventService.addAccountToEvent(eventId, accountId, token, account);
    }

    public ResponseEntity changeRole(String eventId, String accountId, EventRole role){
        return eventService.changeRole(eventId, accountId, role);
    }

    public ResponseEntity createAndAddAccountToEvent(String eventId, String eMail){

        //TODO: Implement
        if(fleventsAccountService.getAccountByMail(eMail).isPresent()){
            return new ResponseEntity<>("Email exists already",HttpStatus.BAD_REQUEST);
        }
        FleventsAccount account = new FleventsAccount();
        account.setEmail(eMail);
        account = fleventsAccountService.saveAccount(account);
        return eventService.createAndAddAccountToEvent(eventId, eMail, account);
    }

    public HttpStatus addAnonymousAccountToEvent(String eventId, FleventsAccount account){
        return eventService.addAnonymousAccountToEvent(eventId, account);
    }

    public ResponseEntity removeAccountToEvent(String eventId, String accountId, EventRole role){
        return eventService.removeAccountToEvent(eventId, accountId, role);
    }
}
