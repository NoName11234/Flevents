package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.FleventsAccountService;
import de.flyndre.fleventsbackend.services.OrganizationService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the service for the FleventsAccountController class.
 * It provides methods regarding accounts. The methods of the FleventsAccountController are mapped on them.
 */

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
    /**
     * Returns the account with the specified id.
     * @param accountId the id of the account to get the account information from
     * @return AccountInformation of the specified account
     */
    public FleventsAccount getAccountById(String accountId){
        return fleventsAccountService.getAccountById(accountId);
    }

    /**
     * Returns the booked events from a specified account.
     * @param accountId the id of the account to get the booked events from
     * @return List<Event> list with the booked events of the given account
     */
    public List<Event> getBookedEvents(String accountId){
        return eventService.getRegisteredEvents(getAccountById(accountId));
    }

    /**
     * Returns the managed events from the specified account.
     * @param accountId the id of the account to get the managed events from
     * @return List<EventInformation> list with the managed events of the given account
     */
    public List<Event> getManagedEvents(String accountId){
        return eventService.getManagedEvents(getAccountById(accountId));
    }

    /**
     * Returns a list of events to explore for the specified account.
     * @param accountId the id of the account to get a list of events to explore for
     * @return List<EventInformation> list with events to explore for the specified account
     */
    public List<Event> getExploreEvents(String accountId){
        return fleventsAccountService.getExploreEvents(getAccountById(accountId));
    }

    /**
     * Get organizations where the given account has the role "administrator".
     * @param accountId the id of the account
     * @return List<OrganizationInformation> list with the organizations where the account is administrator
     */
    public List<Organization> getManagedOrganization(String accountId){
        return organizationService.getManagedOrganization(getAccountById(accountId));
    }

    /**
     * Creates a new account.
     * @param account the account to be created
     * @return the created account
     */
    public FleventsAccount createAccount(FleventsAccount account){
        return fleventsAccountService.createAccount(account);
    }

    /**
     * Sends a mail with a link to reset the password for the account with the specified mail.
     * @param email the email of the account where to reset the password
     */
    public void resetPassword(String email) throws MessagingException {
        FleventsAccount account = fleventsAccountService.getAccountByMail(email);
        account.setSecret(UUID.randomUUID().toString());
        account = fleventsAccountService.editAccount(account.getUuid(),account);
        eMailService.sendNewPassword(email,account.getSecret());
    }

    /**
     * Overwrites a specified account with the given account object.
     * @param accountId the id of the account to be edited
     * @param account the account object with the new information of the account
     * @return the edited accoun
     */
    public FleventsAccount editAccount(String accountId, FleventsAccount account){
        return fleventsAccountService.editAccount(accountId, account);
    }

    /**
     * Deletes the specified account.
     * @param accountId the id of the account to be deleted
     */
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