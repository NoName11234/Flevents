package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.questionnaire.ChoiceModel;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionModel;
import de.flyndre.fleventsbackend.repositories.ChoiceRepository;
import de.flyndre.fleventsbackend.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {

    private QuestionRepository questionRepo;

    public QuestionService(QuestionRepository questionRepo) {
        this.questionRepo = questionRepo;
    }

    public void deleteQuestion(String questionId){

        Optional<QuestionModel> model = questionRepo.findAllByUuid(questionId);
        if(model.isPresent()){
            questionRepo.delete(model.get());
        }

    }
}
