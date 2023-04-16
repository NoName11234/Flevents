package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationControllerService {
    private OrganizationService organizationService;
    private FleventsAccountService fleventsAccountService;
    private EventService eventService;

    public OrganizationControllerService(OrganizationService organizationService, FleventsAccountService fleventsAccountService, EventService eventService){
        this.organizationService = organizationService;
        this.fleventsAccountService = fleventsAccountService;
        this.eventService = eventService;
    }
    public List<OrganizationInformation> getOrganizations(){
        return organizationService.getOrganizations();
    }

    public OrganizationInformation getOrganization(String organizationId){
        return organizationService.getOrganizationInformation(organizationId);
    }

    public List<EventInformation> getEvents(String organizationId){
        return organizationService.getEvents(organizationId);
    }

    public List<AccountInformation> getAccounts(String organizationId){
        return organizationService.getAccounts(organizationId);
    }

    public Organization createOrganisation(Organization organisation){
        return organizationService.createOrganisation(organisation);
    }

    public ResponseEntity editOrganisation(String organizationId, Organization organisation){
        return organizationService.editOrganisation(organizationId, organisation);
    }

    public ResponseEntity<String> sendInvitation(String organizationId, String email,OrganizationRole role){
        return organizationService.sendInvitation(organizationId, email, role);
    }

    public ResponseEntity acceptInvitation(String organizationId,String userId,String token){
        FleventsAccount account = fleventsAccountService.getUser(userId).get();
        return organizationService.acceptInvitation(organizationId, userId, token, account);
    }

    public ResponseEntity removeAccount(String organizationId, String accountId){
        return organizationService.removeAccount(organizationId, accountId);
    }

    public ResponseEntity changeRole(String organizationId, String accountId, OrganizationRole role){
        return organizationService.changeRole(organizationId, accountId, role);
    }

    public ResponseEntity createEvent(String organizationId, Event event, String accountId) {
        //TODO: Implement
        Organization organization = organizationService.getOrganization(organizationId);
        Optional<FleventsAccount> account = fleventsAccountService.getUser(accountId);
        if(account.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return eventService.createEventInOrganization(event, account, organization);
    }
}
