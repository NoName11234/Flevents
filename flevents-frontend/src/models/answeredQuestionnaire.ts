import {AnsweredQuestion} from "@/models/answeredQuestion";

export interface AnsweredQuestionnaire {
  uuid: string;
  userId: string;
  questionnaireId: string;
  answers: AnsweredQuestion[];
}
