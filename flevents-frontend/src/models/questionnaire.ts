import {Question} from "@/models/question";

export interface Questionnaire {
  uuid: string;
  eventId: string;
  title: string;
  creationDate: string;
  closingDate: string;
  questions: Question[];
}
