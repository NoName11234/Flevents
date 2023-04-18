package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.controllerServices.OrganizationControllerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/organizations")
public class OrganizationController {
   private OrganizationControllerService organizationControllerService;
   private final ModelMapper mapper;

   public OrganizationController(OrganizationControllerService organizationControllerService, ModelMapper mapper){
      this.organizationControllerService = organizationControllerService;
      this.mapper = mapper;
   }


   @PostMapping("/{organizationId}/create-event")
   public ResponseEntity createEvent(@PathVariable String organizationId, @RequestBody Event event, @RequestParam String accountId) {
      return new ResponseEntity(mapper.map(organizationControllerService.createEvent(organizationId, event, accountId),EventInformation.class), HttpStatus.CREATED);
   }

   @GetMapping
   public ResponseEntity getOrganizations(){
      return new ResponseEntity(organizationControllerService.getOrganizations().stream().map(organization -> mapper.map(organization, OrganizationInformation.class)).collect(Collectors.toList()),HttpStatus.OK);
   }

   @GetMapping("/{organizationId}")
   public ResponseEntity getOrganization(@PathVariable String organizationId){
      return new ResponseEntity(mapper.map(organizationControllerService.getOrganizationById(organizationId), OrganizationInformation.class),HttpStatus.OK);
   }

   @GetMapping("/{organizationId}/events")
   public List<EventInformation> getEvents(@PathVariable String organizationId){
      return organizationControllerService.getEvents(organizationId).stream().map(event -> mapper.map(event, EventInformation.class)).collect(Collectors.toList());
   }

   @GetMapping("/{organizationId}/accounts")
   public List<AccountInformation> getAccounts(@PathVariable String organizationId){
      return organizationControllerService.getAccounts(organizationId).stream().map(account -> mapper.map(account, AccountInformation.class)).collect(Collectors.toList());
   }

   @PostMapping
   public ResponseEntity createOrganisation(@RequestBody Organization organisation){
      return new ResponseEntity(mapper.map(organizationControllerService.createOrganisation(organisation),OrganizationInformation.class),HttpStatus.OK);
   }

   @PostMapping("/{organizationId}")
   public ResponseEntity editOrganisation(@PathVariable String organizationId, @RequestBody Organization organisation){
      return new ResponseEntity<>(mapper.map(organizationControllerService.editOrganisation(organizationId, organisation), OrganizationInformation.class),HttpStatus.OK);
   }

   @PostMapping("/{organizationId}/invite")
   public ResponseEntity<String> sendInvitation(@PathVariable String organizationId, @RequestParam String email,@RequestParam OrganizationRole role){
      try {
         organizationControllerService.sendInvitation(organizationId, email, role);
         return  new ResponseEntity<>(HttpStatus.OK);
      }catch (Exception ex){
         return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   @PostMapping("/{organizationId}/add-account/{userId}")
   public ResponseEntity acceptInvitation(@PathVariable String organizationId,@PathVariable String userId,@RequestParam String token){
      organizationControllerService.acceptInvitation(organizationId, userId, token);
      return new ResponseEntity(HttpStatus.OK);
   }

   @PostMapping("/{organizationId}/remove-account/{accountId}")
   public ResponseEntity removeAccount(@PathVariable String organizationId, @PathVariable String accountId){
      organizationControllerService.removeAccount(organizationId, accountId);
      return new ResponseEntity(HttpStatus.OK);
   }

   @PostMapping("/{organizationId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String organizationId, @PathVariable String accountId,@RequestParam OrganizationRole fromRole,@RequestParam OrganizationRole toRole){
      organizationControllerService.changeRole(organizationId, accountId, fromRole,toRole);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}
