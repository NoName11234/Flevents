package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.*;
import de.flyndre.fleventsbackend.services.EMailService;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/organizations")
public class OrganizationController {

   private final OrganizationRepository organizationRepository;
   private final FleventsAccountRepository accountRepository;
   private final EventRepository eventRepository;
   private final OrganizationAccountRepository organizationAccountRepository;
   private final EMailService eMailService;
   private final EventRegistrationRepository eventRegistrationRepository;

   private ModelMapper mapper;

   public OrganizationController(ModelMapper mapper, FleventsAccountRepository accountRepository, OrganizationRepository organizationRepository, EventRepository eventRepository, OrganizationAccountRepository organizationAccountRepository, EMailServiceImpl eMailService, EventRegistrationRepository eventRegistrationRepository){
      this.mapper=mapper;
      this.accountRepository = accountRepository;
      this.organizationRepository = organizationRepository;
      this.eventRepository = eventRepository;
      this.organizationAccountRepository = organizationAccountRepository;

      this.eMailService = eMailService;
      this.eventRegistrationRepository = eventRegistrationRepository;
   }



   @GetMapping
   public List<OrganizationInformation> getOrganizations(){
      return organizationRepository.findAll().stream().map(organization -> mapper.map(organization,OrganizationInformation.class)).collect(Collectors.toList());
   }

   @GetMapping("/{organizationId}")
   public OrganizationInformation getOrganization(@PathVariable String organizationId){
      //TODO: Implement
      return mapper.map(organizationRepository.findById(organizationId),OrganizationInformation.class);


   }

   @GetMapping("/{organizationId}/events")
   public List<EventInformation> getEvents(@PathVariable String organizationId){
      //TODO: Implement
      return organizationRepository.findById(organizationId).get().getEvents().stream().map(event -> mapper.map(event, EventInformation.class)).collect(Collectors.toList());
   }

   @GetMapping("/{organizationId}/accounts")
   public List<AccountInformation> getAccounts(@PathVariable String organizationId){
      //TODO: Implement
      return organizationRepository.findById(organizationId).get().getAccounts().stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList());
   }

   @PostMapping
   public Organization createOrganisation(@RequestBody Organization organisation){
      //TODO: Implement
      organisation.setUuid(null);
      return organizationRepository.save(organisation);
   }

   @PostMapping("/{organizationId}")
   public ResponseEntity editOrganisation(@PathVariable String organizationId, @RequestBody Organization organisation){
      //TODO: Implement
      Organization srcOrganization = organizationRepository.findById(organizationId).get();
      srcOrganization.merge(organisation);
      return new ResponseEntity(mapper.map(organizationRepository.save(srcOrganization), OrganizationInformation.class),HttpStatus.OK);
   }

   @PostMapping("/{organizationId}/create-event")
   public ResponseEntity createEvent(@PathVariable String organizationId, @RequestBody Event event, @RequestParam String accountId) {
      //TODO: Implement
      Organization organization = organizationRepository.findById(organizationId).get();
      Optional<FleventsAccount> account = accountRepository.findById(accountId);
      if(account.isEmpty()){
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      event.setUuid(null);
      event.setOrganization(organization);
      event =  eventRepository.saveAndFlush(event);
      EventRegistration registration = eventRegistrationRepository.save(new EventRegistration(null,event,account.get(),EventRole.organizer));
      event.setAttendees(Arrays.asList(registration));
      return new ResponseEntity(mapper.map(event, EventInformation.class),HttpStatus.CREATED);
   }

   @PostMapping("/{organizationId}/invite")
   public ResponseEntity<String> sendInvitation(@PathVariable String organizationId, @RequestParam String email,@RequestParam OrganizationRole role){
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

   @PostMapping("/{organizationId}/add-account/{userId}")
   public ResponseEntity acceptInvitation(@PathVariable String organizationId,@PathVariable String userId,@RequestParam String token){
      //TODO: Implement
      if(!token.substring(0,36).equals("0efd2537-e7c6-458c-8d18-f09258b30562")){
         return new ResponseEntity(HttpStatus.UNAUTHORIZED);
      }
      OrganizationRole role = OrganizationRole.valueOf(token.substring(36));
      if(organizationAccountRepository.findByAccount_UuidAndOrganization_Uuid(userId,organizationId).isPresent()){
         return new ResponseEntity("Accout already in organization",HttpStatus.BAD_REQUEST);
      }
      Organization organization = organizationRepository.findById(organizationId).get();
      FleventsAccount account = accountRepository.findById(userId).get();
      organizationAccountRepository.save(new OrganizationAccount(null,organization,account, role));
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PostMapping("/{organizationId}/remove-account/{accountId}")
   public ResponseEntity removeAccount(@PathVariable String organizationId, @PathVariable String accountId){
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

   @PostMapping("/{organizationId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String organizationId, @PathVariable() String accountId,@RequestParam OrganizationRole role){
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
