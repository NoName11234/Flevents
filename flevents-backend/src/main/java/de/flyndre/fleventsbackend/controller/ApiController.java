package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.services.ApiService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This Class is the Controller for the REST-API path "/api".
 * @author Lukas Burkhardt
 * @version $I$
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiController {

    private ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * @return Hello World! for testing
     */
    @GetMapping("/hello-world")
    public String helloWorld(Authentication auth) {
        System.out.println(auth.getAuthorities().toArray()[0]);
        return "Hello world!";
    }

    /**
     * @return OK, when db is initialysed succesfully, else Internal_Server_Error
     */
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
