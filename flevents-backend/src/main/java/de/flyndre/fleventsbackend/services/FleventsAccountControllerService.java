package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.controller.FleventsAccountController;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FleventsAccountControllerService {

    private FleventsAccountService fleventsAccountService;
    private EventService eventService;
    private OrganizationService organizationService;

    public FleventsAccountControllerService(FleventsAccountService fleventsAccountService, EventService eventService, OrganizationService organizationService){
        this.fleventsAccountService = fleventsAccountService;
        this.eventService = eventService;
        this.organizationService = organizationService;
    }
    public ResponseEntity getAccountPreview( String email, String secret){
        return fleventsAccountService.getAccountPreview(email, secret);
    }

    public AccountInformation getAccountInfo(String accountId){
        return fleventsAccountService.getAccountInfo(accountId);
    }

    public List<EventInformation> getBookedEvents(String accountId){
        return eventService.getRegisteredEvents(accountId);
    }

    public List<EventInformation> getManagedEvents(String accountId){
        return eventService.getManagedEvents(accountId);
    }

    public List<EventInformation> getExploreEvents(String accountId){
        return fleventsAccountService.getExploreEvents(accountId);
    }

    public List<OrganizationInformation> getManagedOrganization(String accountId){
        return organizationService.getManagedOrganization(accountId);
    }

    public ResponseEntity createAccount(FleventsAccount account){
        return fleventsAccountService.createAccount(account);
    }

    public ResponseEntity resetPassword(String email){
        return fleventsAccountService.resetPassword(email);
    }

    public ResponseEntity editAccount(String accountId, FleventsAccount account){
        return fleventsAccountService.editAccount(accountId, account);
    }
}
