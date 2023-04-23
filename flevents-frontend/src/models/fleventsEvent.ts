import {OrganizationPreview} from "@/models/organizationPreview";
import {AccountPreview} from "@/models/accountPreview";

export interface FleventsEvent {
  uuid?: string;
  name: string;
  description: string;
  location: string;
  image: string;
  startTime: string;
  endTime: string;
  mailConfig : Object;
  organizationPreview: OrganizationPreview;
  accountPreviews: AccountPreview[];
}
