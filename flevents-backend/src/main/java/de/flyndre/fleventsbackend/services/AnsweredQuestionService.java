package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionModel;
import de.flyndre.fleventsbackend.repositories.AnsweredQuestionRepository;
import de.flyndre.fleventsbackend.repositories.AnsweredQuestionnaireRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnsweredQuestionService {
    private AnsweredQuestionRepository answeredQuestionRepo;
    private AnsweredQuestionnaireRepository answeredQuestionnaireRepository;

    public AnsweredQuestionService(AnsweredQuestionRepository answeredQuestionRepo, AnsweredQuestionnaireRepository answeredQuestionnaireRepository) {
        this.answeredQuestionRepo = answeredQuestionRepo;
        this.answeredQuestionnaireRepository = answeredQuestionnaireRepository;
    }

    public void deleteQuestion(String questionId){

        Optional<AnsweredQuestionModel> model = answeredQuestionRepo.findAllByUuid(questionId);
        if(model.isPresent()){
            answeredQuestionRepo.delete(model.get());
        }

    }
}
