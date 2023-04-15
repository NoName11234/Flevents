package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.EventRegistrationRepository;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import de.flyndre.fleventsbackend.services.EMailService;
import de.flyndre.fleventsbackend.services.EMailServiceImpl;
import de.flyndre.fleventsbackend.services.FleventsAccountControllerService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/accounts")
public class FleventsAccountController {
    private FleventsAccountControllerService fleventsAccountControllerService;

    public FleventsAccountController(FleventsAccountControllerService fleventsAccountControllerService){
        this.fleventsAccountControllerService = fleventsAccountControllerService;
    }
    @GetMapping("/validate")
    public ResponseEntity getAccountPreview(@RequestParam String email, @RequestParam String secret){
        return fleventsAccountControllerService.getAccountPreview(email, secret);
    }

    @GetMapping("/{accountId}")
    public AccountInformation getAccountInfo(@PathVariable String accountId){
        return fleventsAccountControllerService.getAccountInfo(accountId);
    }

    @GetMapping("/{accountId}/booked-events")
    public List<EventInformation> getBookedEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getBookedEvents(accountId);
    }
    @GetMapping("/{accountId}/managed-events")
    public List<EventInformation> getManagedEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getManagedEvents(accountId);
    }
    @GetMapping("/{accountId}/explore-events")
    public List<EventInformation> getExploreEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getExploreEvents(accountId);
    }
    @GetMapping("/{accountId}/managed-organizations")
    public List<OrganizationInformation> getManagedOrganization(@PathVariable String accountId){
        return fleventsAccountControllerService.getManagedOrganization(accountId);
    }

    @PostMapping()
    public ResponseEntity createAccount(@RequestBody FleventsAccount account){
        return fleventsAccountControllerService.createAccount(account);
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email){
        return fleventsAccountControllerService.resetPassword(email);
    }
    @PostMapping("/{accountId}")
    public ResponseEntity editAccount(@PathVariable String accountId, @RequestBody FleventsAccount account){
        return fleventsAccountControllerService.editAccount(accountId, account);
    }


}
