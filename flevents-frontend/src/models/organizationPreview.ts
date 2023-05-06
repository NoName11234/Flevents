import {Role} from "@/models/role";
import {MailConfig} from "@/models/mailConfig";

export interface OrganizationPreview {
  uuid: string,
  name: string;
  description: string;
  icon: string;
  role: Role;
  address: string;
  phoneContact: string;
  customerNumber: string;
  mailConfig: MailConfig;
}
