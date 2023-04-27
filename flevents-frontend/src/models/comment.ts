import {Account} from "@/models/account";
import {Post} from "@/models/post";


export interface Comment {

  uuid: string,
  author: Account,
  content: string,
  post: Post,
  creationDate: Date,

}
