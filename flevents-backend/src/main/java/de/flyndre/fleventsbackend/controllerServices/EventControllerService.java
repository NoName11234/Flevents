package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.*;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the service for the EventController class.
 * It provides methods regarding accounts. The methods of the EventController are mapped on them.
 */

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
     * Returns a list of all existing events.
     * @return list of all events
     */
    public List<Event> getEvents() {
        return eventService.getEvents();
    }

    /**
     * Returns the event with the given id.
     * @param eventId the id of the event
     * @return event with the given id
     */
    public Event getEventById(String eventId){
        return eventService.getEventById(eventId);
    }

    /**
     * Delets the event with the given id.
     * @param eventId the id of the event to delete
     */
    public void deleteEvent(String eventId){
        eventService.deleteEvent(getEventById(eventId));
    }

    /**
     * Returns a list of all attendees from the specified event.
     * @param eventId the id of the event to get the list of attendees from
     * @return list with the accounts of the attendees of the event
     */
    public List<FleventsAccount> getAttendees(String eventId){
        return eventService.getAttendees(getEventById(eventId));
    }

    /**
     * Returns a list of all organizers from the specified event.
     * @param eventId the id of the event to get the list of organizers from
     * @return list of accounts with the organizers of the event
     */
    public List<FleventsAccount> getOrganizers(String eventId){
        return eventService.getOrganizers(getEventById(eventId));
    }

    /**
     * @param event the event to be created
     * @param accountId the id of the account which is registered as organizer
     * @param organizationId the id of the organization in which to create the event
     * @return the created event object
     */
    public Event createEvent(Event event, String accountId, String organizationId){
        return eventService.createEventInOrganization(event,accountService.getAccountById(accountId),organizationService.getOrganizationById(organizationId));
    }

    /**
     * Overwrites the event specified with a given id with the specified event.
     * @param eventId the id of the event to be set
     * @param event the event to be set to the given id
     * @return updated event
     */
    public Event setEventById(String eventId, Event event){
        return eventService.setEventById(eventId,event);
    }

    /**
     * Sends an invitation email to the given email with a link to register with the specified role to the specified event.
     * @param eventId the id of the event to send an invitation to
     * @param email the email to send the invitation link to
     * @param role the role which gets assigned to the invited person
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
        eMailService.sendEventInvitaion(event,email,token.toString());
    }

    /**
     * Adds the specified account to the specified event. The account will have the specified role.
     * @param eventId the id of the event to add the account to
     * @param accountId the id of the account to be added
     * @param token the token in the invitation link to verify the invitation
     */
    public void acceptInvitation(String eventId, String accountId, String token){
        Event event = getEventById(eventId);
        FleventsAccount account = accountService.getAccountById(accountId);
        InvitationToken invitationToken = invitationTokenService.validate(token);
        eventService.acceptInvitation(event,account,EventRole.valueOf(invitationToken.getRole()));
        invitationTokenService.deleteToken(invitationToken);
    }

    /**
     * Changes the role of a specified account in an event.
     * @param eventId the id of the event with the account
     * @param accountId the id of the account which role has to be changed
     * @param fromRole the role of the account before the change
     * @param toRole the role to change the account to
     */
    public void changeRole(String eventId, String accountId, EventRole fromRole, EventRole toRole){
        eventService.changeRole(getEventById(eventId), accountService.getAccountById(accountId), fromRole,toRole);
    }

    /*
    public ResponseEntity createAndAddAccountToEvent(String eventId, String eMail){

    }*/

    /**
     * Adds an anonymous account to an event.
     * @param eventId the id of the event to add the anonymous account to
     * @param account the anonymous account to be added
     */
    public void addAnonymousAccountToEvent(String eventId, FleventsAccount account){
        account = accountService.createAccount(account);
        eventService.addAccountToEvent(getEventById(eventId),account,EventRole.guest);
    }

    /**
     * Removes a specified account from the specified event. The account needs to have the specified role.
     * @param eventId the id of the event to remove the account from
     * @param accountId the id of the account to be removed from the event
     * @param role the role of the account
     */
    public void removeAccountFromEvent(String eventId, String accountId, EventRole role){
        eventService.removeAccountFromEvent(getEventById(eventId),accountService.getAccountById(accountId),role);
    }
}
