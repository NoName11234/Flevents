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

    /**
     * @return list of all events
     */
    public List<EventInformation> getEvents() {
        return eventService.getEvents();
    }

    /**
     * @param eventId the id of the event
     * @return information of the event with the given id
     */
    public EventInformation getEventById(String eventId){
        return eventService.getEventInformationById(eventId);
    }

    /**
     * @param eventId the id of the event to delete
     * @return the status whether deleting was succesfull or not
     */
    public HttpStatus deleteEvent(String eventId){
        return eventService.deleteEvent(eventId);
    }

    /**
     * @param eventId the id of the event to get the list of attendees from
     * @return ResponseEntity with a list with the attendees of the event
     */
    public ResponseEntity getAttendees(String eventId){
        return eventService.getAttendees(eventId);
    }

    /**
     * @param eventId the id of the event to get the list of organizers from
     * @return ResponseEntity with a list with the organizers of the event
     */
    public ResponseEntity getOrganizers(String eventId){
        return eventService.getOrganizers(eventId);
    }

    /**
     * creates an event
     * @param event the event to be created
     * @return ResponseEntity with information of the created event
     */
    public ResponseEntity createEvent(Event event){
        return eventService.createEvent(event);
    }

    /**
     * sets the event of a given id to the specified event
     * @param eventId the id of the event to be set
     * @param event the event to be set to the given id
     * @return ResponseEntity with information of the process
     */
    public ResponseEntity setEventById(String eventId, Event event){
        return eventService.setEventById(eventId,event);
    }

    /**
     * sends an invitation email to the given email with a link to register with the specified role to the specified event
     * @param eventId the id of the event to send an invitation to
     * @param email the email to send the invitation link to
     * @param role the role which gets assigned to the invited person
     * @return ResponseEntity with information of the process
     */
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

    /**
     * @param eventId the id of the event to add the account to
     * @param accountId the id of the account to be added
     * @param token the token in the invitation link to verify the invitation
     * @return ResponseEntity with information of the process
     */
    public ResponseEntity addAccountToEvent(String eventId, String accountId, String token){
        FleventsAccount account = fleventsAccountService.getUser(accountId).get();
        return eventService.addAccountToEvent(eventId, accountId, token, account);
    }

    /**
     * @param eventId the id of the event where the role of the account has to be changed
     * @param accountId the id of the account where the role has to be changed
     * @param role the role to change the role of the account to
     * @return ResponseEntity with information of the process
     */
    public ResponseEntity changeRole(String eventId, String accountId, EventRole role){
        return eventService.changeRole(eventId, accountId, role);
    }

    /**
     * diabled at the moment
     * @param eventId  the id of the event to create the account in
     * @param eMail the email of the account to be created
     * @return PesponseEntity with information of the process
     */
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

    /**
     * @param eventId the id of the event to add the anonymous account to
     * @param account the anonymous account to be added
     * @return HttpStatus with the information whether the process was successfull
     */
    public HttpStatus addAnonymousAccountToEvent(String eventId, FleventsAccount account){
        return eventService.addAnonymousAccountToEvent(eventId, account);
    }

    /**
     * @param eventId the id of the event to remove the account from
     * @param accountId the id of the account to be removed from the event
     * @param role the role of the account
     * @return ResponseEntity with information of the process
     */
    public ResponseEntity removeAccountToEvent(String eventId, String accountId, EventRole role){
        return eventService.removeAccountToEvent(eventId, accountId, role);
    }
}
