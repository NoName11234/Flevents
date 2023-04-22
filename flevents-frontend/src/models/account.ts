import {FleventsEventPreview} from "@/models/fleventsEventPreview";
import {OrganizationPreview} from "@/models/organizationPreview";

export interface Account {

  uuid: string,
  firstname: string;
  lastname: string;
  email: string;
  secret : string|undefined;
  role : string;
  eventPreviews: FleventsEventPreview[];
  organizationPreviews: OrganizationPreview[];

}
