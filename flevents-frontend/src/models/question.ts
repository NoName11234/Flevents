import {Choices} from "@/models/choices";
import {QuestionType} from "@/models/questionType";

export type Question = {
  uuid: string;
  question: string;
  questionType: QuestionType
  choices: Choices[];
}
