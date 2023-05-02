package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.controllerServices.EventControllerService;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.InvalidAttributesException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This Class is the Controller for the REST-API path "/api/events".
 * It provides an interface regarding events.
 * @author Lukas Burkhardt
 * @version $I$
 */

@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {

private final EventControllerService eventControllerService;
private final ModelMapper mapper;
   public EventController(EventControllerService eventControllerService, ModelMapper mapper){
      this.eventControllerService = eventControllerService;
      this.mapper = mapper;
   }

   /**
    * Returns a list of all existing events.
    * @return list of all events
    */
   @GetMapping
   public ResponseEntity getEvents() {
      try{
         return new ResponseEntity(eventControllerService.getEvents().stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList()),HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Returns the event with the given id.
    * Allows access for invited or above in the specified event.
    * @param eventId the id of the event
    * @param auth the Authentication generated out of a barer token.
    * @return event with the given id
    */
   @GetMapping("/{eventId}")
   public ResponseEntity getEventById(@PathVariable String eventId, Authentication auth){
      // TODO: In below out-commented code also check if in organization of event
//      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.values()))){
//         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//      }
      try {
         return new ResponseEntity(mapper.map(eventControllerService.getEventById(eventId),EventInformation.class),HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Returns the EventPreview of the specified event if the given token is valid.
    * @param eventId the id of the event to get the preview from
    * @param token the token to validate the request
    * @return ResponseEntity with the EventPreview and the http status code
    */
   @GetMapping("/{eventId}/preview")
   public ResponseEntity getEventPreview(@PathVariable String eventId, @RequestParam String token){
      try{
         return new ResponseEntity(eventControllerService.getEventPreview(eventId, token), HttpStatus.OK);
      }catch (InvalidAttributesException e){
         return new ResponseEntity<>("The token is not valid",HttpStatus.BAD_REQUEST);
      }catch (Exception e){
         return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }

   }

   /**
    * Deletes the event with the given id.
    * Allows access for organizer in the specified event.
    * @param eventId the id of the event to delete
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @DeleteMapping("/{eventId}")
   public ResponseEntity deleteEvent(@PathVariable String eventId,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.organizer))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         eventControllerService.deleteEvent(eventId);
         return new ResponseEntity<>(HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Returns a list of all attendees from the specified event.
    * Allows access for tutor an above of the specified event.
    * @param eventId the id of the event to get the list of attendees from
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with a list with the attendees of the event and the http status
    */
   @GetMapping("/{eventId}/attendees")
   public ResponseEntity getAttendees(@PathVariable String eventId,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.organizer,EventRole.tutor))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         return new ResponseEntity<>(eventControllerService.getAttendees(eventId).stream()
                 .map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Returns a list of all organizers from the specified event.
    * Allows access for organizer of the specified event.
    * @param eventId the id of the event to get the list of organizers from
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with a list with the organizers of the event and the http status code
    */
   @GetMapping("/{eventId}/organizers")
   public ResponseEntity getOrganizers(@PathVariable String eventId,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.organizer))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         return new ResponseEntity<>(eventControllerService.getOrganizers(eventId).stream().
                 map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
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
    * Overwrites the event specified with the given id with the parameters of the given event.
    * Allows access for tutor and above of the specified event.
    * @param eventId the id of the event to be set
    * @param event the event to be set to the given id
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the event object and the http status code
    */
   @PostMapping("/{eventId}")
   public ResponseEntity setEventById(@PathVariable String eventId, @RequestBody Event event, Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         return new ResponseEntity<>(mapper.map(eventControllerService.setEventById(eventId,event),EventInformation.class),HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Sends an invitation email to the given email with a link to register with the specified role to the specified event.
    * Allows access for tutor and above of the specified event.
    * @param eventId the id of the event to send an invitation to
    * @param email the email to send the invitation link to
    * @param role the role which gets assigned to the invited person
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code and an optional error message
    */
   @PostMapping("/{eventId}/invite")
   public ResponseEntity inviteToEvent(@PathVariable String eventId, @RequestParam() String email,@RequestParam EventRole role,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try{
         eventControllerService.inviteToEvent(eventId, email, role);
         return new ResponseEntity<>(HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Adds the account identified by the barer token in the role specified by the invitation token to the event.
    * Allows access for invited and above of the specified event.
    * @param eventId the id of the event to add the account to
    * @param token the token in the invitation link to verify the invitation
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/accept-invitation")
   public ResponseEntity acceptInvitation(@PathVariable String eventId, @RequestParam(required = false) String token, Authentication auth){
      try{
         UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
         eventControllerService.acceptInvitation(eventId, details.getId(), token);
         return new ResponseEntity(HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
      }
   }

   /**
    * Adds an Anonymous account identified by the given Email. Invalidates the invitation token to the event.
    * Allows access for invited and above of the specified event.
    * @implNote Does Method is for anonymous Accounts only
    * @param eventId the id of the event to add the account to
    * @param token the token in the invitation link to verify the invitation
    * @param mailAddress the MailAddress of the anonymous Account.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/accept-invitation/anonymously")
   public ResponseEntity acceptInvitation(@PathVariable String eventId, @RequestParam(required = false) String token, @RequestParam String mailAddress){
      try{
         eventControllerService.validateAndDeleteToken(token, eventId);
         eventControllerService.registerAnonymousAccountToEvent(eventId, mailAddress);
         return new ResponseEntity(HttpStatus.OK);

      }catch (Exception e){
         return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
      }
   }

   /**
    * Adds an account as an attendee to the event if the account is in the organization of this event.
    * Allows access for member and above of the organization of the specified event.
    * @param eventId the id of the event to add the account to
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/add-account")
   public ResponseEntity addAccountToEvent(@PathVariable String eventId,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.values()))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
         eventControllerService.addAccountToEvent(eventId,details.getId());
         return new ResponseEntity(HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Add an account directly to an event, if the account is a member or higher in the organization of the event.
    * Allows access for tutor and above of the specified event.
    * @param eventId the event to add to
    * @param userId the account to add
    * @param auth the Authentication generated out of a barer token.
    * @return Ok if successful or an error if something went wrong.
    */
   @PostMapping("/{eventId}/book")
   public ResponseEntity bookEvent(@PathVariable String eventId,@RequestParam String userId,Authentication auth){
      if (!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))) {
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try{
         eventControllerService.addAccountToEvent(eventId,userId);
         return new ResponseEntity(HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity(e.getMessage(),HttpStatus.OK);
      }
   }

   /**
    *Changes the role of a specified account in an event.
    * Allows access for tutor and above of the specified event.
    * @param eventId the id of the event with the account
    * @param accountId the id of the account which role has to be changed
    * @param fromRole the role of the account before the change
    * @param toRole the role to change the account to
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String eventId, @PathVariable() String accountId,@RequestParam EventRole fromRole, @RequestParam EventRole toRole,Authentication auth){
      if (!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))) {
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         eventControllerService.changeRole(eventId, accountId, fromRole,toRole);
         return new ResponseEntity(HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Adds an anonymous account to an event.
    * Allows access for tutor and above of the specified event.
    * @param eventId the id of the event to add the anonymous account to
    * @param account the anonymous account to be added
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code
    */
   @PostMapping("/{eventId}/add-account/add-anonymous")
   public ResponseEntity addAnonymousAccountToEvent(@PathVariable String eventId, @RequestBody FleventsAccount account,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      try {
         eventControllerService.addAnonymousAccountToEvent(eventId, account);
         return new ResponseEntity(HttpStatus.OK);
      }catch (Exception e){
         return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Removes a specified account from the specified event. The account needs to have the specified role.
    * Allows access for tutor and above of the specified event.
    * @param eventId the id of the event to remove the account from
    * @param accountId the id of the account to be removed from the event
    * @param role the role of the account
    * @param auth the Authentication generated out of a barer token.
    * @return ResponseEntity with the http status code and an optional error message
    */
   @PostMapping("/{eventId}/remove-account/{accountId}")
   public ResponseEntity removeAccountToEvent(@PathVariable String eventId, @PathVariable String accountId, @RequestParam EventRole role, Authentication auth){
      try{
         eventControllerService.removeAccountFromEvent(eventId,accountId,role);
         return new ResponseEntity(HttpStatus.OK);
      }catch (NoSuchElementException ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
      }catch (Exception e){
         return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Checks in the given Account
    * @param eventId the id of the event to check in
    * @param accountId the id of the account to be checked in
    * @param auth the Authentication generated out of a barer token.
    * @return HttpStatus whether the process was successfully or not
    */
   @PostMapping("/{eventId}/attendees/check-in/{accountId}")
   public HttpStatus attendeesCheckIn(@PathVariable String eventId, @PathVariable String accountId,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))){
         return HttpStatus.UNAUTHORIZED;
      }
      try{
         eventControllerService.attendeesCheckIn(eventId,accountId);
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }

   /**
    * Checks out the given Account
    * @param eventId the id of the event to check in
    * @param accountId the id of the account to be checked in
    * @param auth the Authentication generated out of a barer token.
    * @return HttpStatus whether the process was successfully or not
    */
   @PostMapping("/{eventId}/attendees/check-out/{accountId}")
   public HttpStatus attendeesCheckOut(@PathVariable String eventId, @PathVariable String accountId,Authentication auth){
      if(!eventControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.tutor,EventRole.organizer))){
         return HttpStatus.UNAUTHORIZED;
      }
      try{
         eventControllerService.attendeesCheckOut(eventId,accountId);
         return HttpStatus.OK;
      }catch (Exception e){
         return HttpStatus.INTERNAL_SERVER_ERROR;
      }
   }
   /**
    * not implemented yet
    * @param eventId the id of the event to add an attachment to
    * @return HttpStatus whether the process was successfully or not
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
    * @return HttpStatus whether the process was successfully or not
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

   /**
    * Gets all checked-In attendees
    * @param eventId the if of the event to get the checked-In attendees from
    * @return a list with the Uuid of all checked-In attendees
    */
   @GetMapping("/{eventId}/check-ins")
   public ResponseEntity getCheckedIn(@PathVariable String eventId){
      try{
         List checkedIns = eventControllerService.getCheckedIn(eventId);
         return new ResponseEntity( checkedIns, HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
      }
   }



}
