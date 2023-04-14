import {QuestionType} from "@/models/questionType";

export interface FreeTextQuestion {
  uuid: string;
  question: string;
  type: QuestionType.FreeText;
}
