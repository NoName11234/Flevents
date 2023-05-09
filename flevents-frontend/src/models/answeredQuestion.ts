import {QuestionType} from "@/models/questionType";
import {Choice} from "@/models/choice";

export type AnsweredQuestion = {
  uuid: string;
  questionType: QuestionType;
  choice: Choice;
  answer: string;
}
