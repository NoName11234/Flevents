import {Account} from "@/models/account";
import {AccountPreview} from "@/models/accountPreview";

class ColorService {

  /**
   * Determines a color based off data of a given account.
   * @param account the account
   */
  getAvatarColor(account: Account|AccountPreview) {
    const red = account.email.toLowerCase().charCodeAt(0)*3;
    const green = account.firstname.toUpperCase().charCodeAt(0)*2;
    const blue = account.lastname.toUpperCase().charCodeAt(0)*2;
    return `rgb(${red}, ${green}, ${blue})`;
  }
}

/**
 * Util-class for determining colors based on input.
 * @author David Maier
 * @since Weekly Build 2
 */
export default new ColorService();
