package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.dtos.questionaire.AnsweredQuestionnaire;
import de.flyndre.fleventsbackend.dtos.questionaire.Questionnaire;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/questionnaires")
public class QuestionnaireController {
    private List<Questionnaire> questionnaireList = new ArrayList<Questionnaire>();
    private List<AnsweredQuestionnaire> answeredQuestionnaires = new ArrayList<>();

    @GetMapping
    public ResponseEntity getQuestionnaires(@RequestParam String eventId){
        ArrayList questionnaires = new ArrayList<Questionnaire>();
        for(Questionnaire questionnaire:questionnaireList){
            if(questionnaire.getEventId().equals(eventId)){
                questionnaires.add(questionnaire);
            }
        }
        return new ResponseEntity<>(questionnaires,HttpStatus.OK);
    }
    @GetMapping("/{questionnaireId}")
    public ResponseEntity getQuestionnaire(@PathVariable String questionnaireId){
       for(Questionnaire questionnaire:questionnaireList){
           if(questionnaire.getUuid().equals(questionnaireId)){
               return new ResponseEntity(questionnaire,HttpStatus.OK);
           }
       }
       return new ResponseEntity<>("Could not found this",HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{questionnaireId}/answers/{userId}")
    public ResponseEntity getAnswers(@PathVariable String questionnaireId,@PathVariable String userId){
        for(AnsweredQuestionnaire questionnaire:answeredQuestionnaires){
            if(questionnaire.getQuestionnaire().equals(questionnaireId)&&questionnaire.getUserId().equals(userId)){
                return new ResponseEntity<>(questionnaire,HttpStatus.OK);
            }
        }
        return new ResponseEntity("Could not found this",HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity createQuestionnaire(@RequestParam String eventId,@RequestBody Questionnaire bodyQuestionnaire){
        bodyQuestionnaire.setUuid(UUID.randomUUID().toString());
        bodyQuestionnaire.setEventId(eventId);
        questionnaireList.add(bodyQuestionnaire);
//        Questionnaire questionnaire = new Questionnaire();
//        questionnaire.setId(UUID.randomUUID().toString());
//        questionnaire.setCreationDate(new Timestamp(System.currentTimeMillis()));
//        questionnaire.setEventId(eventId);
//        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
//        freeTextQuestion.setId(UUID.randomUUID().toString());
//        freeTextQuestion.setQuestion("This is not a question");
//        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
//        singleChoiceQuestion.setId(UUID.randomUUID().toString());
//        singleChoiceQuestion.setQuestion("Is this a question?");
//        singleChoiceQuestion.setChoices(Arrays.asList("Yes","No"));
//        questionnaire.setQuestions(Arrays.asList(freeTextQuestion,singleChoiceQuestion));
        return new ResponseEntity<>(bodyQuestionnaire, HttpStatus.CREATED);
    }
    @PostMapping("/{questionnaireId}")
    public ResponseEntity editQuestionnaire(@PathVariable String questionnaireId,@RequestBody Questionnaire bodyQuestionnaire){
        Questionnaire questionnaire = null;
        for(Questionnaire questionnaireIt:questionnaireList){
            if(questionnaireIt.getEventId().equals(questionnaireId)){
                questionnaire=questionnaireIt;
            }
        }
        if(questionnaire==null){
            return new ResponseEntity<>("Could not found this",HttpStatus.NOT_FOUND);
        }
        int i = questionnaireList.indexOf(questionnaire);
        questionnaireList.remove(questionnaire);
        questionnaireList.add(i,bodyQuestionnaire);
        return new ResponseEntity<>(questionnaire, HttpStatus.OK);
    }

    @DeleteMapping("/{questionnaireId}")
    public ResponseEntity deleteQuestionnaire(@PathVariable String questionnaireId) {
        Optional<Questionnaire> questionnaire = questionnaireList.stream()
                .filter((q) -> q.getUuid().equals(questionnaireId))
                .findFirst();
        if (questionnaire.isEmpty()) {
            return new ResponseEntity<>("No matching questionnaireId.", HttpStatus.NOT_FOUND);
        }
        questionnaireList.remove(questionnaire.get());
        return new ResponseEntity<>("Deleted.", HttpStatus.ACCEPTED);
    }

    @PostMapping("/{questionnaireId}/answers")
    public ResponseEntity addAnswer(@PathVariable String questionnaireId,@RequestBody AnsweredQuestionnaire answeredQuestionnaire){
        for(AnsweredQuestionnaire questionnaire:answeredQuestionnaires){
            if(questionnaire.getQuestionnaire().equals(questionnaireId)&&questionnaire.getUserId().equals(answeredQuestionnaire.getUserId())){
                return new ResponseEntity<>("Already answered",HttpStatus.BAD_REQUEST);
            }
        }
        answeredQuestionnaire.setQuestionnaire(questionnaireId);
        answeredQuestionnaires.add(answeredQuestionnaire);
        return new ResponseEntity(HttpStatus.OK);
    }
}
