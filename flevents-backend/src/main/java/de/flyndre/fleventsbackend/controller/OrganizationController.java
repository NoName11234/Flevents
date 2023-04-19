package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.*;
import de.flyndre.fleventsbackend.services.EMailService;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import de.flyndre.fleventsbackend.services.OrganizationControllerService;
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
   private OrganizationControllerService organizationControllerService;

   public OrganizationController(OrganizationControllerService organizationControllerService){
      this.organizationControllerService = organizationControllerService;
   }


   @PostMapping("/{organizationId}/create-event")
   public ResponseEntity createEvent(@PathVariable String organizationId, @RequestBody Event event, @RequestParam String accountId) {
      return organizationControllerService.createEvent(organizationId, event, accountId);
   }

   @GetMapping
   public List<OrganizationInformation> getOrganizations(){
      return organizationControllerService.getOrganizations();
   }

   @GetMapping("/{organizationId}")
   public OrganizationInformation getOrganization(@PathVariable String organizationId){
      return organizationControllerService.getOrganization(organizationId);
   }

   @GetMapping("/{organizationId}/events")
   public List<EventInformation> getEvents(@PathVariable String organizationId){
      return organizationControllerService.getEvents(organizationId);
   }

   @GetMapping("/{organizationId}/accounts")
   public List<AccountInformation> getAccounts(@PathVariable String organizationId){
      return organizationControllerService.getAccounts(organizationId);
   }

   @PostMapping
   public Organization createOrganisation(@RequestBody Organization organisation){
      return organizationControllerService.createOrganisation(organisation);
   }

   @PostMapping("/{organizationId}")
   public ResponseEntity editOrganisation(@PathVariable String organizationId, @RequestBody Organization organisation){
      return organizationControllerService.editOrganisation(organizationId, organisation);
   }

   @PostMapping("/{organizationId}/invite")
   public ResponseEntity<String> sendInvitation(@PathVariable String organizationId, @RequestParam String email,@RequestParam OrganizationRole role){
      return organizationControllerService.sendInvitation(organizationId, email, role);
   }

   @PostMapping("/{organizationId}/add-account/{userId}")
   public ResponseEntity acceptInvitation(@PathVariable String organizationId,@PathVariable String userId,@RequestParam String token){
      return organizationControllerService.acceptInvitation(organizationId, userId, token);
   }

   @PostMapping("/{organizationId}/remove-account/{accountId}")
   public ResponseEntity removeAccount(@PathVariable String organizationId, @PathVariable String accountId){
      return organizationControllerService.removeAccount(organizationId, accountId);
   }

   @PostMapping("/{organizationId}/leave-organisation/{acountId}")
   public ResponseEntity leaveOrganization(@PathVariable String organizationId, @PathVariable String accountId){
      return organizationControllerService.leaveOrganization(organizationId, accountId);
   }

   @PostMapping("/{organizationId}/change-role/{accountId}")
   public ResponseEntity changeRole(@PathVariable String organizationId, @PathVariable String accountId,@RequestParam OrganizationRole role){
      return organizationControllerService.changeRole(organizationId, accountId, role);
   }
}
