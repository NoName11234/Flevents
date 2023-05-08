import {EventRole} from "@/models/eventRole";

export interface EventListFilterOptions {
  organizationUuids?: string[],
  queryText?: string,
  fromDate?: string,
  toDate?: string,
  eventRoles?: EventRole[],
  dateAscending?: boolean,
}
