import {Account} from "@/models/account";
import {Answer} from "@/models/answer";

export interface Poll {
  uuid: string,
  author: Account,
  question: string,
  answer: Answer[],
  date: Date,
  totalAnswerCount: number
}
