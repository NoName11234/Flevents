package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.EventRegistration;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import de.flyndre.fleventsbackend.repositories.EventRepository;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
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
public class EventService {
    private EventRegistrationRepository eventRegistrationRepository;
    private EventRepository eventRepository;
    private ModelMapper mapper;
    private EMailService eMailService;

    public EventService(EventRegistrationRepository eventRegistrationRepository, ModelMapper mapper, EventRepository eventRepository, EMailService eMailService){
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.mapper = mapper;
        this.eventRepository = eventRepository;
        this.eMailService = eMailService;
    }

    public List<EventInformation> getRegisteredEvents(String accountId){
        //TODO: Implement
        List<EventRegistration> registrations = eventRegistrationRepository.findByAccount_UuidAndRole(accountId, EventRole.attendee);
        registrations.addAll(eventRegistrationRepository.findByAccount_UuidAndRole(accountId,EventRole.guest));
        registrations.addAll(eventRegistrationRepository.findByAccount_UuidAndRole(accountId,EventRole.tutor));
        List<EventInformation> informationList = registrations.stream().map(eventRegistration -> {
            EventInformation eventInformation = mapper.map(eventRegistration.getEvent(),EventInformation.class);
            eventInformation.setRole(eventRegistration.getRole());
            return eventInformation;
        }).collect(Collectors.toList());
        return informationList;
    }

    public List<EventInformation> getManagedEvents(String accountId){
        //TODO: Implement
        List<EventRegistration> registrations = eventRegistrationRepository.findByAccount_UuidAndRole(accountId, EventRole.organizer);
        registrations.addAll(eventRegistrationRepository.findByAccount_UuidAndRole(accountId,EventRole.tutor));
        List<EventInformation> informationList = registrations.stream().map(eventRegistration -> {
            EventInformation eventInformation = mapper.map(eventRegistration.getEvent(),EventInformation.class);
            eventInformation.setRole(eventRegistration.getRole());
            return eventInformation;
        }).collect(Collectors.toList());
        return informationList;
    }

    public List<EventInformation> getEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map((event) -> mapper.map(event, EventInformation.class)).collect(Collectors.toList());
    }

    public EventInformation getEventInformationById(String eventId){
        Event event = eventRepository.findById(eventId).get();
        return mapper.map(event, EventInformation.class);
    }

    public Optional<Event> getEventById(String eventId){
        return eventRepository.findById(eventId);
    }

    public HttpStatus deleteEvent(String eventId){
        Event ev = eventRepository.findById(eventId).get();
        eventRegistrationRepository.deleteAll(ev.getAttendees());
        eventRepository.delete(eventRepository.findById(eventId).get());
        return HttpStatus.OK;
    }

    public ResponseEntity getAttendees(String eventId){
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

    public ResponseEntity getOrganizers(String eventId){
        //TODO: Implement
        List<EventRegistration> registrations = eventRegistrationRepository.findByEvent_UuidAndRole(eventId,EventRole.organizer);
        return new ResponseEntity(registrations.stream().map(registration ->{
            AccountInformation information = mapper.map(registration.getAccount(),AccountInformation.class);
            information.setRole(registration.getRole());
            return information;
        }).collect(Collectors.toList()),HttpStatus.OK);
    }

    public ResponseEntity createEvent(Event event){
        //TODO: Implement
        event.setUuid(null);
        return new ResponseEntity<>(mapper.map(eventRepository.save(event),EventInformation.class),HttpStatus.CREATED);
    }

    public ResponseEntity setEventById(String eventId, Event event){
        try{
            Event srcEvent = eventRepository.findById(eventId).get();
            srcEvent.merge(event);
            return new ResponseEntity<>(mapper.map(eventRepository.save(srcEvent),EventInformation.class),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity inviteToEvent(String email, EventRole role, FleventsAccount account, Optional<Event> event){
        //TODO: Implement
        EventRegistration registration = eventRegistrationRepository.save(new EventRegistration(null,event.get(),account,EventRole.invited));
        try {
            eMailService.sendEventInvitaion(event.get(),email, registration.getId() +role.toString());
        } catch (MessagingException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity addAccountToEvent(String eventId, String accountId, String token, FleventsAccount account){

        //TODO:
        if(token==null){
            Event event = eventRepository.findById(eventId).get();
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

        EventRegistration registration = optionalEventRegistration.get();
        registration.setAccount(account);
        registration.setRole(role);
        eventRegistrationRepository.save(registration);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity changeRole(String eventId, String accountId, EventRole role){
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

    public ResponseEntity createAndAddAccountToEvent(String eventId, String eMail, FleventsAccount account){

        //TODO: Implement
        Event event = eventRepository.findById(eventId).get();

        eventRegistrationRepository.save(new EventRegistration(null,event,account,EventRole.guest));
        return new ResponseEntity<>(account,HttpStatus.OK);
    }

    public HttpStatus addAnonymousAccountToEvent(String eventId, FleventsAccount account){
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

    public ResponseEntity removeAccountToEvent(String eventId, String accountId, EventRole role){
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
}
