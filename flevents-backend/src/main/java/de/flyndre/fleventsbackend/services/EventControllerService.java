package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventControllerService {
    private EventService eventService;
    public EventControllerService(EventService eventService){
        this.eventService = eventService;
    }
    public List<EventInformation> getEvents() {
        return eventService.getEvents();
    }

    public EventInformation getEventById(String eventId){
        return eventService.getEventById(eventId);
    }

    public HttpStatus deleteEvent(String eventId){
        return eventService.deleteEvent(eventId);
    }
}
