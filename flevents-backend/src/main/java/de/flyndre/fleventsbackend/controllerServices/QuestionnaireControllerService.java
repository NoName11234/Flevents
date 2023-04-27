package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.Role;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import de.flyndre.fleventsbackend.Models.questionnaire.ChoiceModel;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionModel;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import de.flyndre.fleventsbackend.dtos.questionnaire.Choice;
import de.flyndre.fleventsbackend.dtos.questionnaire.Question;
import de.flyndre.fleventsbackend.dtos.questionnaire.Questionnaire;
import de.flyndre.fleventsbackend.services.AuthService;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.FleventsAccountService;
import de.flyndre.fleventsbackend.services.QuestionnaireService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final FleventsAccountService fleventsAccountService;

    public QuestionnaireControllerService(AuthService authService, QuestionnaireService questionnaireService, EventService eventService, FleventsAccountService fleventsAccountService){
        this.questionnaireService = questionnaireService;
        this.authService = authService;
        this.eventService = eventService;
        this.fleventsAccountService = fleventsAccountService;
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

    public AnsweredQuestionnaireModel getAnswerFromUser(String questionnaireId, String userId){
        return questionnaireService.getAnswerFromUser(fleventsAccountService.getAccountById(userId),    questionnaireService.getQuestionnaire(questionnaireId));
    }

    public QuestionnaireModel createQuestionnaire(String eventId, Questionnaire questionnaire){
        QuestionnaireModel questionnaireModel = convertQuestionnaireToQuestionnaireModel(questionnaire);
        questionnaireModel.setEvent(eventService.getEventById(eventId));

        return questionnaireService.saveNewQuestionnaireModel(questionnaireModel);
    }

    public QuestionnaireModel editQuestionnaire(String questionnaireId, Questionnaire questionnaire){
        QuestionnaireModel qm = questionnaireService.getQuestionnaire(questionnaireId);
        QuestionnaireModel newQm = convertQuestionnaireToQuestionnaireModel(questionnaire);
    }

    private QuestionnaireModel convertQuestionnaireToQuestionnaireModel(Questionnaire questionnaire){
        QuestionnaireModel questionnaireModel = new QuestionnaireModel();
        questionnaireModel.setUuid(questionnaire.getUuid());
        questionnaireModel.setAnsweredQuestionnaireModels(null);
        questionnaireModel.setCreationDate(questionnaire.getCreationDate());
        questionnaireModel.setEvent(null);
        questionnaireModel.setTitle(questionnaire.getTitle());
        questionnaireModel.setClosingDate(questionnaire.getClosingDate());

        List<QuestionModel> questionModels = new ArrayList<>();

        for(int i=0;i<questionnaire.getQuestions().size();i++){
            Question q =  questionnaire.getQuestions().get(i);
            QuestionModel qm = new QuestionModel();
            List<ChoiceModel> choiceModels = new ArrayList<>();
            for(int a=0; a<q.getChoices().size();a++){
                Choice dto = q.getChoices().get(a);
                ChoiceModel m = new ChoiceModel(dto.getUuid(),dto.getChoice(),qm,null);
                choiceModels.add(m);
            }
            qm.setUuid(q.getUuid());
            qm.setQuestion(q.getQuestion());
            qm.setQuestionnaire(questionnaireModel);
            qm.setChoiceModels(choiceModels);
            questionModels.add(qm);
        }

        questionnaireModel.setQuestions(questionModels);
        return questionnaireModel;
    }

    private QuestionnaireModel
}
