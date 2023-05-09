import {Choice} from "@/models/choice";
import {QuestionType} from "@/models/questionType";

export type Question = {
  uuid: string;
  question: string;
  questionType: QuestionType
  choices: Choice[];
}
