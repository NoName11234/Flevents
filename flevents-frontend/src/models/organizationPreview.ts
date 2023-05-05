import {Role} from "@/models/role";

export interface OrganizationPreview {
  uuid: string,
  name: string;
  description: string;
  icon: string;
  role: Role;
  address: string;
  phoneContact: string;
  customerNumber: string;
}
