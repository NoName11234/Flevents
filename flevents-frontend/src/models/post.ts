import {Account} from "@/models/account";

export interface Post {

  uuid: string,
  author: Account,
  title: string,
  text: string,
  date: Date,
  attachments: Array<string>,

}
