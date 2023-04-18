import {Account} from "@/models/account";
import {FleventsEventPreview} from "@/models/fleventsEventPreview";
import {Comment} from "@/models/comment";

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
