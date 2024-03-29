package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import de.flyndre.fleventsbackend.repositories.EventRepository;
import de.flyndre.fleventsbackend.repositories.MailConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This Service provides logic and usage for the event repository and the eventRegistrationRepository.
 * It provides methods for manipulating the data of these repositories.
 * @author Lukas Burkhardt
 * @version $I$
 */

@Service
public class EventService {
    private EventRegistrationRepository eventRegistrationRepository;
    private EventRepository eventRepository;
    private final MailConfigRepository mailConfigRepository;
    private static ResourceBundle strings = ResourceBundle.getBundle("ConfigStrings");

    public EventService(EventRegistrationRepository eventRegistrationRepository, EventRepository eventRepository, MailConfigRepository mailConfigRepository){
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eventRepository = eventRepository;
        this.mailConfigRepository = mailConfigRepository;
    }

    /**
     * Returns all events where the given account is registered as tutor, attendee or guest.
     * @param account the account to get the events from
     * @return list of events
     */
    public List<Event> getRegisteredEvents(FleventsAccount account){
        List<EventRegistration> registrations = eventRegistrationRepository.findByAccount_UuidAndRole(account.getUuid(), EventRole.attendee);
        registrations.addAll(eventRegistrationRepository.findByAccount_UuidAndRole(account.getUuid(),EventRole.guest));
        registrations.addAll(eventRegistrationRepository.findByAccount_UuidAndRole(account.getUuid(),EventRole.tutor));
        List<Event> events = registrations.stream().map(registration -> registration.getEvent()).collect(Collectors.toList());
        return events;
    }

    /**
     * Get all events where the specified account has the role organizer or tutor.
     * @param account the account to get the managed events from
     * @return all events where the given account is registered as organizer or tutor.
     */
    public List<Event> getManagedEvents(FleventsAccount account){
        List<EventRegistration> registrations = eventRegistrationRepository.findByAccount_UuidAndRole(account.getUuid(), EventRole.organizer);
        registrations.addAll(eventRegistrationRepository.findByAccount_UuidAndRole(account.getUuid(),EventRole.tutor));
        List<Event> events = registrations.stream().map(registration -> registration.getEvent()).collect(Collectors.toList());
        return events;
    }

    /**
     * Returns a list of all events in the database.
     * @return list of all events
     */
    public List<Event> getEvents() {
        List<Event> events = eventRepository.findAll();
        return events;
    }

    /**
     * Returns the specified event.
     * @param eventId the id of the event
     * @return the event if its existing
     * @throws NoSuchElementException if no event was found
     */
    public Event getEventById(String eventId){
        Optional<Event> optional = eventRepository.findById(eventId);
        if(!optional.isPresent()){
            throw new NoSuchElementException(strings.getString("eventService.EventNotFoundById"));
        }
        return optional.get();
    }

    /**
     * Deletes the given event and all according registrations in the database.
     * @param event the event to be deleted
     */
    public void deleteEvent(Event event){
        for(EventRegistration eventRegistration:event.getAttendees()){
            eventRegistrationRepository.delete(eventRegistration);
        }
        eventRepository.delete(event);
    }

    /**
     * Returns a list of attendees of the specified event.
     * @param event the event to get the attendees from
     * @return list of accounts registered as tutor, attendee, guest or invited
     */
    public List<FleventsAccount> getAttendees(Event event){
        List<FleventsAccount> accounts = event.getAttendees().stream().map(registration -> {
            if(!registration.getRole().equals(EventRole.organizer)){
                return registration.getAccount();
            }
            return null;
        }).collect(Collectors.toList());
        return accounts;
    }

    /**
     * Returns a list of organizers of the specified event.
     * @param event the event to get the organizers from
     * @return list of accounts registered at the event as an organizer
     */
    public List<FleventsAccount> getOrganizers(Event event){
        //TODO: Implement
        List<FleventsAccount> accounts = event.getAttendees().stream().map(registration -> {
            if(!registration.getRole().equals(EventRole.organizer)){
                return registration.getAccount();
            }
            return null;
        }).collect(Collectors.toList());
        return accounts;
    }


    /**
     * Checks whether the specified account is registered in the specified event with the given role. Throws an Exception if there is no registration with the given values.
     * @param eventId the id to check for the account in
     * @param accountId the id of the account to check for
     * @param role the role of the specified account
     * @return EventRegistration of the specified account in the specified event
     */
    public EventRegistration getEventRegistration(String eventId,String accountId, EventRole role){
        Optional<EventRegistration> optional = eventRegistrationRepository.findByAccount_UuidAndEvent_UuidAndRole(accountId,eventId,role);
        if(!optional.isPresent()){
            throw new NoSuchElementException(String.format(strings.getString("eventService.EventRegistrationNotFound"),accountId,eventId,role.toString()));
        }
        return optional.get();
    }

    /**
     * Creates an event in the specified organization and adds the given account with the role organizer.
     * @param event the event to be created
     * @param account the account which shall be used to create the event
     * @param organization the organization in which to create the event
     * @return event which has been created
     */
    public Event createEventInOrganization(Event event, FleventsAccount account, Organization organization){
        event.setUuid(null);
        event.setOrganization(organization);
        if(event.getMailConfig().getUuid()==null){
            event.setMailConfig(organization.getMailConfig().copy());
        }else{
            event.setMailConfig(event.getMailConfig().copy());
        }
        mailConfigRepository.save(event.getMailConfig());
        event = eventRepository.save(event);
        addAccountToEvent(event,account,EventRole.organizer);
        return event;
    }

    /**
     * Sets the event of a given id to the specified event.
     * @param eventId the id of the event to be set
     * @param event the event to be set to the given id
     * @return the overwritten event
     * @throws IllegalArgumentException if the times aren't in the right order.
     */
    public Event setEventById(String eventId, Event event){
        Event srcEvent = eventRepository.findById(eventId).get();
        if(event.getStartTime()!=null&&event.getEndTime()==null&&event.getStartTime().isAfter(srcEvent.getEndTime())) {
            throw new IllegalArgumentException(strings.getString("eventService.NoValidTime"));
        }
        if(event.getEndTime()!=null&&event.getEndTime()==null&&event.getEndTime().isBefore(srcEvent.getStartTime())){
            throw new IllegalArgumentException(strings.getString("eventService.NoValidTime"));
        }
        if(event.getEndTime()!=null&&event.getEndTime()!=null&&event.getEndTime().isBefore(event.getStartTime())){
            throw new IllegalArgumentException(strings.getString("eventService.NoValidTime"));
        }
        srcEvent.merge(event);
        srcEvent = eventRepository.save(srcEvent);
        return srcEvent;
    }

    /**
     * Adds the given account to the given event.
     * @param event the event to add the account to
     * @param account the account to be added to the event
     * @param role the role the account has in the event
     * @return the eventregistration object
     */
    public EventRegistration addAccountToEvent(Event event, FleventsAccount account, EventRole role){
        if(eventRegistrationRepository.findByAccount_UuidAndEvent_UuidAndRole(account.getUuid(), event.getUuid(), role).isPresent()){
            throw new IllegalArgumentException(strings.getString("eventService.AccountAlreadyRegistered"));
        }
        return eventRegistrationRepository.save(new EventRegistration(null,event,account,role, false));
    }

    /**
     * Adds a Registration to the given event, to represent an invited user.
     * @param event the event of the invitation
     * @param account the invited account may be only an anonymous account.
     * @return a registration that represent the invitation.
     */
    public EventRegistration addInvitationToEvent(Event event, FleventsAccount account){
        return eventRegistrationRepository.save(new EventRegistration(null,event,account,EventRole.invited,false));
    }

    /**
     * Changes if possible the role of a registration.
     * @param event the event where the role of the account has to be changed
     * @param account the account which ones role has to be changed
     * @param fromRole the previous role of the account
     * @param toRole the new role of the account
     */
    public void changeRole(Event event, FleventsAccount account, EventRole fromRole, EventRole toRole){
        Optional<EventRegistration> optional = event.getAttendees().stream().filter(registration -> registration.getAccount().equals(account)&&registration.getRole().equals(fromRole)).findAny();
        if(!optional.isPresent()){
            throw new NoSuchElementException(strings.getString("eventService.AccountToBeChangedNotFound"));
        }
        if(event.getAttendees().stream().filter(registration -> registration.getAccount().equals(account)&&registration.getRole().equals(toRole)).findAny().isPresent()){
            throw new IllegalArgumentException(strings.getString("eventService.AccountAlreadyRegisteredWithRole"));
        }
        EventRegistration registration = optional.get();
        registration.setRole(toRole);
        eventRegistrationRepository.save(registration);
    }

    /**
     * Adds the given account to the specified event with the given role. Throws an Exception if there are now open invitations left for the event or if there is already a registration with the same parameters.
     * @param event the event to add the account to
     * @param account the account to be added to the event
     * @param role the role for the account in the event
     */
    public void acceptInvitation(Event event, FleventsAccount account, EventRole role){
        Optional<EventRegistration> optional = event.getAttendees().stream().filter(registration -> registration.getRole().equals(EventRole.invited)).findAny();
        if(!optional.isPresent()){
            throw new NoSuchElementException(strings.getString("eventService.NoInvitationsLeft"));
        }
        if(event.getAttendees().stream().filter(registration -> registration.getAccount().equals(account)&&registration.getRole().equals(role)).findAny().isPresent()){
            throw new IllegalArgumentException(strings.getString("eventService.RegistrationAlreadyExists"));
        }
        eventRegistrationRepository.save(new EventRegistration(null,event,account,role, false));
        eventRegistrationRepository.delete(optional.get());
    }

    /**
     * Removes the given account with the given role from the specified event.
     * @param event the event where to remove the account from
     * @param account the account to be removed from
     * @param role the role of the account in the event
     */
    public void removeAccountFromEvent(Event event, FleventsAccount account, EventRole role){
        eventRegistrationRepository.delete(getEventRegistration(event.getUuid(), account.getUuid(), role));
    }

    /**
     * Sets the attendees status to checkedIn.
     * @param event the event to check in
     * @param account the account to be checked in
     */
    public void attendeesCheckIn(Event event, FleventsAccount account){
        for(EventRegistration eventRegistration:event.getAttendees()) {
            if (account.getUuid().equals(eventRegistration.getAccount().getUuid())) {
                eventRegistration.setCheckedIn(true);
            }
        }
        eventRepository.save(event);
    }

    /**
     * Sets the attendees status to checkedOut.
     * @param event the event to check in
     * @param account the account to be checked in
     */
    public void attendeesCheckOut(Event event, FleventsAccount account){
        for(EventRegistration eventRegistration:event.getAttendees()) {
            if (account.getUuid().equals(eventRegistration.getAccount().getUuid())) {
                eventRegistration.setCheckedIn(false);
            }
        }
        eventRepository.save(event);
    }

    /**
     * Gets all checked-In attendees
     * @param eventId the if of the event to get the checked-In attendees from
     * @return a list with the Uuid of all checked-In attendees
     */
    public List<String> getCheckedIn(String eventId){
        List<EventRegistration> eventRegistrations =  eventRegistrationRepository.findByEvent_UuidAndRole(eventId, EventRole.attendee);
        List<String> checkedIns = null;
        if(eventRegistrations.isEmpty()){
            throw new NoSuchElementException(strings.getString("eventService.NoRegistrationsInEvent"));
        }
        for (EventRegistration er :eventRegistrations
        ) {
            if(er.isCheckedIn()){
                checkedIns.add(er.getAccount().getUuid());
            }
        }
        return checkedIns;
    }

    public void deleteQuestionnaireFromEvent(String questionnaireId, String eventId){
        Event event = getEventById(eventId);
        List<QuestionnaireModel> questions = event.getQuestionnaires();

        for(int i=0;i< questions.size();i++){
            if(questions.get(i).getUuid().equals(questionnaireId)){
                questions.remove(i);
                break;
            }
        }
        event.setQuestionnaires(questions);
        eventRepository.save(event);
    }

    public void registerNewQuestionnaire(QuestionnaireModel questionnaireModel, String eventId){
        Event event = getEventById(eventId);
        List<QuestionnaireModel> questionnaires = event.getQuestionnaires();
        questionnaires.add(questionnaireModel);
        event.setQuestionnaires(questionnaires);
        eventRepository.save(event);
    }
}
