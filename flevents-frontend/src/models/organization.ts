import {AccountPreview} from "@/models/accountPreview";
import {FleventsEventPreview} from "@/models/fleventsEventPreview";
import {MailConfig} from "@/models/mailConfig";

export interface Organization {
  uuid: string,
  name: string;
  description: string;
  address: string;
  phoneContact: string;
  customerNumber: string;
  icon: string;
  mailConfig: MailConfig;
  accountPreviews: AccountPreview[];
  eventPreviews: FleventsEventPreview[];
}
