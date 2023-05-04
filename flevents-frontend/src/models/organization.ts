import {AccountPreview} from "@/models/accountPreview";
import {FleventsEventPreview} from "@/models/fleventsEventPreview";

export interface Organization {
  uuid: string,
  name: string;
  description: string;
  address: string;
  phoneContact: string;
  customerNumber: string;
  icon: string;
  accountPreviews: AccountPreview[];
  eventPreviews: FleventsEventPreview[];
}
