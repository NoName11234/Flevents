package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.controllerServices.FleventsAccountControllerService;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.security.jwt.JwtUtils;
import de.flyndre.fleventsbackend.security.payload.request.LoginRequest;
import de.flyndre.fleventsbackend.security.payload.response.JwtResponse;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import de.flyndre.fleventsbackend.services.FleventsAccountService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the Controller for the REST-API path "/api/accounts".
 * It an interface functionality regarding accounts.
 */

@RestController
@CrossOrigin
@RequestMapping("/api/accounts")
public class FleventsAccountController {
    private FleventsAccountControllerService fleventsAccountControllerService;
    private final ModelMapper mapper;

    public FleventsAccountController(FleventsAccountControllerService fleventsAccountControllerService, ModelMapper mapper){
        this.fleventsAccountControllerService = fleventsAccountControllerService;
        this.mapper = mapper;
    }

    /**
     * @param loginRequest the email and password of the account to get the preview from
     * @return ResponseEntity with the http status code
     */
    @PostMapping("/validate")
    public ResponseEntity getAccountPreview(@Valid @RequestBody LoginRequest loginRequest){
        JwtResponse token = fleventsAccountControllerService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/revalidate")
    public ResponseEntity getnewToken(Authentication auth){
        JwtResponse token = fleventsAccountControllerService.reevaluate(auth);
        return ResponseEntity.ok(token);
    }

    /**
     * Returns the account with the specified id.
     * @param accountId the id of the account to get the account information from
     * @return AccountInformation of the specified account
     */
    @GetMapping("/{accountId}")
    public AccountInformation getAccountInfo(@PathVariable String accountId){
        return mapper.map(fleventsAccountControllerService.getAccountById(accountId), AccountInformation.class);
    }

    /**
     * Returns the booked events from a specified account.
     * @param accountId the id of the account to get the booked events from
     * @return List<EventInformation> list with the booked events of the given account
     */
    @GetMapping("/{accountId}/booked-events")
    public List<EventInformation> getBookedEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getBookedEvents(accountId).stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    /**
     * Returns the managed events from the specified account.
     * @param accountId the id of the account to get the managed events from
     * @return List<EventInformation> list with the managed events of the given account
     */
    @GetMapping("/{accountId}/managed-events")
    public List<EventInformation> getManagedEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getManagedEvents(accountId).stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    /**
     * Returns a list of events to explore for the specified account.
     * @param accountId the id of the account to get a list of events to explore for
     * @return List<EventInformation> list with events to explore for the specified account
     */
    @GetMapping("/{accountId}/explore-events")
    public List<EventInformation> getExploreEvents(@PathVariable String accountId){
        return fleventsAccountControllerService.getExploreEvents(accountId).stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    /**
     * Get organizations where the given account has the role "administrator".
     * @param accountId the id of the account
     * @return List<OrganizationInformation> list with the organizations where the account is administrator
     */
    @GetMapping("/{accountId}/managed-organizations")
    public List<OrganizationInformation> getManagedOrganization(@PathVariable String accountId){
        return fleventsAccountControllerService.getManagedOrganization(accountId).stream().map(organization -> mapper.map(organization, OrganizationInformation.class)).collect(Collectors.toList());
    }

    /**
     * Creates a new account.
     * @param account the account to be created
     * @return ResponseEntity with the created account and the http status code
     */
    @PostMapping()
    public ResponseEntity createAccount(@RequestBody FleventsAccount account){
        return new ResponseEntity<>(mapper.map(fleventsAccountControllerService.createAccount(account), AccountInformation.class),HttpStatus.OK);
    }

    /**
     * Sends a mail with a link to reset the password for the account with the specified mail.
     * @param email the email of the account where to reset the password
     * @return ResponseEntity with the http status code and an optional error message.
     */
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email){
        try{
            fleventsAccountControllerService.resetPassword(email);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Overwrites a specified account with the given account object.
     * @param accountId the id of the account to be edited
     * @param account the account object with the new information of the account
     * @return ResponseEntity with the overwritten account and the http status code
     */
    @PostMapping("/{accountId}")
    public ResponseEntity editAccount(@PathVariable String accountId, @RequestBody FleventsAccount account){
        return new ResponseEntity(mapper.map(fleventsAccountControllerService.editAccount(accountId, account), AccountInformation.class),HttpStatus.OK);
    }

    /**
     * Deletes the specified account.
     * @param accountId the id of the account to be deleted
     * @return ResponseEntity with the http status code and an optional error message
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity deleteAccount(@PathVariable String accountId){
        try{
            fleventsAccountControllerService.deleteAccount(accountId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

}
