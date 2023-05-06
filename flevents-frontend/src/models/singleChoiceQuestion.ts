import {SingleChoiceQuestionChoice} from "@/models/singleChoiceQuestionChoice";

export interface SingleChoiceQuestion {
  uuid: string;
  question: string;
  choices: SingleChoiceQuestionChoice[];
}
