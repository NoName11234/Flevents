import {FleventsEventPreview} from "@/models/fleventsEventPreview";
import {OrganizationPreview} from "@/models/organizationPreview";
import {Role} from "@/models/role";

export interface Account {

  uuid: string,
  firstname: string;
  lastname: string;
  email: string;
  secret : string|undefined;
  role : Role;
  eventPreviews: FleventsEventPreview[];
  organizationPreviews: OrganizationPreview[];
  platformAdmin: boolean;

}
