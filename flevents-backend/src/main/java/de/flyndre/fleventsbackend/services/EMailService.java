package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.dtos.EmailDetails;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

@Component
public interface EMailService{

    void sendSimpleEmail(EmailDetails details) throws MessagingException;

    void sendMailWithAttachment(EmailDetails details);

    void sendOrganizationInvitation(Organization organization,String emailAddress,String token) throws MessagingException;
    void sendEventInvitaion(Event event, String emailAddress, String token) throws MessagingException;
    void sendNewPassword(String emailAddress, String secret) throws MessagingException;
    void sendReminder(Event event, String emailAddress) throws MessagingException;
    void sendFeedback(Event event, String emailAddress) throws MessagingException;
}
