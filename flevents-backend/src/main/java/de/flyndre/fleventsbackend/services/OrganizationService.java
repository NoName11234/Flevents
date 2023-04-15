package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    private OrganizationRepository organizationRepository;
    private OrganizationAccountRepository organizationAccountRepository;
    private EMailService eMailService;
    private ModelMapper mapper;

    public OrganizationService(OrganizationRepository organizationRepository, OrganizationAccountRepository organizationAccountRepository, ModelMapper mapper){
        this.organizationRepository = organizationRepository;
        this.organizationAccountRepository = organizationAccountRepository;
        this.mapper = mapper;
    }

    public List<OrganizationInformation> getOrganizations(){
        return organizationRepository.findAll().stream().map(organization -> mapper.map(organization,OrganizationInformation.class)).collect(Collectors.toList());
    }

    public OrganizationInformation getOrganization(String organizationId){
        //TODO: Implement
        return mapper.map(organizationRepository.findById(organizationId),OrganizationInformation.class);
    }

    public List<EventInformation> getEvents(String organizationId){
        //TODO: Implement
        return organizationRepository.findById(organizationId).get().getEvents().stream().map(event -> mapper.map(event, EventInformation.class)).collect(Collectors.toList());
    }

    public List<AccountInformation> getAccounts(String organizationId){
        //TODO: Implement
        return organizationRepository.findById(organizationId).get().getAccounts().stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList());
    }

    public Organization createOrganisation(Organization organisation){
        //TODO: Implement
        organisation.setUuid(null);
        return organizationRepository.save(organisation);
    }

    public ResponseEntity editOrganisation(String organizationId, Organization organisation){
        //TODO: Implement
        Organization srcOrganization = organizationRepository.findById(organizationId).get();
        srcOrganization.merge(organisation);
        return new ResponseEntity(mapper.map(organizationRepository.save(srcOrganization), OrganizationInformation.class), HttpStatus.OK);
    }

    public ResponseEntity<String> sendInvitation(String organizationId, String email,OrganizationRole role){
        //TODO: Implement
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
        if(!organizationOptional.isPresent()){
            return new ResponseEntity<>("No Organization with such a id.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try{
            eMailService.sendOrganizationInvitation(organizationOptional.get(),email,"0efd2537-e7c6-458c-8d18-f09258b30562" +role.toString());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity acceptInvitation(String organizationId,String userId,String token, FleventsAccount account){
        //TODO: Implement
        if(!token.substring(0,36).equals("0efd2537-e7c6-458c-8d18-f09258b30562")){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        OrganizationRole role = OrganizationRole.valueOf(token.substring(36));
        if(organizationAccountRepository.findByAccount_UuidAndOrganization_Uuid(userId,organizationId).isPresent()){
            return new ResponseEntity("Accout already in organization",HttpStatus.BAD_REQUEST);
        }
        Organization organization = organizationRepository.findById(organizationId).get();
        organizationAccountRepository.save(new OrganizationAccount(null,organization,account, role));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity removeAccount(String organizationId, String accountId){
        Optional<OrganizationAccount> acc = organizationAccountRepository.findByAccount_UuidAndOrganization_Uuid(accountId, organizationId);
        if(acc.isEmpty()){
            return new ResponseEntity<>("Found no account with these specification in this organization.",HttpStatus.BAD_REQUEST);
        }
        if(OrganizationRole.admin.equals(acc.get().getRole())
                && organizationAccountRepository.findByOrganization_UuidAndRole(organizationId,OrganizationRole.admin).size()<=1
        ){
            return new ResponseEntity<>("Can't delete last administrator",HttpStatus.BAD_REQUEST);
        }
        organizationAccountRepository.delete(acc.get());
        return new ResponseEntity<>("Deleted!",HttpStatus.OK);
    }

    public ResponseEntity changeRole(String organizationId, String accountId, OrganizationRole role){
        OrganizationRole currRole = OrganizationRole.member;
        Organization org = organizationRepository.findById(organizationId).get();
        for(int i = 0; i < org.getAccounts().size(); i++){
            if(org.getAccounts().get(i).getAccount().getUuid().equals(accountId)){
                currRole = org.getAccounts().get(i).getRole();
            }
        }
        if(OrganizationRole.admin.equals(currRole) && !role.equals(OrganizationRole.admin)
                && organizationAccountRepository.findByOrganization_UuidAndRole(organizationId,OrganizationRole.admin).size()<=1
        ){
            return new ResponseEntity<>("Can't degrade last administrator",HttpStatus.BAD_REQUEST);
        }
        OrganizationAccount acc = organizationAccountRepository.findByAccount_UuidAndOrganization_Uuid(accountId, organizationId).get();
        acc.setRole(role);
        organizationAccountRepository.save(acc);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
