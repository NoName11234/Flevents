package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.Role;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaire;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import de.flyndre.fleventsbackend.services.AuthService;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.QuestionnaireService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This Class is the service for the QuestionnaireController class.
 * It provides methods regarding Questionnaires.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Service
public class QuestionnaireControllerService {
    private final AuthService authService;
    private final QuestionnaireService questionnaireService;
    private final EventService eventService;

    public QuestionnaireControllerService(AuthService authService, QuestionnaireService questionnaireService, EventService eventService){
        this.questionnaireService = questionnaireService;
        this.authService = authService;
        this.eventService = eventService;
    }
    /**
     * Validate if the given Authentication matches to the given roles for the given event id.
     * @param auth the Authentication to validate.
     * @param uuid the id of the event in which context the validation should be done.
     * @param roles the event roles that should match.
     * @return true if the given parameters match, false if not.
     */
    public boolean getGranted(Authentication auth, String uuid, List<Role> roles){
        return authService.validateRights(auth, roles, uuid);
    }

    public List<QuestionnaireModel> getQuestionnaires(String eventId){
        return questionnaireService.getQuestionnaires(eventService.getEventById(eventId));
    }

    public QuestionnaireModel getQuestionnaire(String questionnaireId){
        return questionnaireService.getQuestionnaire(questionnaireId);
    }

    public AnsweredQuestionnaire getAnswerFromUser(String questionnaireId, String userId){
        return questionnaireService.getAnswerFromUser()
    }
}
