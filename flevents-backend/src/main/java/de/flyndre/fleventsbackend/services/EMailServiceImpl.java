package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
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

@Component
public class EMailServiceImpl implements EMailService{
    private JavaMailSender javaMailSender;
    private ModelMapper mapper;
    @Value("${application.organizationjoinurl}")
    private String organizationJoinPath;
    @Value("${application.eventjoinurl}")
    private String eventJoinPath;

    public EMailServiceImpl(JavaMailSender javaMailSender, ModelMapper modelMapper){
        this.javaMailSender=javaMailSender;
        this.mapper = modelMapper;
    }

    @Value("${spring.mail.username}") private String sender;
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

    @Override
    public void sendMailWithAttachment(EmailDetails details) {

    }

    @Override
    public void sendOrganizationInvitation(Organization organization, String emailAddress, String token) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("Invitation to be a part of "+organization.getName());
        details.setMsgBody("You are invited to join the organization "+organization.getName()+" at the flevents event manage platform. To join click the following link: "+ organizationJoinPath +organization.getUuid()+"?token="+token);
        sendSimpleEmail(details);
    }

    @Override
    public void sendEventInvitaion(Event event, String emailAddress, String token) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("Invitation to be part of "+event.getName());
        details.setMsgBody("You are invited to join the event "+event.getName()+" at the flevents event manage platform. To join click the following link: "+ eventJoinPath +event.getUuid()+"?token="+token);
        sendSimpleEmail(details);
    }

    @Override
    public void sendNewPassword(String emailAddress, String secret) throws MessagingException {
        EmailDetails details = new EmailDetails();
        details.setTo(new ArrayList<String>(Arrays.asList(emailAddress)));
        details.setSubject("New flevents password");
        details.setMsgBody("Here is your new password for you flevents account. Use this to login to your account. Please change your password in the account settings immediately. \n Password: "+secret);
        sendSimpleEmail(details);
    }
}
