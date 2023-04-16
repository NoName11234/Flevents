package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EmailDetails;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FleventsAccountService {
    private FleventsAccountRepository fleventsAccountRepository;
    private EMailService eMailService;
    private ModelMapper mapper;

    public FleventsAccountService(FleventsAccountRepository fleventsAccountRepository, ModelMapper mapper, EMailService eMailService){
        this.fleventsAccountRepository = fleventsAccountRepository;
        this.mapper = mapper;
        this.eMailService = eMailService;
    }
    public Optional<FleventsAccount> getUser(String accountId){
        return fleventsAccountRepository.findById(accountId);
    }

    public ResponseEntity getAccountPreview(String email, String secret){
        Optional<FleventsAccount> account = fleventsAccountRepository.findByEmail(email);
        if(account.isEmpty()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        if(!account.get().getSecret().equals(secret)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(mapper.map(account.get(), AccountInformation.class),HttpStatus.OK);
    }

    public AccountInformation getAccountInfo(String accountId){
        //TODO: Implement
        FleventsAccount account = fleventsAccountRepository.findById(accountId).get();
        AccountInformation information= mapper.map(account,AccountInformation.class);
        return information;
    }

    public Optional<FleventsAccount> getAccountByMail(String mail){
        return fleventsAccountRepository.findByEmail(mail);
    }

    public FleventsAccount saveAccount(FleventsAccount account){
        return fleventsAccountRepository.save(account);
    }

    public List<EventInformation> getExploreEvents(String accountId){
        //TODO: Implement
        FleventsAccount account = fleventsAccountRepository.findById(accountId).get();
        List<Event> events = account.getOrganisations().stream().map(organizationAccount -> {
            List<Event> information = organizationAccount.getOrganization().getEvents();
            return information;
        }).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

        return events.stream().map(event -> mapper.map(event,EventInformation.class)).collect(Collectors.toList());
    }

    public ResponseEntity createAccount(FleventsAccount account){
        //TODO: Implement
        if(account.getEmail()==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(account.getSecret()==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(!fleventsAccountRepository.findByEmail(account.getEmail()).isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        account.setUuid(null);
        return new ResponseEntity<>(fleventsAccountRepository.save(account),HttpStatus.CREATED);
    }

    public ResponseEntity resetPassword(String email){
        Optional<FleventsAccount> optionalFleventsAccount = fleventsAccountRepository.findByEmail(email);
        if(optionalFleventsAccount.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FleventsAccount account= optionalFleventsAccount.get();
        String secret = UUID.randomUUID().toString();
        account.setSecret(secret);
        fleventsAccountRepository.save(account);
        try {
            eMailService.sendNewPassword(email,secret);
        } catch (MessagingException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity editAccount(String accountId, FleventsAccount account){
        //TODO: Implement
        Optional<FleventsAccount> existingAcc = fleventsAccountRepository.findById(accountId);
        if (existingAcc.isEmpty()) {
            return new ResponseEntity<>("No account with specified account id found", HttpStatus.BAD_REQUEST);
        }
        if(account.getEmail() != null) {
            Optional<FleventsAccount> existingAccEmail = fleventsAccountRepository.findByEmail(account.getEmail());
            if(existingAccEmail.isPresent() && !existingAccEmail.get().getUuid().equals(account.getUuid())) {
                return new ResponseEntity<>("This email is already in use", HttpStatus.BAD_REQUEST);
            }
        }
        FleventsAccount srcAccount = existingAcc.get();
        srcAccount.merge(account);
        return new ResponseEntity<>(mapper.map(fleventsAccountRepository.save(srcAccount),AccountInformation.class), HttpStatus.OK);
    }
}
