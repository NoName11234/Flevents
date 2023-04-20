package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationPreview;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    private OrganizationRepository organizationRepository;
    private OrganizationAccountRepository organizationAccountRepository;
    public OrganizationService(OrganizationRepository organizationRepository, OrganizationAccountRepository organizationAccountRepository){
        this.organizationRepository = organizationRepository;
        this.organizationAccountRepository = organizationAccountRepository;
    }

    public List<Organization> getOrganizations(){
        return organizationRepository.findAll();
    }

    public Organization getOrganizationById(String organizationId){
        Optional<Organization> optional = organizationRepository.findById(organizationId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not found an organization with this id");
        }
        return optional.get();
    }


    public List<FleventsAccount> getAccounts(Organization organization){
        return organization.getAccounts().stream().map(organizationAccount -> organizationAccount.getAccount()).collect(Collectors.toList());
    }

    public Organization createOrganisation(Organization organisation){
        organisation.setUuid(null);
        return organizationRepository.save(organisation);
    }

    public Organization editOrganisation(String organizationId, Organization organisation){
        Organization srcOrganization = organizationRepository.findById(organizationId).get();
        srcOrganization.merge(organisation);
        return organizationRepository.save(srcOrganization);
    }

    public void removeAccount(Organization organization, FleventsAccount account){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        organizationAccountRepository.delete(optional.get());
    }

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

    public List<Organization> getManagedOrganization(FleventsAccount account){
        //TODO: Implement
        List<OrganizationAccount> organizationAccounts = organizationAccountRepository.findByAccount_UuidAndRole(account.getUuid(), OrganizationRole.admin);
        List<Organization> organisations = organizationAccounts.stream().map(organizationAccount -> organizationAccount.getOrganization()).collect(Collectors.toList());
        return organisations;
    }

    public void addAccountToOrganization(Organization organization,FleventsAccount account, OrganizationRole role){
        if(organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(role)).findAny().isPresent()){
            throw new IllegalArgumentException("This account is already part of this organization in the specified role");
        }
        organizationAccountRepository.save(new OrganizationAccount(null,organization,account,role));
    }
}
