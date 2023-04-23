package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.dtos.EmailDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This Service contains logic and usage for the email service.
 * It provides methods regarding different types of emails.
 * @author Lukas Burkhardt
 * @version $I$
 */

@Component
public class EMailServiceImpl implements EMailService{
    private JavaMailSender javaMailSender;
    private ModelMapper mapper;
    @Value("${application.baseurl}")
    private String baseurl;


    public EMailServiceImpl(JavaMailSender javaMailSender, ModelMapper modelMapper){
        this.javaMailSender=javaMailSender;
        this.mapper = modelMapper;
    }

    @Value("${spring.mail.username}") private String sender;

    /**
     * Sends a simple mail defined by the given EmailDetails.
     * @param details all details necessary for sending a mail
     * @throws MessagingException gets thrown if something goes wrong while sending the mail
     */
    @Override
    public void sendSimpleEmail(EmailDetails details) throws MessagingException {
        // Try block to check for exceptions
        // Creating a simple mail message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

        // Setting up necessary details
        helper.setFrom(sender);
        if(details.getTo()!=null){
            for (String s : details.getTo()){
                helper.addTo(s);
            }
        }
        if(details.getCc()!=null){
            for (String s: details.getCc()){
                helper.addCc(s);
            }
        }
        if(details.getBcc()!=null){
            for(String s: details.getBcc()){
                helper.addBcc(s);
            }
        }
        helper.setSubject(details.getSubject());
        helper.setText(details.getMsgBody());

        // Sending the mail
        javaMailSender.send(mimeMessage);
    }

    /**
     * not implemented yet
     * @param details all details necessary for sending a mail
     */
    @Override
    public void sendMailWithAttachment(EmailDetails details) {

    }

    /**
     * Sends an email to the specified address containing an invitation link, combined out of the link to the event and a validation token, to the specified organization.
     * @param organization the organization where the owner of the email gets invited to
     * @param emailAddress the email address to send the mail to
     * @param token the token to validate the invitation link
     * @throws MessagingException gets thrown if something goes wrong while sending the mail
     */
    @Override
    public void sendOrganizationInvitation(Organization organization, String emailAddress, String token) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("Invitation to be a part of "+organization.getName());
        details.setMsgBody("You are invited to join the organization "+organization.getName()+" at the flevents event manage platform. To join click the following link: "+ baseurl+"/join/" +organization.getUuid()+"?token="+token);
        sendSimpleEmail(details);
    }

    /**
     * Sends an email to the specified address containing an invitation link, combined out of the link to the event and a validation token, to the specified event.
     * @param event the event where the owner of the email gets invited to
     * @param emailAddress the email address to send the mail to
     * @param token the token to validate the invitation link
     * @throws MessagingException gets thrown if something goes wrong while sending the mail
     */
    @Override
    public void sendEventInvitaion(Event event, String emailAddress, String token) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("Invitation to be part of "+event.getName());

        if(event.getMailConfig()==null){
            details.setMsgBody("You are invited to join the event "+event.getName()+" at the flevents event manage platform. To join click the following link: "+ baseurl+"/organizations/join/" +event.getUuid()+"?token="+token);
        }else{
            details.setMsgBody(event.getMailConfig().getRegisterMessage());
        }

        sendSimpleEmail(details);
    }

    /**
     * Sends an email to the specified address containing a new temporary password.
     * @param emailAddress the email address to send the mail to
     * @param secret the temporary password
     * @throws MessagingException gets thrown if something goes wrong while sending the mail
     */
    @Override
    public void sendNewPassword(String emailAddress, String secret) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("New flevents password");
        details.setMsgBody("Here is your new password for you flevents account. Use this to login to your account. Please change your password in the account settings immediately. \n Password: "+secret);
        sendSimpleEmail(details);
    }

    /**
     * Sends an email to the specified address containing a default or a custom reminder event text, which can be specified in the mailconfig object of the event.
     * @param event the event to send the reminder for
     * @param emailAddress the email address to send the mail to
     * @throws MessagingException gets thrown if something goes wrong while sending the mail
     */
    @Override
    public void sendReminder(Event event, String emailAddress) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("Reminder for event: "+event.getName());

        if(event.getMailConfig().equals(null)){
            details.setMsgBody("The event "+event.getName()+" starts tomorrow at "+event.getStartTime() + "! Dont miss it!");
        }else{
            details.setMsgBody(event.getMailConfig().getInfoMessage());
        }

        sendSimpleEmail(details);
    }

    /**
     * Sends an email to the specified address containing a default or a custom text with a thank-you-message for participating, which can be specified in the mailconfig object of the event.
     * @param event the event to send the mail for
     * @param emailAddress the email address to send the mail to
     * @throws MessagingException gets thrown if something goes wrong while sending the mail
     */
    @Override
    public void sendFeedback(Event event, String emailAddress) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("Thank you for your participation at "+event.getName());

        if(event.getMailConfig().equals(null)){
            details.setMsgBody("Thank you for your participation at " + event.getName() + ". We hope you had a great time!");
        }else{
            details.setMsgBody(event.getMailConfig().getFeedbackMessage());
        }

        sendSimpleEmail(details);
    }

    /**
     * Sends an email to the specified address containing a default or a custom text with a Alert for participating, which can be specified in the mailconfig object of the event.
     * @param event the event to send the mail for
     */
    @Override
    public void sendAlertMessage(Event event) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setSubject("Last information for "+event.getName());
        details.setBcc(event.getAttendees().stream().map(registration -> registration.getAccount().getEmail()).collect(Collectors.toList()));
        details.setMsgBody(event.getMailConfig().getInfoMessage());
        sendSimpleEmail(details);
    }

    @Override
    public void sendThankMessage(Event event) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setSubject("Thanks to be part of "+event.getName());
        details.setBcc(event.getAttendees().stream().map(registration -> registration.getAccount().getEmail()).collect(Collectors.toList()));
        details.setMsgBody(event.getMailConfig().getFeedbackMessage());
        sendSimpleEmail(details);
    }

    @Override
    public void sendRegistraitionMail(FleventsAccount account) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setSubject("You have been registered on flevents.");
        details.setTo(Arrays.asList(account.getEmail()));
        details.setMsgBody(String.format("Hi %s,\n we are glad that you choose to register you at flevents. You're account is now ready to use. Visit simple our website and enjoy our great tool. \n Your flevents team",account.getFirstname()));
        sendSimpleEmail(details);
    }
}
