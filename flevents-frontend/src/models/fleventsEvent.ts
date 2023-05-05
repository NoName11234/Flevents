import {OrganizationPreview} from "@/models/organizationPreview";
import {AccountPreview} from "@/models/accountPreview";
import {Post} from "@/models/post";
import {Questionnaire} from "@/models/questionnaire";
import {MailConfig} from "@/models/mailConfig";

export interface FleventsEvent {
  uuid?: string;
  name: string;
  description: string;
  location: string;
  image: string;
  startTime: string;
  endTime: string;
  mailConfig: MailConfig;
  organizationPreview: OrganizationPreview;
  accountPreviews: AccountPreview[];
  posts: Post[];
  questionnaires: Questionnaire[];
}
