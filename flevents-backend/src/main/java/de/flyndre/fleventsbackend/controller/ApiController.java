package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.repositories.*;
import de.flyndre.fleventsbackend.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello world!";
    }

    @GetMapping("/initdb")
    public HttpStatus initDB() {
        try{
            apiService.initDB();
            return HttpStatus.OK;
        }catch(IOException e){
            System.out.println(e.toString());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
