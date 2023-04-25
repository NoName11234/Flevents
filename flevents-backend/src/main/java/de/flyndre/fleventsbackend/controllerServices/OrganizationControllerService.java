package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.OrganizationPreview;
import de.flyndre.fleventsbackend.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.naming.directory.InvalidAttributesException;
import java.util.List;

/**
 * This Class is the service for the OrganizationController class.
 * It provides methods regarding organizations. The methods of the OrganizationController are mapped on them.
 * @author Lukas Burkhardt
 * @version $I$
 */

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

    /**
     * Returns a list of the organizations existing in the database.
     * @return List of all organizations
     */
    public List<Organization> getOrganizations(){
        return organizationService.getOrganizations();
    }

    /**
     * Returns a single organization with the given id.
     * @param organizationId the id of the organization to be returned
     * @return the organization object
     */
    public Organization getOrganizationById(String organizationId){
        return organizationService.getOrganizationById(organizationId);
    }

    /**
     * Returns the OrganizationPreview of the specified organization if the given token is valid.
     * @param organizationId the id of the organization to get the preview from
     * @param token the token to validate the request
     * @return the OrganizationPreview with the data of the organization
     */
    public OrganizationPreview getOrganizationPreview(String organizationId, String token) throws InvalidAttributesException {
        invitationTokenService.validate(token, organizationId);
        Organization orga = organizationService.getOrganizationById(organizationId);
        OrganizationPreview preview = new OrganizationPreview();
        preview.setName(orga.getName());
        preview.setRole(null);
        preview.setIcon(orga.getIcon());
        preview.setUuid(orga.getUuid());
        preview.setAddress(orga.getAddress());
        preview.setDescription(orga.getDescription());
        preview.setPhoneContact(orga.getPhoneContact());

        return preview;
    }

    /**
     * Returns a list of events from a single organization specified by an id.
     * @param organizationId the id of the organization to get the events from
     * @return list of all events from the organization
     */
    public List<Event> getEvents(String organizationId){
        return getOrganizationById(organizationId).getEvents();
    }

    /**
     * Get all accounts of an organizations.
     * @param organizationId the id of the organization to get all accounts from
     * @return list of accounts
     */
    public List<FleventsAccount> getAccounts(String organizationId){
        return organizationService.getAccounts(getOrganizationById(organizationId));
    }

    /**
     * Creates a new organization out of the given organization object, not all values have to be set.
     * @param organisation organization object to be created
     * @param email the email address to send an invitation to become the first admin
     * @return Organization that has been created
     */
    public Organization createOrganisation(Organization organisation, String email) {
        Organization organization = organizationService.createOrganisation(organisation);
        try {
            sendInvitation(organization.getUuid(), email, OrganizationRole.admin);
        }catch (RuntimeException e){
            organizationService.deleteOrganization(organization);
            throw e;
        }
        return organisation;
    }

    /**
     * Overwrites an existing organization object with the given organization object.
     * @param organizationId the id of the organization to be overwritten
     * @param organisation the organization object with the new organization information
     * @return the updated organization object
     */
    public Organization editOrganisation(String organizationId, Organization organisation){
        return organizationService.editOrganisation(organizationId, organisation);
    }

    /**
     * Sends an invitation link in a mail to a specified organization.
     * @param organizationId the id of the organization to send an invitation link to
     * @param email the email to send the invitation to
     * @param role the role to set the account with the invitation to
     */
    public void sendInvitation(String organizationId, String email,OrganizationRole role)
    {
        InvitationToken token = invitationTokenService.createToken(organizationId,role);
        try {
            eMailService.sendOrganizationInvitation(getOrganizationById(organizationId),email,token.toString());
        } catch (Exception e) {
            invitationTokenService.deleteToken(token);
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds an account to an organization after the account owner accepted an invitation.
     * @param organizationId the id of the organization to add the account to
     * @param accountId the id of the account to be added to the organization
     * @param token the token to verify the invitation
     */
    public void acceptInvitation(String organizationId,String accountId,String token) throws InvalidAttributesException {
        InvitationToken invitationToken = invitationTokenService.validate(token,organizationId);
        organizationService.addAccountToOrganization(getOrganizationById(organizationId),fleventsAccountService.getAccountById(accountId),OrganizationRole.valueOf(invitationToken.getRole()));
        invitationTokenService.deleteToken(invitationToken);
    }

    public boolean getGranted(Authentication auth, String uuid, List<Role> roles){
       return authService.validateRights(auth, roles, uuid);
    }

    /**
     * Removes a specified account from a specified organization.
     * @param organizationId the id of the organization to remove the account from
     * @param accountId the id of the account to be removed
     */
    public void removeAccount(String organizationId, String accountId){
        organizationService.removeAccount(getOrganizationById(organizationId), fleventsAccountService.getAccountById(accountId));
    }

    /**
     * Changes the role of a specified account in a specified organization.
     * @param organizationId the id of the organization where to change the accounts role
     * @param accountId the id of the account which role has to be changed
     * @param fromRole the role of the account before the change
     * @param toRole the role to change the account to
     */
    public void changeRole(String organizationId, String accountId, OrganizationRole fromRole, OrganizationRole toRole){
        organizationService.changeRole(getOrganizationById(organizationId),fleventsAccountService.getAccountById(accountId), fromRole,toRole);
    }

    /**
     * Creates an event in the specified organization and adds the given account with the role organizer.
     * @param event the event to be created
     * @param accountId the id of the account which shall be used to create the event
     * @param organizationId the id of the organization in which to create the event
     * @return event which has been created
     */
    public Event createEvent(String organizationId, Event event, String accountId) {
        //TODO: Implement
        Organization organization = organizationService.getOrganizationById(organizationId);
        FleventsAccount account = fleventsAccountService.getAccountById(accountId);
        return eventService.createEventInOrganization(event, account, organization);
    }

    /**
     *  A specified account leaves a specified organization.
     * @param organizationId the id of the organization to remove the account from
     * @param accountId the id of the account to be removed
     */
    public void leaveOrganization(String organizationId, String accountId){
        organizationService.leaveOrganization(getOrganizationById(organizationId), fleventsAccountService.getAccountById(accountId));
    }

}
