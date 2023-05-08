package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.EventPreview;
import de.flyndre.fleventsbackend.services.*;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * This Class is the service for the EventController class.
 * It provides methods regarding accounts. The methods of the EventController are mapped on them.
 * @author Lukas Burkhardt
 * @version $I$
 */

@Service
public class EventControllerService {
    private EventService eventService;
    private FleventsAccountService fleventsAccountService;
    private final OrganizationService organizationService;
    private  final FleventsAccountService accountService;
    private final EMailServiceImpl eMailService;
    private final InvitationTokenService invitationTokenService;
    private final PostService postService;
    private final AuthService authService;
    private static ResourceBundle strings = ResourceBundle.getBundle("ConfigStrings");

    public EventControllerService(EventService eventService, FleventsAccountService fleventsAccountService, OrganizationService organizationService, FleventsAccountService accountService, EMailServiceImpl eMailService, InvitationTokenService invitationTokenService, PostService postService, AuthService authService){
        this.eventService = eventService;
        this.fleventsAccountService = fleventsAccountService;
        this.organizationService = organizationService;
        this.accountService = accountService;
        this.eMailService = eMailService;
        this.invitationTokenService = invitationTokenService;
        this.postService = postService;
        this.authService = authService;
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
     * Returns the EventPreview of the specified organization if the given token is valid.
     * @param eventId the id of the event to get the preview from
     * @param token the token to validate the request
     * @return the EventPreview with the data of the event
     */
    public EventPreview getEventPreview(String eventId, String token) throws InvalidAttributesException {
        invitationTokenService.validate(token,eventId);
        Event eve = eventService.getEventById(eventId);
        EventPreview preview = new EventPreview();
        preview.setRole(null);
        preview.setName(eve.getName());
        preview.setUuid(eve.getUuid());
        preview.setDescription(eve.getDescription());
        preview.setImage(eve.getImage());
        preview.setEndTime(eve.getEndTime());
        preview.setLocation(eve.getLocation());
        preview.setStartTime(eve.getStartTime());
        return preview;
    }

    /**
     * Deletes the event with the given id.
     * @param eventId the id of the event to delete
     */
    public void deleteEvent(String eventId){
        Event event = getEventById(eventId);
        event.getPosts().forEach(post -> postService.deletePost(post));
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
     * @throws IllegalArgumentException if the times aren't in the right order.
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
        EventRegistration registration = eventService.addInvitationToEvent(event,account);
        InvitationToken token = invitationTokenService.createToken(eventId,role);
        eMailService.sendEventInvitaion(event,email,token.toString());
    }

    /**
     * Adds the specified account to the specified event. The account will have the specified role.
     * @param eventId the id of the event to add the account to
     * @param accountId the id of the account to be added
     * @param token the token in the invitation link to verify the invitation
     */
    public void acceptInvitation(String eventId, String accountId, String token) throws InvalidAttributesException {
        Event event = getEventById(eventId);
        if(LocalDateTime.now().isAfter(event.getEndTime())){
            throw new IllegalArgumentException(strings.getString("event.EventIsOver"));
        }
        FleventsAccount account = accountService.getAccountById(accountId);
        InvitationToken invitationToken = invitationTokenService.validate(token,eventId);
        eventService.acceptInvitation(event,account,EventRole.valueOf(invitationToken.getRole()));
        invitationTokenService.deleteToken(invitationToken);
    }

    /**
     * invalidates the given InvitationToken
     * @param token the InvitationToken to a specific event
     * @param eventId the EventUuid to the Event
     * */

    public void validateAndDeleteToken(String token, String eventId) throws InvalidAttributesException {
        InvitationToken invitationToken = invitationTokenService.validate(token,eventId);
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
        account = accountService.createAnonymousAccountWithName(account.getEmail(), account.getFirstname(), account.getLastname());
        eventService.addAccountToEvent(getEventById(eventId),account,EventRole.guest);
    }

    /**
     * Registers an anonymous Account to an Event.
     * @param eventId the id of the event to add the anonymous account to
     * @param mailAddress the mail adr ess to be added
     */
    public void registerAnonymousAccountToEvent(String eventId, String mailAddress){
        Event event = getEventById(eventId);
        if(LocalDateTime.now().isAfter(event.getEndTime())){
            throw new IllegalArgumentException(strings.getString("event.EventIsOver"));
        }
        FleventsAccount account = accountService.createAnonymousAccount(mailAddress);
        eventService.addAccountToEvent(event,account,EventRole.attendee);
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

    /**
     * Run every hour and checks if some scheduled mails have to be sent.
     * Triggers the email sending for each event, if the time in the email config is at the same time as the system.
     * Ignoring the values of minute, second and nanosecond.
     * Makes not sure that the emails were sent when something happens while sending them.
     */
    @Scheduled(cron = "1 0 * * * *")
    public void sendAutomaticEmails(){
        List<Event> events = eventService.getEvents();
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        now.minusHours(2);
        for(Event event:events) {
            if (now.equals(event.getMailConfig().getInfoMessageTime().withMinute(0).withSecond(0).withNano(0))) {
                try {
                    eMailService.sendAlertMessage(event);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
            if (now.equals(event.getMailConfig().getFeedbackMessageTime().withMinute(0).withSecond(0).withNano(0))) {
                try {
                    eMailService.sendThankMessage(event);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Validate if the given Authentication matches to the given roles for the given event id.
     * Also including the role of an admin of the Organization of the event.
     * @param auth the Authentication to validate.
     * @param eventid the id of the event in which context the validation should be done.
     * @param roles the event roles that should match.
     * @return true if the given parameters match, false if not.
     */
    public boolean grandEventRole(Authentication auth, String eventid, List<Role> roles){
        Event event = getEventById(eventid);
        return authService.validateRights(auth, roles, eventid)
                || authService.validateRights(auth, List.of(OrganizationRole.admin), event.getOrganization().getUuid());
    }

    /**
     * Validate if the given {@link Authentication} contains the given roles in the Organization of the given event.
     * @param auth the Authentication to validate
     * @param eventId the id of the event in which context the validation should occur
     * @param roles the OrganizationRoles that should get granted.
     * @return
     */
    public boolean grandOrganizationRole(Authentication auth, String eventId, List<Role> roles){
        Event event = getEventById(eventId);
        return authService.validateRights(auth,roles,event.getOrganization().getUuid());
    }

    /**
     * Sets the attendees status to checkedIn.
     * @param eventId the id of the event to check in
     * @param accountId the id of the account to be checked in
     */
    public void attendeesCheckIn(String eventId, String accountId){
        eventService.attendeesCheckIn(getEventById(eventId), accountService.getAccountById(accountId));
    }

    /**
     * Sets the attendees status to checkedOut.
     * @param eventId the id of the event to check in
     * @param accountId the id of the account to be checked in
     */
    public void attendeesCheckOut(String eventId, String accountId){
        eventService.attendeesCheckOut(getEventById(eventId), accountService.getAccountById(accountId));
    }

    /**
     * Gets all checked-In attendees
     * @param eventId the if of the event to get the checked-In attendees from
     * @return a list with all the Uuid of checked-In attendees
     */
    public List<String> getCheckedIn(String eventId){
        return eventService.getCheckedIn(eventId);
    }

    /**
     * Ands an account as an attendee to the given event.
     * @param eventId the event to add the account
     * @param accountId the account to be added.
     */
    public void addAccountToEvent(String eventId, String accountId) {
        Event event = getEventById(eventId);
        if(LocalDateTime.now().isAfter(event.getEndTime())){
            throw new IllegalArgumentException(strings.getString("event.EventIsOver"));
        }
        eventService.addAccountToEvent(event,accountService.getAccountById(accountId),EventRole.attendee);
    }
}