package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.questionnaire.*;
import de.flyndre.fleventsbackend.dtos.questionnaire.Questionnaire;
import de.flyndre.fleventsbackend.repositories.AnsweredQuestionnaireRepository;
import de.flyndre.fleventsbackend.repositories.ChoiceRepository;
import de.flyndre.fleventsbackend.repositories.QuestionRepository;
import de.flyndre.fleventsbackend.repositories.QuestionnaireRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * This Service handles the Questionnaire usage.
 * It provides methods for manipulating the data of these repositories.
 * @author Lukas Burkhardt
 * @version $I$
 */

@Service
public class QuestionnaireService {
    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final AnsweredQuestionnaireRepository answeredQuestionnaireRepository;
    public QuestionnaireService(QuestionnaireRepository questionnaireRepository, AnsweredQuestionnaireRepository answeredQuestionnaireRepository, QuestionRepository questionRepository, ChoiceRepository choiceRepository){
        this.questionnaireRepository = questionnaireRepository;
        this.answeredQuestionnaireRepository = answeredQuestionnaireRepository;
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
    }

    public List<QuestionnaireModel> getQuestionnaires(Event event){
        return questionnaireRepository.findByEvent_Uuid(event.getUuid());
    }

    public QuestionnaireModel getQuestionnaire(String questionnaireId){
        Optional<QuestionnaireModel> optional = questionnaireRepository.findAllByUuid(questionnaireId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not find a questionnaire with this id");
        }
        return optional.get();
    }

    public AnsweredQuestionnaireModel getAnswerFromUser(FleventsAccount user, QuestionnaireModel questionnaireModel){
        return answeredQuestionnaireRepository.findByUserAndQuestionnaireModel(user, questionnaireModel);
    }

    public QuestionnaireModel saveNewQuestionnaireModel(QuestionnaireModel questionnaireModel){
        return questionnaireRepository.save(questionnaireModel);
    }

    public AnsweredQuestionnaireModel saveNewAnsweredQuestionnaireModel(AnsweredQuestionnaireModel answeredQuestionnaireModel){
        return answeredQuestionnaireRepository.save(answeredQuestionnaireModel);
    }

    public QuestionnaireModel editQuestionnaire(String questionnaireId, QuestionnaireModel questionnaireModel){
        QuestionnaireModel oldQuestionnaireModel = getQuestionnaire(questionnaireId);
        oldQuestionnaireModel.merge(questionnaireModel);
        return questionnaireRepository.save(oldQuestionnaireModel);
    }

    public AnsweredQuestionnaireModel getAnsweredQuestionnaireById(String answeredQuestionnaireId){
        return answeredQuestionnaireRepository.findById(answeredQuestionnaireId).get();
    }

    public void deleteAnsweredQuestionnaire(String answeredQuestionnaireId){
        answeredQuestionnaireRepository.delete(getAnsweredQuestionnaireById(answeredQuestionnaireId));
    }

    public void deleteQuestionnaire(String questionnaireId){
        questionnaireRepository.delete(getQuestionnaire(questionnaireId));
    }
}
