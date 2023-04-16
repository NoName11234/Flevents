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

    /**
     * @param email the email of the account to get the preview from
     * @param secret the secret to access the account informatiosn
     * @return ResponseEntity with information of the process and the account preview
     */
    @GetMapping("/validate")
    public ResponseEntity getAccountPreview(@RequestParam String email, @RequestParam String secret){
        return fleventsAccountControllerService.getAccountPreview(email, secret);
    }

    /**
     * @param accountId the id of the account to get the account information from
     * @return AccountInformation information of the specified account
     */
    @GetMapping("/{accountId}")
    public AccountInformation getAccountInfo(@PathVariable String accountId){
        return fleventsAccountControllerService.getAccountInfo(accountId);
    }

    /**
     * @param accountId the id of the account to get the booked events from
     * @return List<EventInformation> list with the booked events of the given account
     */
    @GetMapping("/{accountId}/booked-events")
    public List<EventInformation> getBookedEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getBookedEvents(accountId);
    }

    /**
     * @param accountId the id of the account to get the managed events from
     * @return List<EventInformation> list with the managed events of the given account
     */
    @GetMapping("/{accountId}/managed-events")
    public List<EventInformation> getManagedEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getManagedEvents(accountId);
    }

    /**
     * @param accountId the id of the account to get a list of events to explore for
     * @return List<EventInformation> list with events to explore for the specified account
     */
    @GetMapping("/{accountId}/explore-events")
    public List<EventInformation> getExploreEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getExploreEvents(accountId);
    }

    /**
     * get organizations where the given account has the role "administrator"
     * @param accountId the id of the account
     * @return List<OrganizationInformation> list with information of the organizations where the account is administrator
     */
    @GetMapping("/{accountId}/managed-organizations")
    public List<OrganizationInformation> getManagedOrganization(@PathVariable String accountId){
        return fleventsAccountControllerService.getManagedOrganization(accountId);
    }

    /**
     * create a new account
     * @param account the account to be created
     * @return ResponseEntity with information about the process
     */
    @PostMapping()
    public ResponseEntity createAccount(@RequestBody FleventsAccount account){
        return fleventsAccountControllerService.createAccount(account);
    }

    /**
     * sends a mail with a link to reset the passwort for the account with the specified mail
     * @param email the email of the account where to reset the password
     * @return ResponseEntity with information about the process
     */
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email){
        return fleventsAccountControllerService.resetPassword(email);
    }

    /**
     * @param accountId the id of the account to be edited
     * @param account the account object with the new information of the account
     * @return ResponseEntity with information about the process
     */
    @PostMapping("/{accountId}")
    public ResponseEntity editAccount(@PathVariable String accountId, @RequestBody FleventsAccount account){
        return fleventsAccountControllerService.editAccount(accountId, account);
    }


}
