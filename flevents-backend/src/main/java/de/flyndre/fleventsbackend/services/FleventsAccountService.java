package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.EmailDetails;
import de.flyndre.fleventsbackend.dtos.EventInformation;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FleventsAccountService {
    private FleventsAccountRepository fleventsAccountRepository;
    private EMailService eMailService;
    private ModelMapper mapper;

    @Autowired
    PasswordEncoder encoder;

    public FleventsAccountService(FleventsAccountRepository fleventsAccountRepository, ModelMapper mapper, EMailService eMailService){
        this.fleventsAccountRepository = fleventsAccountRepository;
        this.mapper = mapper;
        this.eMailService = eMailService;
    }

    /**
     * @param accountId
     * @return
     */
    public FleventsAccount getAccountById(String accountId){
        Optional<FleventsAccount> optional = fleventsAccountRepository.findById(accountId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Found no account for the given id");
        }
        return optional.get();
    }

    public FleventsAccount validate(String email, String secret){
        Optional<FleventsAccount> account = fleventsAccountRepository.findByEmail(email);
        if(account.isEmpty()){
            throw new NoSuchElementException("Theres no account to the given email");
        }
        if(!account.get().getSecret().equals(secret)){
            throw new IllegalArgumentException("The given secret is not valid to this account")
;        }
        return account.get();
    }

    public FleventsAccount getAccountByMail(String mail){
        Optional<FleventsAccount> optional = fleventsAccountRepository.findByEmail(mail);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Theres no account to the given email");
        }
        return optional.get();
    }

    public List<Event> getExploreEvents(FleventsAccount account){
        List<Event> events = account.getOrganisations().stream().map(organizationAccount -> {
            List<Event> information = organizationAccount.getOrganization().getEvents();
            return information;
        }).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

        return events;
    }

    public FleventsAccount createAccount(FleventsAccount account){
        //TODO: Implement
        if(account.getEmail()==null){
            throw new IllegalArgumentException("No email provided");
        }
        if(account.getSecret()==null){
            throw new IllegalArgumentException("No secret provided");
        }
        if(!fleventsAccountRepository.findByEmail(account.getEmail()).isEmpty()){
            throw new IllegalArgumentException("The provided email is already in use");
        }
        account.setUuid(null);
        account.setIsActive(true);
        account.setSecret(encoder.encode(account.getSecret()));
        return fleventsAccountRepository.save(account);
    }
    public FleventsAccount createAnonymousAccount(String email){
        if(fleventsAccountRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("Email already in use");
        }
        FleventsAccount account = new FleventsAccount();
        account.setEmail(email);
        return fleventsAccountRepository.save(account);
    }

    public FleventsAccount editAccount(String accountId, FleventsAccount account){
        Optional<FleventsAccount> existingAcc = fleventsAccountRepository.findById(accountId);
        if (existingAcc.isEmpty()) {
            throw new NoSuchElementException("No account with specified account id found");
        }
        if(account.getEmail() != null) {
            Optional<FleventsAccount> existingAccEmail = fleventsAccountRepository.findByEmail(account.getEmail());
            if(existingAccEmail.isPresent() && !existingAccEmail.get().getUuid().equals(account.getUuid())) {
                throw new IllegalArgumentException("This email is already in use");
            }
        }
        FleventsAccount srcAccount = existingAcc.get();
        srcAccount.merge(account);
        return fleventsAccountRepository.save(srcAccount);
    }

    public void deleteAccount(FleventsAccount account){
        account.setIsActive(false);
        fleventsAccountRepository.save(account);
    }
}
