package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FleventsAccountService {
    private FleventsAccountRepository fleventsAccountRepository;

    public FleventsAccountService(FleventsAccountRepository fleventsAccountRepository){
        this.fleventsAccountRepository = fleventsAccountRepository;
    }
    public Optional<FleventsAccount> getUser(String accountId){
        return fleventsAccountRepository.findById(accountId);
    }
}
