package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FleventsAccountControllerService {
    public ResponseEntity getAccountPreview( String email, String secret){
        return ;
    }

    public AccountInformation getAccountInfo(String accountId){
        return ;
    }

    public List<EventInformation> getBookedEvents(String accountId){
        return ;
    }

    public List<EventInformation> getManagedEvents(String accountId){
        return ;
    }

    public List<EventInformation> getExploreEvents(String accountId){
        return ;
    }

    public List<OrganizationInformation> getManagedOrganization(String accountId){
        return ;
    }

    public ResponseEntity createAccount(FleventsAccount account){
        return ;
    }

    public ResponseEntity resetPassword(String email){
        return ;
    }

    public ResponseEntity editAccount(String accountId, FleventsAccount account){
        return ;
    }
}
