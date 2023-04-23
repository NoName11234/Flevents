package de.flyndre.fleventsbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.flyndre.fleventsbackend.services.*;
import de.flyndre.fleventsbackend.dtos.EmailDetails;

import java.util.ArrayList;

/*
This Controller is deprecated!
 */

//@RestController
//@CrossOrigin
//@RequestMapping("/api/mail")
public class EMailController {
    private final EMailService eMailService;

    public EMailController(EMailService eMailService) {
        this.eMailService = eMailService;
    }

    //@PostMapping("/sendMail")
    public ResponseEntity sendMail(@RequestBody JsonNode details) {
        ArrayNode node = (ArrayNode) details.get("to");
        ArrayList to = new ArrayList();
        for(int i = 0; i < node.size(); i++){
            to.add(node.get(i));
        }
        System.out.println(to.get(0));
        EmailDetails detail = new EmailDetails(to, to, to, details.get("subject").asText(), details.get("msgBody").asText(), details.get("attachment").asText());
        try{
            eMailService.sendSimpleEmail(detail);
            return new ResponseEntity(HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
