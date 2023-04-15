package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.repositories.*;
import de.flyndre.fleventsbackend.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ApiService apiService;

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
    public void initDB() throws IOException {
        apiService.initDB();
    }
}
