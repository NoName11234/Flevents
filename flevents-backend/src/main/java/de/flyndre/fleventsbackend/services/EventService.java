package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.EventRegistration;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import de.flyndre.fleventsbackend.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private EventRegistrationRepository eventRegistrationRepository;
    private EventRepository eventRepository;
    private ModelMapper mapper;

    public EventService(EventRegistrationRepository eventRegistrationRepository, ModelMapper mapper, EventRepository eventRepository){
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.mapper = mapper;
        this.eventRepository = eventRepository;
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

    public EventInformation getEventById(String eventId){
        Event event = eventRepository.findById(eventId).get();
        return mapper.map(event, EventInformation.class);
    }

    public HttpStatus deleteEvent(String eventId){
        Event ev = eventRepository.findById(eventId).get();
        eventRegistrationRepository.deleteAll(ev.getAttendees());
        eventRepository.delete(eventRepository.findById(eventId).get());
        return HttpStatus.OK;
    }
}
