package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.security.jwt.JwtUtils;
import de.flyndre.fleventsbackend.security.payload.response.JwtResponse;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import de.flyndre.fleventsbackend.services.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FleventsAccountControllerService {

    private FleventsAccountService fleventsAccountService;
    private EventService eventService;
    private OrganizationService organizationService;
    private final EMailServiceImpl eMailService;

    private final AuthService authService;


    public FleventsAccountControllerService(FleventsAccountService fleventsAccountService, EventService eventService, OrganizationService organizationService, EMailServiceImpl eMailService, AuthService authService){
        this.fleventsAccountService = fleventsAccountService;
        this.eventService = eventService;
        this.organizationService = organizationService;
        this.eMailService = eMailService;
        this.authService = authService;
    }

    public JwtResponse login(String mail, String password){
        FleventsAccount acc = getByAccountMail(mail);
        return authService.authorize(acc, password);
    }

    public JwtResponse reevaluate(Authentication auth){
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return authService.authorize(getAccountById(user.getId()), user.getPassword());
    }

    public FleventsAccount getAccountById(String accountId){
        return fleventsAccountService.getAccountById(accountId);
    }

    public List<Event> getBookedEvents(String accountId){
        return eventService.getRegisteredEvents(getAccountById(accountId));
    }

    public List<Event> getManagedEvents(String accountId){
        return eventService.getManagedEvents(getAccountById(accountId));
    }

    public List<Event> getExploreEvents(String accountId){
        return fleventsAccountService.getExploreEvents(getAccountById(accountId));
    }

    public List<Organization> getManagedOrganization(String accountId){
        return organizationService.getManagedOrganization(getAccountById(accountId));
    }

    public FleventsAccount createAccount(FleventsAccount account){
        return fleventsAccountService.createAccount(account);
    }

    public FleventsAccount getByAccountMail(String mail){
        return fleventsAccountService.getAccountByMail(mail);
    }

    public void resetPassword(String email) throws MessagingException {
        FleventsAccount account = fleventsAccountService.getAccountByMail(email);
        account.setSecret(UUID.randomUUID().toString());
        account = fleventsAccountService.editAccount(account.getUuid(),account);
        eMailService.sendNewPassword(email,account.getSecret());
    }

    public FleventsAccount editAccount(String accountId, FleventsAccount account){
        return fleventsAccountService.editAccount(accountId, account);
    }
    public void deleteAccount(String accountId){
        FleventsAccount account = fleventsAccountService.getAccountById(accountId);
        account.getEvents().stream().map(eventRegistration -> {
            if(eventRegistration.getEvent().getStartTime().after(Timestamp.from(Instant.now()))){
                eventService.removeAccountFromEvent(eventRegistration.getEvent(),eventRegistration.getAccount(),eventRegistration.getRole());
            }
            return null;
        });
        fleventsAccountService.deleteAccount(account);
    }
}