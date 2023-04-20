package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
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
     * @return the event object
     */
    public Event getEventById(String eventId){
        return eventService.getEventById(eventId);
    }

    /**
     * @param eventId the id of the event to delete
     */
    public void deleteEvent(String eventId){
        eventService.deleteEvent(getEventById(eventId));
    }

    /**
     * @param eventId the id of the event to get the list of attendees from
     * @return list with the accounts of the attendees of the event
     */
    public List<FleventsAccount> getAttendees(String eventId){
        return eventService.getAttendees(getEventById(eventId));
    }

    /**
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
    public Event createEvent(Event event,String accountId, String organizationId){
        return eventService.createEventInOrganization(event,accountService.getAccountById(accountId),organizationService.getOrganizationById(organizationId));
    }

    /**
     * sets the event of a given id to the specified event
     * @param eventId the id of the event to be set
     * @param event the event to be set to the given id
     * @return updated event
     */
    public Event setEventById(String eventId, Event event){
        return eventService.setEventById(eventId,event);
    }

    /**
     * sends an invitation email to the given email with a link to register with the specified role to the specified event
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
        eMailService.sendEventInvitaion(event,email,token.getToken());
    }

    /**
     * @param eventId the id of the event to add the account to
     * @param accountId the id of the account to be added
     * @param token the token in the invitation link to verify the invitation
     */
    public void addAccountToEvent(String eventId, String accountId, String token){
        Event event = getEventById(eventId);
        FleventsAccount account = accountService.getAccountById(accountId);
        InvitationToken invitationToken = invitationTokenService.validate(token);
        eventService.changeRole(event,account,EventRole.invited,EventRole.valueOf(invitationToken.getRole()));
    }

    /**
     *changes the role of a specified account in an event
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
     * @param eventId the id of the event to add the anonymous account to
     * @param account the anonymous account to be added
     */
    public void addAnonymousAccountToEvent(String eventId, FleventsAccount account){
        account = accountService.createAccount(account);
        eventService.addAccountToEvent(getEventById(eventId),account,EventRole.guest);
    }

    /**
     * @param eventId the id of the event to remove the account from
     * @param accountId the id of the account to be removed from the event
     * @param role the role of the account
     */
    public void removeAccountFromEvent(String eventId, String accountId, EventRole role){
        eventService.removeAccountFromEvent(getEventById(eventId),accountService.getAccountById(accountId),role);
    }

    /**
     * Sets the attendees status to checkedIn.
     * @param eventId the id of the event to check in
     * @param accountId the id of the account to be checked in
     */
    public void attendeesCheckIn(String eventId, String accountId){
        eventService.attendeesCheckIn(eventId, accountId);
    }

    /**
     * Gets all checked-In attendees
     * @param eventId the if of the event to get the checked-In attendees from
     * @return a list with all checked-In attendees
     */
    public List<AccountInformation> getCheckedIn(String eventId){
        return eventService.getCheckedIn(eventId);
    }
}
