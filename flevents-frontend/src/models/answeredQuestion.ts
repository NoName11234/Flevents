import {AnsweredSingleChoiceQuestion} from "@/models/answeredSingleChoiceQuestion";
import {AnsweredFreeTextQuestion} from "@/models/answeredFreeTextQuestion";

export type AnsweredQuestion = AnsweredSingleChoiceQuestion | AnsweredFreeTextQuestion;
