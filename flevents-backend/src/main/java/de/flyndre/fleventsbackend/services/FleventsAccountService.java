package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import de.flyndre.fleventsbackend.repositories.FleventsAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

/**
 * This Service provides usage and logic for the FleventsAccount repository.
 * It provides methods for manipulating the data of these repositories.
 * @author Paul Lehmann
 * @version $I$
 */

@Service
public class FleventsAccountService {
    private FleventsAccountRepository fleventsAccountRepository;
    private EMailService eMailService;
    private ModelMapper mapper;
    private static ResourceBundle strings = ResourceBundle.getBundle("ConfigStrings");

    @Autowired
    PasswordEncoder encoder;

    public FleventsAccountService(FleventsAccountRepository fleventsAccountRepository, ModelMapper mapper, EMailServiceImpl eMailService){
        this.fleventsAccountRepository = fleventsAccountRepository;
        this.mapper = mapper;
        this.eMailService = eMailService;
    }

    /**
     * Returns the specified account. Throws an Exception if there is no account with this id.
     * @param accountId the id of the account to be returned
     * @return FleventsAccount specified by its id
     */
    public FleventsAccount getAccountById(String accountId){
        Optional<FleventsAccount> optional = fleventsAccountRepository.findById(accountId);
        if(!optional.isPresent()){
            throw new NoSuchElementException(strings.getString("accountService.NoAccountWithThisId"));
        }
        return optional.get();
    }

    /**
     * Checks whether the secret matches the email. Throws an Exception if the mail does not exist or if the given secret is not matching the mail.
     * @param email the email of the account to validate
     * @param secret the secret to validate
     * @return FleventsAccount linked to the given mail if the secret is correct
     */
    public FleventsAccount validate(String email, String secret){
        Optional<FleventsAccount> account = fleventsAccountRepository.findByEmail(email);
        if(account.isEmpty()){
            throw new NoSuchElementException(strings.getString("accountService.MailNotValid"));
        }
        if(!account.get().getSecret().equals(secret)){
            throw new IllegalArgumentException(strings.getString("accountService.SecretNotValid"))
;        }
        return account.get();
    }

    /**
     * Returns the account linked to the given mail.
     * @param mail the mail to check for whether there is an account
     * @return FleventsAccount with the given mail
     */
    public FleventsAccount getAccountByMail(String mail){
        Optional<FleventsAccount> optional = fleventsAccountRepository.findByEmail(mail);
        if(!optional.isPresent()){
            throw new NoSuchElementException(strings.getString("accountService.NoAccountWithThisMail"));
        }
        return optional.get();
    }

    /**
     * Returns a list of events to explore for the specified account.
     * @param account the account to get the list for
     * @return List<Event> list of events to explore
     */
    public List<Event> getExploreEvents(FleventsAccount account){
        List<Event> events = account.getOrganisations().stream().map(organizationAccount -> {
            List<Event> information = organizationAccount.getOrganization().getEvents();
            return information;
        }).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

        return events;
    }

    /**
     * Creates the given account in the database. Throws an Exception if the given account is invalid or if the email is already in use.
     * @param account the account to be saved
     * @return FleventsAccount the saved account
     */
    public FleventsAccount createAccount(FleventsAccount account){
        if(account.getEmail()==null){
            throw new IllegalArgumentException(strings.getString("accountService.AccountNotCreatedNoMail"));
        }
        if(account.getSecret()==null){
            throw new IllegalArgumentException(strings.getString("accountService.AccountNotCreatedNoSecret"));
        }
        Optional<FleventsAccount> optional;
        if(!(optional= fleventsAccountRepository.findByEmail(account.getEmail())).isEmpty()&&optional.get().getIsActive()){
            throw new IllegalArgumentException(strings.getString("accountService.AccountNotCreatedMailInUse"));
        }
        if(optional.isPresent()){
            FleventsAccount oldAcc = optional.get();
            oldAcc.setEmail(null);
            fleventsAccountRepository.save(oldAcc);
        }
        account.setUuid(null);
        account.setIsActive(true);
        account.setIsPlatformAdmin(false);
        account.setSecret(encoder.encode(account.getSecret()));
        return fleventsAccountRepository.save(account);
    }

    /**
     * Creates an anonymous account in the Database. Throws an Exception if the email is already in use.
     * @param email the email for the account to be created
     * @return FleventsAccount which has been created
     */
    public FleventsAccount createAnonymousAccount(String email){
        Optional<FleventsAccount> optional;
        if(!(optional= fleventsAccountRepository.findByEmail(email)).isEmpty()&&optional.get().getIsActive()){
            throw new IllegalArgumentException(strings.getString("accountService.AnonymAccountNotCreatedMailInUse"));
        }
        if(optional.isPresent()){
            FleventsAccount oldAcc = optional.get();
            oldAcc.setEmail(null);
            fleventsAccountRepository.save(oldAcc);
        }
        FleventsAccount account = new FleventsAccount();
        account.setEmail(email);
        account.setIsActive(false);
        return fleventsAccountRepository.save(account);
    }

    /**
     * Creates an anonymous account in the Database with First and Lastname.
     * @implNote This is for Event-Purpose Only
     * @param email the email for the account to be created
     * @param firstname the firstname for the account to be created
     * @param lastname the lastname for the account to be created
     * @return FleventsAccount which has been created
     */
    public FleventsAccount createAnonymousAccountWithName(String email, String firstname, String lastname){
        if(fleventsAccountRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException(strings.getString("accountService.AnonymAccountWithNameNotCreatedMailInUse"));
        }
        FleventsAccount account = new FleventsAccount();
        account.setEmail(email);
        account.setFirstname(firstname);
        account.setLastname(lastname);
        return fleventsAccountRepository.save(account);
    }


    /**
     * Overwrites an existing account with a new given account object.
     * @param accountId the id of the account to be overwritten
     * @param account the account object with the new values
     * @return overwritten FleventsAccount
     */
    public FleventsAccount editAccount(String accountId, FleventsAccount account){
        Optional<FleventsAccount> existingAcc = fleventsAccountRepository.findById(accountId);
        if (existingAcc.isEmpty()) {
            throw new NoSuchElementException(strings.getString("accountService.AccountIdToBeEditedNotFound"));
        }
        if(account.getEmail() != null) {
            Optional<FleventsAccount> existingAccEmail = fleventsAccountRepository.findByEmail(account.getEmail());
            if(existingAccEmail.isPresent() && !existingAccEmail.get().getUuid().equals(account.getUuid())) {
                throw new IllegalArgumentException(strings.getString("accountService.AccountToBeEditedMailInUse"));
            }
        }
        FleventsAccount srcAccount = existingAcc.get();
        if (account.getSecret() != null) {
            account.setSecret(encoder.encode(account.getSecret()));
        }
        srcAccount.merge(account);
        return fleventsAccountRepository.save(srcAccount);
    }

    /**
     * Deletes the specified account.
     * @param account the account to be deleted
     */
    public void deleteAccount(FleventsAccount account){
        account.setIsActive(false);
        account.setSecret(null);
        fleventsAccountRepository.save(account);
    }

    public void deleteAnsweredQuestionnaireFromAccount(String answeredQuestionnaireId, String accountId){
        FleventsAccount account = getAccountById(accountId);
        List<AnsweredQuestionnaireModel> answeredQuestionnaireModels = account.getAnsweredQuestionnaireModels();

        for(int i = 0;i<answeredQuestionnaireModels.size(); i++){
            if(answeredQuestionnaireModels.get(i).getUuid().equals(answeredQuestionnaireId)){
                answeredQuestionnaireModels.remove(i);
                break;
            }
        }

        account.setAnsweredQuestionnaireModels(answeredQuestionnaireModels);
        fleventsAccountRepository.save(account);
    }

    public void saveAnsweredQuestionnaire(AnsweredQuestionnaireModel answeredQuestionnaireModel, String userId){
        FleventsAccount account = getAccountById(userId);
        List<AnsweredQuestionnaireModel> answers = account.getAnsweredQuestionnaireModels();
        answers.add(answeredQuestionnaireModel);
        account.setAnsweredQuestionnaireModels(answers);
        fleventsAccountRepository.save(account);
    }
}
