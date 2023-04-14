import {AnsweredQuestion} from "@/models/answeredQuestion";

export interface AnsweredQuestionnaire {
  uuid: string;
  userId: string;
  questionnaire: string;
  answers: AnsweredQuestion[];
}
