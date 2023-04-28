import {Account} from "@/models/account";

class ColorService {
  getAvatarColor(account: Account) {
    const red = account.email.toLowerCase().charCodeAt(0)*3;
    const green = account.firstname.toUpperCase().charCodeAt(0)*2;
    const blue = account.lastname.toUpperCase().charCodeAt(0)*2;
    return `rgb(${red}, ${green}, ${blue})`;
  }
}

export default new ColorService();
