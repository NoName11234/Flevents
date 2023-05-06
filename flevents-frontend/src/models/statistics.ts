import {QuestionSummary} from "@/models/questionSummary";

export interface Statistics {
  userCount: number;
  questionSummaries: QuestionSummary[];
}
