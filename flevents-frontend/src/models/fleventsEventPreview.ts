import {Role} from "@/models/role";

export interface FleventsEventPreview {
  uuid?: string;
  name: string;
  description: string;
  location: string,
  image: string;
  startTime: string;
  endTime: string;
  role?: Role;
}
