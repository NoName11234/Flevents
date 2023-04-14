import {Role} from "@/models/role";

export interface AccountPreview {

  uuid: string,
  firstname: string;
  lastname: string;
  email: string;
  icon: string;
  role: Role;

}
