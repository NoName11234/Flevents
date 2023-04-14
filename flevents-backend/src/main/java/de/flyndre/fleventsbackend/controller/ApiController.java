package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.repositories.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiController {
    private OrganizationRepository organizationRepository;
    private FleventsAccountRepository fleventsAccountRepository;
    private OrganizationAccountRepository organizationAccountRepository;
    private EventRegistrationRepository eventRegistrationRepository;
    private EventRepository eventRepository;

    public ApiController(OrganizationRepository organizationRepository, FleventsAccountRepository fleventsAccountRepository, OrganizationAccountRepository organizationAccountRepository, EventRegistrationRepository eventRegistrationRepository, EventRepository eventRepository) {
        this.organizationRepository = organizationRepository;
        this.fleventsAccountRepository = fleventsAccountRepository;
        this.organizationAccountRepository = organizationAccountRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello world!";
    }

    @GetMapping("/initdb")
    public FleventsAccount initDB() throws IOException {
        Organization flyndre = new Organization(null,"Flyndre","the best organization of the world",null,null,null,null,null);
        flyndre = organizationRepository.save(flyndre);
        Organization dhbw = new Organization(null,"DHBW","not so the best university of the world",null,null,null,null,null);
        dhbw = organizationRepository.save(dhbw);

        FleventsAccount lukas = fleventsAccountRepository.save(new FleventsAccount(null,"Lukas","Burkhardt","i21005@hb.dhbw-stuttgart.de",null,"You'll never gona gues this!!",null,null));
        FleventsAccount paul = fleventsAccountRepository.save(new FleventsAccount(null,"Paul","Lehmann","paul@hb.dhbw-stuttgart.de",null,"You'll never gonna gues this!!",null,null));
        FleventsAccount ruben = fleventsAccountRepository.save(new FleventsAccount(null,"Ruben","Kraft","ruben@hb.dhbw-stuttgart.de",null,"You'll never gonna guess this!!",null,null));
        FleventsAccount pasi = fleventsAccountRepository.save(new FleventsAccount(null,"Pascal","Fuchs","pasi@hb.dhbw-stuttgart.de",null,"You'll gonna guess this!!",null,null));
        FleventsAccount david = fleventsAccountRepository.save(new FleventsAccount(null, "David", "Maier", "dm@flyndre.de", null, "wireshork", null, null));

        //Event birthday = eventRepository.save(new Event(null,"Birthday","This is my birthday party",null,null,null,"My house",null,flyndre,null));
        //Event exam = eventRepository.save(new Event(null,"Exam","This is the exam from the lecture swe",null,null,null,"Campus Horb",null,dhbw,null));


        organizationAccountRepository.save(new OrganizationAccount(null,flyndre,lukas,OrganizationRole.admin));
        organizationAccountRepository.save(new OrganizationAccount(null,flyndre,paul,OrganizationRole.member));
        organizationAccountRepository.save(new OrganizationAccount(null,flyndre,ruben,OrganizationRole.organizer));
        organizationAccountRepository.save(new OrganizationAccount(null,flyndre,david,OrganizationRole.admin));
        organizationAccountRepository.save(new OrganizationAccount(null,dhbw,lukas,OrganizationRole.admin));
        organizationAccountRepository.save(new OrganizationAccount(null,dhbw,paul,OrganizationRole.member));
        organizationAccountRepository.save(new OrganizationAccount(null,dhbw,david,OrganizationRole.admin));

        //eventRegistrationRepository.save(new EventRegistration(null,birthday,lukas,EventRole.organizer));
        //eventRegistrationRepository.save(new EventRegistration(null,birthday,paul,EventRole.attendee));
        //eventRegistrationRepository.save(new EventRegistration(null,birthday,ruben,EventRole.guest));
        //eventRegistrationRepository.save(new EventRegistration(null,birthday,pasi,EventRole.invited));
        //eventRegistrationRepository.save(new EventRegistration(null,exam,paul,EventRole.organizer));
        //eventRegistrationRepository.save(new EventRegistration(null,exam,lukas,EventRole.tutor));
        //eventRegistrationRepository.save(new EventRegistration(null,exam,ruben,EventRole.attendee));
        //eventRegistrationRepository.save(new EventRegistration(null,exam,david,EventRole.attendee));


        return lukas;
    }
}
