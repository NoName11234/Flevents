import {Account} from "@/models/account";
import {Attachment} from "@/models/attachment";

export interface Post {

  uuid: string,
  author: Account,
  title: string,
  content: string,
  creationDate: string,
  attachments?: Array<Attachment>,
  comments?: Array<Comment>,

}
