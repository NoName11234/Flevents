import {QuestionType} from "@/models/questionType";

export interface SingleChoiceQuestion {
  uuid: string;
  question: string;
  choices: string[];
}
