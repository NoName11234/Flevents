package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionModel;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import de.flyndre.fleventsbackend.controllerServices.QuestionnaireControllerService;
import de.flyndre.fleventsbackend.dtos.questionnaire.AnsweredQuestionnaire;
import de.flyndre.fleventsbackend.dtos.questionnaire.Questionnaire;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This Class is the Controller for the REST-API path "/api/questionnaires".
 * It provides an interface regarding questionnaires.
 * @author Paul Lehmann
 * @version $I$
 */
@RestController
@CrossOrigin
@RequestMapping("/api/questionnaires")
public class QuestionnaireController {

    private final QuestionnaireControllerService questionnaireControllerService;
    private final ModelMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);
    private static ResourceBundle strings = ResourceBundle.getBundle("strings.properties");
    public QuestionnaireController(QuestionnaireControllerService questionnaireControllerService, ModelMapper mapper) {
        this.questionnaireControllerService = questionnaireControllerService;
        this.mapper = mapper;
    }

    /**
     * Returns all questionnaires from the specified event.
     * @param eventId the id of the event to get the questionnaires from
     * @param auth the authorization object
     * @return
     */
    @GetMapping
    public ResponseEntity getQuestionnaires(@RequestParam String eventId, Authentication auth){
        if(!questionnaireControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(questionnaireControllerService.getQuestionnaires(eventId), HttpStatus.OK);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{questionnaireId}")
    public ResponseEntity getQuestionnaire(@PathVariable String questionnaireId, Authentication auth){
        if(!questionnaireControllerService.getGranted(auth, questionnaireControllerService.getQuestionnaire(questionnaireId).getEventId(),Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(mapper.map(questionnaireControllerService.getQuestionnaire(questionnaireId), Questionnaire.class),HttpStatus.OK);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{questionnaireId}/answers/{userId}")
    public ResponseEntity getAnswers(@PathVariable String questionnaireId,@PathVariable String userId, Authentication auth){
        //TODO: not Working
        //if(!questionnaireControllerService.getGranted(auth, questionnaireControllerService.getQuestionnaire(questionnaireId).getEventId(), Arrays.asList(EventRole.organizer,EventRole.tutor, EventRole.attendee))){
        //    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
       //}
        try {
            return new ResponseEntity<>(mapper.map(questionnaireControllerService.getAnswerFromUser(questionnaireId, userId), AnsweredQuestionnaire.class),HttpStatus.OK);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity createQuestionnaire(@RequestParam String eventId,@RequestBody Questionnaire bodyQuestionnaire, Authentication auth){
        if(!questionnaireControllerService.getGranted(auth, eventId, Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(mapper.map(questionnaireControllerService.createQuestionnaire(eventId, bodyQuestionnaire), Questionnaire.class), HttpStatus.CREATED);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/{questionnaireId}")
    public ResponseEntity editQuestionnaire(@PathVariable String questionnaireId,@RequestBody Questionnaire bodyQuestionnaire, Authentication auth){
        if(!questionnaireControllerService.getGranted(auth, questionnaireControllerService.getQuestionnaire(questionnaireId).getEventId(), Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(mapper.map(questionnaireControllerService.editQuestionnaire(questionnaireId, bodyQuestionnaire), Questionnaire.class), HttpStatus.OK);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{questionnaireId}")
    public ResponseEntity deleteQuestionnaire(@PathVariable String questionnaireId, Authentication auth) {
        //TODO: not working
        //if(!questionnaireControllerService.getGranted(auth, questionnaireControllerService.getQuestionnaire(questionnaireId).getEventId(), Arrays.asList(EventRole.organizer,EventRole.tutor))){
        //    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        //}
        try {
            questionnaireControllerService.deleteQuestionnaire(questionnaireId);
            return new ResponseEntity<>(strings.getString("questionnaire.QuestionnaireDeleted"), HttpStatus.ACCEPTED);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{questionnaireId}/answers")
    public ResponseEntity addAnswer(@PathVariable String questionnaireId,@RequestBody AnsweredQuestionnaire answeredQuestionnaire, Authentication auth){
        //TODO: not working
        //if(!questionnaireControllerService.getGranted(auth, questionnaireControllerService.getQuestionnaire(questionnaireId).getEventId(), Arrays.asList(EventRole.organizer,EventRole.tutor, EventRole.attendee, EventRole.guest))){
        //    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        //}
        try {
            questionnaireControllerService.addAnswer(questionnaireId, answeredQuestionnaire);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            logger.error(strings.getString("logger.InternalError"),e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{questionnaireId}/statistics")
    public ResponseEntity getStatistics(@PathVariable String questionnaireId, Authentication auth){
        //TODO: Authentification if working again
        //Todo: Errorhandling and Logging
        return new ResponseEntity(questionnaireControllerService.getStatistics(questionnaireId), HttpStatus.OK);
    }
}
