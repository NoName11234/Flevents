import {Duration} from "@/models/duration";

export interface MailConfig {
  uuid: string;
  registerMessage: string;
  infoMessage: string;
  infoMessageOffset: Duration;
  feedbackMessage: string;
  feedbackMessageOffset: Duration;
  organizationInvitation: string;
  eventInvitation: string;
}
