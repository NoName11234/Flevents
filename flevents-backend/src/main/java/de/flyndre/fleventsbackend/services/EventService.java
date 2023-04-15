package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.EventRegistration;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private EventRegistrationRepository eventRegistrationRepository;
    private ModelMapper mapper;

    public EventService(EventRegistrationRepository eventRegistrationRepository, ModelMapper mapper){
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.mapper = mapper;
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
}
