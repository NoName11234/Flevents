package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.InvitationToken;
import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.Models.OrganizationRole;
import de.flyndre.fleventsbackend.Models.PlatformAdminRole;
import de.flyndre.fleventsbackend.services.EMailService;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.InvitationTokenService;
import de.flyndre.fleventsbackend.services.OrganizationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PlatformControllerService {
    private final OrganizationService organizationService;
    private final EMailService eMailService;
    private final InvitationTokenService  invitationTokenService;
    private final EventService eventService;

    public PlatformControllerService(OrganizationService organizationService, EMailService eMailService, InvitationTokenService invitationTokenService, EventService eventService) {
        this.organizationService = organizationService;
        this.eMailService = eMailService;
        this.invitationTokenService = invitationTokenService;
        this.eventService = eventService;
    }

    public boolean getGranted(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority(PlatformAdminRole.platformAdmin.toString()));
    }

    public Organization createOrganisation(Organization organization,String email) {
        organization = organizationService.createOrganisation(organization);
        InvitationToken token =null;
        try {
           eMailService.sendOrganizationInvitation(
                   organization
                   , email
                   , (token=invitationTokenService.createToken(organization.getUuid(),OrganizationRole.admin)).toString());
            return organization;
        } catch (Exception e) {
            organizationService.deleteOrganization(organization);
            invitationTokenService.deleteToken(token);
            throw new RuntimeException(e);
        }

    }

    /**
     * Deletes the organization including all associated data.
     * @param organizationId the id of the organization to delete.
     * @throws NoSuchElementException if there's no organization to the given id.
     */
    public void deleteOrganization(String organizationId) {
        Organization organization = organizationService.getOrganizationById(organizationId);
        organization.getEvents().forEach(event -> eventService.deleteEvent(event));
        organizationService.deleteOrganization(organizationService.getOrganizationById(organizationId));
    }
}
