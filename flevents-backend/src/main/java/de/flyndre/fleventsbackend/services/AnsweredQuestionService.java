package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionModel;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionModel;
import de.flyndre.fleventsbackend.repositories.AnsweredQuestionRepository;
import de.flyndre.fleventsbackend.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnsweredQuestionService {
    private AnsweredQuestionRepository answeredQuestionRepo;

    public AnsweredQuestionService(AnsweredQuestionRepository answeredQuestionRepo) {
        this.answeredQuestionRepo = answeredQuestionRepo;
    }

    public void deleteQuestion(String questionId){

        Optional<AnsweredQuestionModel> model = answeredQuestionRepo.findAllByUuid(questionId);
        if(model.isPresent()){
            answeredQuestionRepo.delete(model.get());
        }

    }
}
