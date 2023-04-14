package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface FleventsAccountRepository extends JpaRepository<FleventsAccount,String> {
    Optional<FleventsAccount> findByEmail(String email);
}
