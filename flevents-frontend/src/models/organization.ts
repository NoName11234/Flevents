import {AccountPreview} from "@/models/accountPreview";
import {FleventsEventPreview} from "@/models/fleventsEventPreview";

export interface Organization {
  uuid: string,
  name: string;
  description: string;
  address: string;
  icon: string;
  phoneContact: string;
  accountPreviews: AccountPreview[];
  eventPreviews: FleventsEventPreview[];
}
