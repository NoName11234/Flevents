package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.repositories.MailConfigRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This Service provides usage and logic for the Organization repository and the OrganizationAccount repository.
 * It provides methods for manipulating the data of these repositories.
 * @author Paul Lehmann
 * @version $I$
 */

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationAccountRepository organizationAccountRepository;
    private final MailConfigRepository mailConfigRepository;
    private static ResourceBundle strings = ResourceBundle.getBundle("ConfigStrings");
    public OrganizationService(OrganizationRepository organizationRepository, OrganizationAccountRepository organizationAccountRepository, MailConfigRepository mailConfigRepository){
        this.organizationRepository = organizationRepository;
        this.organizationAccountRepository = organizationAccountRepository;
        this.mailConfigRepository = mailConfigRepository;
    }

    /**
     * Returns a list of all organizations.
     * @return List<Organization> list of all organizations
     */
    public List<Organization> getOrganizations(){
        return organizationRepository.findAll();
    }

    /**
     * Returns the organization specified by its id.
     * @param organizationId the id of the organization
     * @return Organization with the given id
     * @throws NoSuchElementException if for the given id there's no organization.
     */
    public Organization getOrganizationById(String organizationId){
        Optional<Organization> optional = organizationRepository.findById(organizationId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not found an organization with this id");
        }
        return optional.get();
    }


    /**
     * Returns all accounts of the given organization.
     * @param organization the organization to get the accounts from
     * @return List<FleventsAccoount> list of all accounts
     */
    public List<FleventsAccount> getAccounts(Organization organization){
        return organization.getAccounts().stream().map(organizationAccount -> organizationAccount.getAccount()).collect(Collectors.toList());
    }

    /**
     * Creates the given organization in the database.
     * @param organisation the organization to be created
     * @return the created organization object
     */
    public Organization createOrganisation(Organization organisation){
        organisation.setUuid(null);
        organisation.setMailConfig(new MailConfig());
        mailConfigRepository.save(organisation.getMailConfig());
        return organizationRepository.save(organisation);
    }

    /**
     * Overwrites the specified organization with the given organization object.
     * @param organizationId the id of the organization to be overwritten
     * @param organisation the new organization object
     * @return the overwritten organization
     */
    public Organization editOrganisation(String organizationId, Organization organisation){
        Organization srcOrganization = organizationRepository.findById(organizationId).get();
        srcOrganization.merge(organisation);
        return organizationRepository.save(srcOrganization);
    }

    /**
     * Removes the given account from the specified organization.
     * @param organization the organization with the account to be removed
     * @param account the account to be removed
     */
    public void removeAccount(Organization organization, FleventsAccount account){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        if(organizationAccountRepository.findByOrganization_UuidAndRole(organization.getUuid(),OrganizationRole.admin).size()<2){
            throw new IllegalArgumentException("You cant delete the last admin account of an organization.");
        }
        organizationAccountRepository.delete(optional.get());
    }

    /**
     * Changes the role of the given account in the specified organization to the new given role. Throws an Exception if the given account is not in the organization or if the given account already has the role to change to.
     * @param organization the organization with the account
     * @param account the account which role has to be changed
     * @param fromRole the previous role of the account
     * @param toRole the new role of the account
     */
    public void changeRole(Organization organization, FleventsAccount account, OrganizationRole fromRole, OrganizationRole toRole){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(fromRole)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        if(organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(toRole)).findAny().isPresent()){
            throw new IllegalArgumentException("The given account has already the given role");
        }
        OrganizationAccount organizationAccount = optional.get();
        organizationAccount.setRole(toRole);
        organizationAccountRepository.save(organizationAccount);
    }

    /**
     * Returns all organizations where the given account has the role "admin".
     * @param account the account to get the managed organizations from
     * @return list of all managed organizations
     */
    public List<Organization> getManagedOrganization(FleventsAccount account){
        //TODO: Implement
        List<OrganizationAccount> organizationAccounts = organizationAccountRepository.findByAccount_UuidAndRole(account.getUuid(), OrganizationRole.admin);
        List<Organization> organisations = organizationAccounts.stream().map(organizationAccount -> organizationAccount.getOrganization()).collect(Collectors.toList());
        return organisations;
    }

    /**
     * Adds the given account to the specified organization with the specified role. Throws an exception if the account is already part of the organization.
     * @param organization the organization to add the account to
     * @param account the account to be added
     * @param role the role for the account in the organization
     */
    public void addAccountToOrganization(Organization organization, FleventsAccount account, OrganizationRole role){
        if(organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(role)).findAny().isPresent()){
            throw new IllegalArgumentException("This account is already part of this organization in the specified role");
        }
        organizationAccountRepository.save(new OrganizationAccount(null,organization,account,role));
    }

    /**
     * Deletes the specified organization.
     * @param organization the organization object to be deleted
     */
    public void deleteOrganization(Organization organization){
        for(OrganizationAccount organizationAccount:organization.getAccounts()){
            organizationAccountRepository.delete(organizationAccount);
        }
        organizationRepository.delete(organization);
    }

    /**
     * Leaves a specified organization.
     * @param organization the organzation to leave
     * @param account the account who leaves the organization
     */
    public void leaveOrganization(Organization organization, FleventsAccount account){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        organizationAccountRepository.delete(optional.get());
    }

    /**
     * Sets the Email-Configuration for the organization invitation in the specified organization.
     * @param organization the organization to set the Email-Configuration
     * @param mailText the text for the MailConfiguration to set
     */
    public void setMailConfigOrgaInvite(Organization organization, String mailText) {
        if(mailText.isEmpty()){
            throw new IllegalArgumentException("The Mailconfig is empty, cant change it");
        }
        MailConfig mailConfig = organization.getMailConfig();
        mailConfig.setOrganizationInvitation(mailText);
        mailConfigRepository.save(mailConfig);
        organization.setMailConfig(mailConfig);
        organizationRepository.save(organization);
    }

    /**
     * Sets the Email-Configuration for the event invitation in the specified organization.
     * @param organization the organization to set the Email-Configuration
     * @param mailText the text for the MailConfiguration to set
     */
    public void setMailConfigEventInvite(Organization organization, String mailText) {
        if(mailText.isEmpty()){
            throw new IllegalArgumentException("The Mailconfig is empty, cant change it");
        }
        MailConfig mailConfig = organization.getMailConfig();
        mailConfig.setEventInvitation(mailText);
        mailConfigRepository.save(mailConfig);
        organization.setMailConfig(mailConfig);
        organizationRepository.save(organization);
    }

    /**
     * Sets the Email-Configuration for the event info in the specified organization.
     * @param organization the organization to set the Email-Configuration
     * @param localDateTime the time to set
     * @param mailText the text for the MailConfiguration to set
     */
    public void setMailConfigEventInfo(Organization organization, String mailText, Duration localDateTime) {
        if(mailText.isEmpty()){
            throw new IllegalArgumentException("The Mailconfig is empty, cant change it");
        }
        if(localDateTime == null){
            throw new IllegalArgumentException("The LocalDateTime is null, cant change it");
        }
        MailConfig mailConfig = organization.getMailConfig();
        mailConfig.setInfoMessage(mailText);
        mailConfig.setInfoMessageOffset(localDateTime);
        mailConfigRepository.save(mailConfig);
        organization.setMailConfig(mailConfig);
        organizationRepository.save(organization);
    }

    /**
     * Sets the Email-Configuration for the event feedback in the specified organization.
     * @param organization the organization to set the Email-Configuration
     * @param localDateTime the time to set
     * @param mailText the text for the MailConfiguration to set
     */
    public void setMailConfigEventFeedback(Organization organization, String mailText, Duration localDateTime) {
        if(mailText.isEmpty()){
            throw new IllegalArgumentException("The Mailconfig is empty, cant change it");
        }

        MailConfig mailConfig = organization.getMailConfig();
        mailConfig.setFeedbackMessage(mailText);
        mailConfig.setInfoMessageOffset(localDateTime);
        mailConfigRepository.save(mailConfig);
        organization.setMailConfig(mailConfig);
        organizationRepository.save(organization);
    }

    /**
     * Gets the Email-Configuration for the specified organization.
     * @param organization the organization to get the Email-Configuration
     * @return the mail configuration
     */
    public MailConfig getMailConfig(Organization organization) {
        return organization.getMailConfig();
    }

    /**
     * Sets the Email-Configuration for the specified organization.
     * @param organization the organization to get the Email-Configuration
     * @param mailConfig the mail configuration of the organization
     */
    public void setMailConfig(Organization organization, MailConfig mailConfig) {
        organization.setMailConfig(mailConfig);
        mailConfigRepository.save(mailConfig);
        organizationRepository.save(organization);
    }

}
