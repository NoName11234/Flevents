package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Role;
import de.flyndre.fleventsbackend.Models.questionnaire.*;
import de.flyndre.fleventsbackend.dtos.questionnaire.*;
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

    public List<Questionnaire> getQuestionnaires(String eventId){
        List<QuestionnaireModel> questionnaireModels = questionnaireService.getQuestionnaires(eventService.getEventById(eventId));
        List<Questionnaire> questionnaires = new ArrayList<>();
        for(int i=0;i<questionnaireModels.size();i++){
            questionnaires.add(convertQuestionnaireModelToQuestionnaire(questionnaireModels.get(i)));
        }
        return questionnaires;
    }

    public Questionnaire getQuestionnaire(String questionnaireId){
        return convertQuestionnaireModelToQuestionnaire(questionnaireService.getQuestionnaire(questionnaireId));
    }

    public AnsweredQuestionnaireModel getAnswerFromUser(String questionnaireId, String userId){
        return questionnaireService.getAnswerFromUser(fleventsAccountService.getAccountById(userId),    questionnaireService.getQuestionnaire(questionnaireId));
    }

    public QuestionnaireModel createQuestionnaire(String eventId, Questionnaire questionnaire){
        QuestionnaireModel questionnaireModel = convertQuestionnaireToQuestionnaireModel(questionnaire);
        questionnaireModel.setEvent(eventService.getEventById(eventId));
        questionnaireService.saveNewQuestionnaireModel(questionnaireModel);
        //for test purposes
        //eventService.registerNewQuestionnaire(questionnaireModel, eventId);
        return questionnaireModel;
    }

    public QuestionnaireModel editQuestionnaire(String questionnaireId, Questionnaire questionnaire){
        QuestionnaireModel newQm = convertQuestionnaireToQuestionnaireModel(questionnaire);
        return questionnaireService.editQuestionnaire(questionnaireId,newQm);
    }

    public void deleteQuestionnaire(String questionnaireId){
        QuestionnaireModel qm = questionnaireService.getQuestionnaire(questionnaireId);
        eventService.deleteQuestionnaireFromEvent(questionnaireId, qm.getEvent().getUuid());
        List<AnsweredQuestionnaireModel> answeredQuestionnaireModels = qm.getAnsweredQuestionnaireModels();

        for(int i=0;i<answeredQuestionnaireModels.size();i++){
            deleteAnsweredQuestionnaire(answeredQuestionnaireModels.get(i).getUuid());
        }

        questionnaireService.deleteQuestionnaire(questionnaireId);
    }

    public void deleteAnsweredQuestionnaire(String answeredQuestionnaireId){
        AnsweredQuestionnaireModel aqm = questionnaireService.getAnsweredQuestionnaireById(answeredQuestionnaireId);
        fleventsAccountService.deleteAnsweredQuestionnaireFromAccount(answeredQuestionnaireId, aqm.getUser().getUuid());
        questionnaireService.deleteAnsweredQuestionnaire(answeredQuestionnaireId);
    }

    public AnsweredQuestionnaireModel addAnswer(String questionnaireId, AnsweredQuestionnaire answeredQuestionnaire){
        answeredQuestionnaire.setQuestionnaireId(questionnaireId);
        AnsweredQuestionnaireModel newAqm = convertAnsweredQuestionnaireToAnsweredQuestionnaireModel(answeredQuestionnaire);
        //for test purposes
        //fleventsAccountService.saveAnsweredQuestionnaire(newAqm, answeredQuestionnaire.getUserId());
        return questionnaireService.saveNewAnsweredQuestionnaireModel(newAqm);
    }

    private QuestionnaireModel convertQuestionnaireToQuestionnaireModel(Questionnaire questionnaire){
        QuestionnaireModel questionnaireModel = new QuestionnaireModel();
        questionnaireModel.setUuid(questionnaire.getUuid());
        questionnaireModel.setAnsweredQuestionnaireModels(null);
        questionnaireModel.setCreationDate(questionnaire.getCreationDate());
        questionnaireModel.setEvent(eventService.getEventById(questionnaire.getEventId()));
        questionnaireModel.setTitle(questionnaire.getTitle());
        questionnaireModel.setClosingDate(questionnaire.getClosingDate());

        List<QuestionModel> questionModels = new ArrayList<>();

        for(int i=0;i<questionnaire.getQuestions().size();i++){
            Question q =  questionnaire.getQuestions().get(i);
            QuestionModel qm = new QuestionModel();
            List<ChoiceModel> choiceModels = new ArrayList<>();
            if(q.getChoices()!=null) {
                for (int a = 0; a < q.getChoices().size(); a++) {
                    Choice dto = q.getChoices().get(a);
                    ChoiceModel m = new ChoiceModel(dto.getUuid(), dto.getChoice());
                    choiceModels.add(m);
                }
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

    private AnsweredQuestionnaireModel convertAnsweredQuestionnaireToAnsweredQuestionnaireModel(AnsweredQuestionnaire answeredQuestionnaire){
        AnsweredQuestionnaireModel answeredQuestionnaireModel = new AnsweredQuestionnaireModel();
        answeredQuestionnaireModel.setUuid(answeredQuestionnaire.getUuid());
        answeredQuestionnaireModel.setUser(fleventsAccountService.getAccountById(answeredQuestionnaire.getUserId()));
        answeredQuestionnaireModel.setQuestionnaireModel(questionnaireService.getQuestionnaire(answeredQuestionnaire.getQuestionnaireId()));

        List<AnsweredQuestionModel> answerModels = new ArrayList<>();

        for(int i=0;i<answeredQuestionnaire.getAnswers().size();i++){
            AnsweredQuestion aq = answeredQuestionnaire.getAnswers().get(i);
            ChoiceModel cm = null;
            if(aq.getChoice()!=null) {
                cm = new ChoiceModel(aq.getChoice().getUuid(), aq.getChoice().getChoice());
            }
            AnsweredQuestionModel aqm = new AnsweredQuestionModel(aq.getUuid(), cm, aq.getAnswer(), answeredQuestionnaireModel);
            answerModels.add(aqm);
        }

        answeredQuestionnaireModel.setAnswers(answerModels);
        return answeredQuestionnaireModel;
    }

    private Questionnaire convertQuestionnaireModelToQuestionnaire(QuestionnaireModel questionnaireModel){
        Questionnaire questionnaire = new Questionnaire();
        List<Question> questions = new ArrayList<>();
        List<QuestionModel> questionModels = questionnaireModel.getQuestions();

        for(int a=0;a<questionModels.size();a++){
            QuestionModel questionModel = questionModels.get(a);
            Question question = new Question();
            List<Choice> choices = new ArrayList<>();
            List<ChoiceModel> choiceModels = questionModel.getChoiceModels();

            for(int b=0;b<choiceModels.size();b++){
                ChoiceModel choiceModel = choiceModels.get(b);
                Choice choice = new Choice();

                choice.setChoice(choiceModel.getChoice());
                choice.setUuid(choiceModel.getUuid());
                choices.add(choice);
            }

            question.setChoices(choices);
            question.setQuestion(questionModel.getQuestion());
            question.setUuid(questionModel.getUuid());
            questions.add(question);
        }
        questionnaire.setQuestions(questions);
        questionnaire.setUuid(questionnaireModel.getUuid());
        questionnaire.setTitle(questionnaireModel.getTitle());
        questionnaire.setCreationDate(questionnaireModel.getCreationDate());
        questionnaire.setClosingDate(questionnaireModel.getClosingDate());

        return questionnaire;
    }
}
