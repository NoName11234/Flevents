package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.FleventsAccountService;
import de.flyndre.fleventsbackend.services.OrganizationService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FleventsAccountControllerService {

    private FleventsAccountService fleventsAccountService;
    private EventService eventService;
    private OrganizationService organizationService;
    private final EMailServiceImpl eMailService;

    public FleventsAccountControllerService(FleventsAccountService fleventsAccountService, EventService eventService, OrganizationService organizationService, EMailServiceImpl eMailService){
        this.fleventsAccountService = fleventsAccountService;
        this.eventService = eventService;
        this.organizationService = organizationService;
        this.eMailService = eMailService;
    }
    public FleventsAccount getAccountById(String accountId){
        return fleventsAccountService.getAccountById(accountId);
    }

    public List<Event> getBookedEvents(String accountId){
        return eventService.getRegisteredEvents(getAccountById(accountId));
    }

    public List<Event> getManagedEvents(String accountId){
        return eventService.getManagedEvents(getAccountById(accountId));
    }

    public List<Event> getExploreEvents(String accountId){
        return fleventsAccountService.getExploreEvents(getAccountById(accountId));
    }

    public List<Organization> getManagedOrganization(String accountId){
        return organizationService.getManagedOrganization(getAccountById(accountId));
    }

    public FleventsAccount createAccount(FleventsAccount account){
        return fleventsAccountService.createAccount(account);
    }

    public void resetPassword(String email) throws MessagingException {
        FleventsAccount account = fleventsAccountService.getAccountByMail(email);
        account.setSecret(UUID.randomUUID().toString());
        account = fleventsAccountService.editAccount(account.getUuid(),account);
        eMailService.sendNewPassword(email,account.getSecret());
    }

    public FleventsAccount editAccount(String accountId, FleventsAccount account){
        return fleventsAccountService.editAccount(accountId, account);
    }
    public void deleteAccount(String accountId){
        FleventsAccount account = fleventsAccountService.getAccountById(accountId);
        account.getEvents().stream().map(eventRegistration -> {
            if(eventRegistration.getEvent().getStartTime().isAfter(LocalDateTime.now())){
                eventService.removeAccountFromEvent(eventRegistration.getEvent(),eventRegistration.getAccount(),eventRegistration.getRole());
            }
            return null;
        });
        fleventsAccountService.deleteAccount(account);
    }
}