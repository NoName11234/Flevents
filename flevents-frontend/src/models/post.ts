import {Account} from "@/models/account";

export interface Post {

  uuid: string,
  author: Account,
  title: string,
  content: string,
  event: FleventsEventPreview,
  creationDate: Date,
  attachments: Array<string>,
  comments: Array<Comment>,

}
