import {AnsweredSingleChoiceQuestion} from "@/models/answeredSingleChoiceQuestion";
import {AnsweredFreeTextQuestion} from "@/models/answeredFreeTextQuestion";
import {QuestionType} from "@/models/questionType";
import {Choices} from "@/models/choices";

export type AnsweredQuestion = {
  uuid : string;
  questionType : QuestionType
  choice: Choices;
  answer: string;
}
