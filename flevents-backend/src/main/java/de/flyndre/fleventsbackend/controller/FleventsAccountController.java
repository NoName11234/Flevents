package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import de.flyndre.fleventsbackend.services.EMailService;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/accounts")
public class FleventsAccountController {
    private final FleventsAccountRepository fleventsAccountRepository;
    private final OrganizationAccountRepository organizationAccountRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EMailService eMailService;

    private ModelMapper mapper;

    public FleventsAccountController(ModelMapper mapper, FleventsAccountRepository repository, OrganizationAccountRepository oarep, EventRegistrationRepository eventRegistrationRepository, EMailServiceImpl eMailService){
        this.mapper=mapper;
        this.fleventsAccountRepository = repository;
        this.organizationAccountRepository = oarep;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eMailService = eMailService;
    }
    @GetMapping("/validate")
    public ResponseEntity getAccountPreview(@RequestParam String email, @RequestParam String secret){
        Optional<FleventsAccount> account = fleventsAccountRepository.findByEmail(email);
        if(account.isEmpty()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        if(!account.get().getSecret().equals(secret)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(mapper.map(account.get(),AccountInformation.class),HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    public AccountInformation getAccountInfo(@PathVariable String accountId){
        //TODO: Implement
        FleventsAccount account = fleventsAccountRepository.findById(accountId).get();
        AccountInformation information= mapper.map(account,AccountInformation.class);
        return information;
    }
    @GetMapping("/{accountId}/booked-events")
    public List<EventInformation> getBookedEvents(@PathVariable String accountId){
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
    @GetMapping("/{accountId}/managed-events")
    public List<EventInformation> getManagedEvents(@PathVariable String accountId){
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
    @GetMapping("/{accountId}/explore-events")
    public List<EventInformation> getExploreEvents(@PathVariable String accountId){
        //TODO: Implement
        FleventsAccount account = fleventsAccountRepository.findById(accountId).get();
        List<Event> events = account.getOrganisations().stream().map(organizationAccount -> {
            List<Event> information = organizationAccount.getOrganization().getEvents();
            return information;
        }).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

        return events.stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }
    @GetMapping("/{accountId}/managed-organizations")
    public List<OrganizationInformation> getManagedOrganization(@PathVariable String accountId){
        //TODO: Implement
        List<OrganizationAccount> organizations = organizationAccountRepository.findByAccount_UuidAndRole(accountId, OrganizationRole.admin);
        List<OrganizationInformation> informationList = organizations.stream().map(organizationAccount -> {
            OrganizationInformation organizationInformation = mapper.map(organizationAccount.getOrganization(),OrganizationInformation.class);
            organizationInformation.setRole(organizationAccount.getRole());
            return organizationInformation;
        }).collect(Collectors.toList());
        return informationList;
    }

    @PostMapping()
    public ResponseEntity createAccount(@RequestBody FleventsAccount account){
        //TODO: Implement
        if(account.getEmail()==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(account.getSecret()==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(!fleventsAccountRepository.findByEmail(account.getEmail()).isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        account.setUuid(null);
        return new ResponseEntity<>(fleventsAccountRepository.save(account),HttpStatus.CREATED);
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email){
        Optional<FleventsAccount> optionalFleventsAccount = fleventsAccountRepository.findByEmail(email);
        if(optionalFleventsAccount.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FleventsAccount account= optionalFleventsAccount.get();
        String secret = UUID.randomUUID().toString();
        account.setSecret(secret);
        fleventsAccountRepository.save(account);
        try {
            eMailService.sendNewPassword(email,secret);
        } catch (MessagingException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/{accountId}")
    public ResponseEntity editAccount(@PathVariable String accountId, @RequestBody FleventsAccount account){
        //TODO: Implement
        Optional<FleventsAccount> existingAcc = fleventsAccountRepository.findById(accountId);
        if (existingAcc.isEmpty()) {
            return new ResponseEntity<>("No account with specified account id found", HttpStatus.BAD_REQUEST);
        }
        if(account.getEmail() != null) {
            Optional<FleventsAccount> existingAccEmail = fleventsAccountRepository.findByEmail(account.getEmail());
            if(existingAccEmail.isPresent() && !existingAccEmail.get().getUuid().equals(account.getUuid())) {
                return new ResponseEntity<>("This email is already in use", HttpStatus.BAD_REQUEST);
            }
        }
        FleventsAccount srcAccount = existingAcc.get();
        srcAccount.merge(account);
        return new ResponseEntity<>(mapper.map(fleventsAccountRepository.save(srcAccount),AccountInformation.class), HttpStatus.OK);
    }


}
