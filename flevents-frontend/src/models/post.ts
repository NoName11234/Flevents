import {Account} from "@/models/account";

export interface Post {

  uuid: string,
  author: Account,
  title: string,
  content: string,
  date: Date,
  attachments: Array<string>,

}
