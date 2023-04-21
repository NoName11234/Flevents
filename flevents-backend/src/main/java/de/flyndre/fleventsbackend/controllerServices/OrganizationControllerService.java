package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.*;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationControllerService {
    private OrganizationService organizationService;
    private FleventsAccountService fleventsAccountService;
    private EventService eventService;
    private final InvitationTokenService invitationTokenService;
    private final EMailServiceImpl eMailService;

    private final AuthService authService;

    public OrganizationControllerService(OrganizationService organizationService, FleventsAccountService fleventsAccountService, EventService eventService, InvitationTokenService invitationTokenService, EMailServiceImpl eMailService, AuthService authService){
        this.organizationService = organizationService;
        this.fleventsAccountService = fleventsAccountService;
        this.eventService = eventService;
        this.invitationTokenService = invitationTokenService;
        this.eMailService = eMailService;
        this.authService = authService;
    }
    public List<Organization> getOrganizations(){
        return organizationService.getOrganizations();
    }

    public Organization getOrganizationById(String organizationId){
        return organizationService.getOrganizationById(organizationId);
    }

    public List<Event> getEvents(String organizationId){
        return getOrganizationById(organizationId).getEvents();
    }

    public List<FleventsAccount> getAccounts(String organizationId){
        return organizationService.getAccounts(getOrganizationById(organizationId));
    }

    public Organization createOrganisation(Organization organisation){
        return organizationService.createOrganisation(organisation);
    }

    public Organization editOrganisation(String organizationId, Organization organisation){
        return organizationService.editOrganisation(organizationId, organisation);
    }

    public void sendInvitation(String organizationId, String email,OrganizationRole role) throws MessagingException {
        InvitationToken token = invitationTokenService.saveToken(new InvitationToken(role.toString()));
        eMailService.sendOrganizationInvitation(getOrganizationById(organizationId),email,token.getToken());
    }

    public void acceptInvitation(String organizationId,String accountId,String token){
        InvitationToken invitationToken = invitationTokenService.validate(token);
        organizationService.addAccountToOrganization(getOrganizationById(organizationId),fleventsAccountService.getAccountById(accountId),OrganizationRole.valueOf(invitationToken.getRole()));
    }

    public boolean getGranted(Authentication auth, String uuid, ArrayList<String> roles){
       return authService.validateRights(auth, roles, uuid);
    }

    public void removeAccount(String organizationId, String accountId){
        organizationService.removeAccount(getOrganizationById(organizationId), fleventsAccountService.getAccountById(accountId));
    }

    public void changeRole(String organizationId, String accountId, OrganizationRole fromRole, OrganizationRole toRole){
        organizationService.changeRole(getOrganizationById(organizationId),fleventsAccountService.getAccountById(accountId), fromRole,toRole);
    }

    public Event createEvent(String organizationId, Event event, String accountId) {
        //TODO: Implement
        Organization organization = organizationService.getOrganizationById(organizationId);
        FleventsAccount account = fleventsAccountService.getAccountById(accountId);
        return eventService.createEventInOrganization(event, account, organization);
    }
}
