package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.controllerServices.FleventsAccountControllerService;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.security.jwt.JwtUtils;
import de.flyndre.fleventsbackend.security.payload.request.LoginRequest;
import de.flyndre.fleventsbackend.security.payload.request.LogoutRequest;
import de.flyndre.fleventsbackend.security.payload.response.JwtResponse;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This Class is the Controller for the REST-API path "/api/accounts".
 * It an interface functionality regarding accounts.
 * @author Lukas Burkhardt
 * @version $I$
 */

@RestController
@CrossOrigin
@RequestMapping("/api/accounts")
public class FleventsAccountController {
    private FleventsAccountControllerService fleventsAccountControllerService;
    private final ModelMapper mapper;
    private final JwtUtils jwtUtils;

    public FleventsAccountController(FleventsAccountControllerService fleventsAccountControllerService, ModelMapper mapper, JwtUtils jwtUtils){
        this.fleventsAccountControllerService = fleventsAccountControllerService;
        this.mapper = mapper;
        this.jwtUtils = jwtUtils;
    }

    /**
     * @param loginRequest the email and password of the account to get the preview from
     * @return ResponseEntity with the http status code
     */
    @PostMapping("/login")
    public ResponseEntity getAccountPreview(@Valid @RequestBody LoginRequest loginRequest){
        JwtResponse token = fleventsAccountControllerService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }
    /**
     * @param auth The AuthenticationToken of the current logged-in User
     * @return ResponseEntity with the http status code
     */
    @PostMapping("/refresh")
    public ResponseEntity getnewToken(Authentication auth){
        JwtResponse token = fleventsAccountControllerService.reevaluate(auth);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody LogoutRequest request){
        jwtUtils.invalidateToken(request.getToken());
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Returns the account extracted of the barer token
     * @param auth the Authentication generated out of a barer token.
     * @return AccountInformation of the specified account
     */
    @GetMapping()
    public AccountInformation getAccountInfo(Authentication auth){
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        return mapper.map(fleventsAccountControllerService.getAccountById(details.getId()), AccountInformation.class);
    }

    /**
     * Returns the booked events of the account extracted of the barer token
     * @param auth the Authentication generated out of a barer token.
     * @return List<EventInformation> list with the booked events of the given account
     */
    @GetMapping("/booked-events")
    public List<EventInformation> getBookedEvents(Authentication auth){
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        return fleventsAccountControllerService.getBookedEvents(details.getId()).stream()
                .map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    /**
     * Returns the managed events of the account extracted of the barer token
     * @param auth the Authentication generated out of a barer token.
     * @return List<EventInformation> list with the managed events of the given account
     */
    @GetMapping("/managed-events")
    public List<EventInformation> getManagedEvents(Authentication auth){
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        return fleventsAccountControllerService.getManagedEvents(details.getId()).stream()
                .map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    /**
     * Returns a list of events to explore for the account extracted of the barer token.
     * @param auth the Authentication generated out of a barer token.
     * @return List<EventInformation> list with events to explore for the specified account
     */
    @GetMapping("/{accountId}/explore-events")
    public List<EventInformation> getExploreEvents(Authentication auth){
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        return fleventsAccountControllerService.getExploreEvents(details.getId()).stream()
                .map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    /**
     * Get organizations where the account extracted of the barer token has the role "administrator".
     * @param auth the Authentication generated out of a barer token.
     * @return List<OrganizationInformation> list with the organizations where the account is administrator
     */
    @GetMapping("/managed-organizations")
    public List<OrganizationInformation> getManagedOrganization(Authentication auth){
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        return fleventsAccountControllerService.getManagedOrganization(details.getId()).stream().map(organization -> mapper.map(organization, OrganizationInformation.class)).collect(Collectors.toList());
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
     * Overwrites the account extracted of the barer token with the given account object.
     * @param account the account object with the new information of the account
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity with the overwritten account and the http status code
     */
    @PutMapping()
    public ResponseEntity editAccount(@RequestBody FleventsAccount account, Authentication auth){
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        return new ResponseEntity(mapper.map(fleventsAccountControllerService.editAccount(details.getId(), account), AccountInformation.class),HttpStatus.OK);
    }

    /**
     * Deletes the account extracted of the barer token.
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity with the http status code and an optional error message
     */
    @DeleteMapping()
    public ResponseEntity deleteAccount(Authentication auth){
        try{
            UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
            fleventsAccountControllerService.deleteAccount(details.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

}
