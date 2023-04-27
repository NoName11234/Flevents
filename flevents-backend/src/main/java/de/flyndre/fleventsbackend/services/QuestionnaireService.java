package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import de.flyndre.fleventsbackend.repositories.AnsweredQuestionnaireRepository;
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
    private final AnsweredQuestionnaireRepository answeredQuestionnaireRepository;
    public QuestionnaireService(QuestionnaireRepository questionnaireRepository, AnsweredQuestionnaireRepository answeredQuestionnaireRepository){
        this.questionnaireRepository = questionnaireRepository;
        this.answeredQuestionnaireRepository = answeredQuestionnaireRepository;
    }

    public List<QuestionnaireModel> getQuestionnaires(Event event){
        return questionnaireRepository.findByEvent_Uuid(event.getUuid());
    }

    public QuestionnaireModel getQuestionnaire(String questionnaireId){
        Optional<QuestionnaireModel> optional = questionnaireRepository.findById(questionnaireId);
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
}
