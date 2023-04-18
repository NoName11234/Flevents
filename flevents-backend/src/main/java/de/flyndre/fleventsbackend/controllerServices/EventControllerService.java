package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.*;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventControllerService {
    private EventService eventService;
    private FleventsAccountService fleventsAccountService;
    private final OrganizationService organizationService;
    private  final FleventsAccountService accountService;
    private final EMailServiceImpl eMailService;
    private final InvitationTokenService invitationTokenService;
    public EventControllerService(EventService eventService, FleventsAccountService fleventsAccountService, OrganizationService organizationService, FleventsAccountService accountService, EMailServiceImpl eMailService, InvitationTokenService invitationTokenService){
        this.eventService = eventService;
        this.fleventsAccountService = fleventsAccountService;
        this.organizationService = organizationService;
        this.accountService = accountService;
        this.eMailService = eMailService;
        this.invitationTokenService = invitationTokenService;
    }

    /**
     * @return list of all events
     */
    public List<Event> getEvents() {
        return eventService.getEvents();
    }

    /**
     * @param eventId the id of the event
     * @return information of the event with the given id
     */
    public Event getEventById(String eventId){
        return eventService.getEventById(eventId);
    }

    /**
     * @param eventId the id of the event to delete
     * @return the status whether deleting was succesfull or not
     */
    public void deleteEvent(String eventId){
        eventService.deleteEvent(getEventById(eventId));
    }

    /**
     * @param eventId the id of the event to get the list of attendees from
     * @return ResponseEntity with a list with the attendees of the event
     */
    public List<FleventsAccount> getAttendees(String eventId){
        return eventService.getAttendees(getEventById(eventId));
    }

    /**
     * @param eventId the id of the event to get the list of organizers from
     * @return ResponseEntity with a list with the organizers of the event
     */
    public List<FleventsAccount> getOrganizers(String eventId){
        return eventService.getOrganizers(getEventById(eventId));
    }

    /**
     * creates an event
     * @param event the event to be created
     * @return ResponseEntity with information of the created event
     */
    public Event createEvent(Event event,String accountId, String organizationId){
        return eventService.createEventInOrganization(event,accountService.getAccountById(accountId),organizationService.getOrganizationById(organizationId));
    }

    /**
     * sets the event of a given id to the specified event
     * @param eventId the id of the event to be set
     * @param event the event to be set to the given id
     * @return ResponseEntity with information of the process
     */
    public Event setEventById(String eventId, Event event){
        return eventService.setEventById(eventId,event);
    }

    /**
     * sends an invitation email to the given email with a link to register with the specified role to the specified event
     * @param eventId the id of the event to send an invitation to
     * @param email the email to send the invitation link to
     * @param role the role which gets assigned to the invited person
     * @return ResponseEntity with information of the process
     */
    public void inviteToEvent(String eventId, String email, EventRole role) throws MessagingException {
        Event event = getEventById(eventId);
        FleventsAccount account;
        try {
            account = fleventsAccountService.getAccountByMail(email);
        }catch (NoSuchElementException ex) {
            account = fleventsAccountService.createAnonymousAccount(email);
        }
        EventRegistration registration = eventService.addAccountToEvent(event,account,EventRole.invited);
        InvitationToken token = invitationTokenService.saveToken(new InvitationToken(role.toString()));
        eMailService.sendEventInvitaion(event,email,token.getToken());
    }

    /**
     * @param eventId the id of the event to add the account to
     * @param accountId the id of the account to be added
     * @param token the token in the invitation link to verify the invitation
     * @return ResponseEntity with information of the process
     */
    public void addAccountToEvent(String eventId, String accountId, String token){
        Event event = getEventById(eventId);
        FleventsAccount account = accountService.getAccountById(accountId);
        InvitationToken invitationToken = invitationTokenService.validate(token);
        eventService.changeRole(event,account,EventRole.invited,EventRole.valueOf(invitationToken.getRole()));
    }

    /**
     *
     * @param eventId
     * @param accountId
     * @param fromRole
     * @param toRole
     */
    public void changeRole(String eventId, String accountId, EventRole fromRole, EventRole toRole){
        eventService.changeRole(getEventById(eventId), accountService.getAccountById(accountId), fromRole,toRole);
    }

    /**
     * diabled at the moment
     * @param eventId  the id of the event to create the account in
     * @param eMail the email of the account to be created
     * @return PesponseEntity with information of the process
     */
    /*
    public ResponseEntity createAndAddAccountToEvent(String eventId, String eMail){

    }*/

    /**
     * @param eventId the id of the event to add the anonymous account to
     * @param account the anonymous account to be added
     * @return HttpStatus with the information whether the process was successfull
     */
    public void addAnonymousAccountToEvent(String eventId, FleventsAccount account){
        account = accountService.createAccount(account);
        eventService.addAccountToEvent(getEventById(eventId),account,EventRole.guest);
    }

    /**
     * @param eventId the id of the event to remove the account from
     * @param accountId the id of the account to be removed from the event
     * @param role the role of the account
     * @return ResponseEntity with information of the process
     */
    public void removeAccountFromEvent(String eventId, String accountId, EventRole role){
        eventService.removeAccountFromEvent(getEventById(eventId),accountService.getAccountById(accountId),role);
    }
}
