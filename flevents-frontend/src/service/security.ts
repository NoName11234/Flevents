import {Account} from "@/models/account";

const paths = {
  account: 'account'
}

/**
 * Sets the currently logged in account.
 * @param account The new current account.
 */
export function setAccount(account: Account) {
  localStorage.setItem(paths.account, JSON.stringify(account));
}

/**
 * Returns the currently logged in account or `null` if none is logged in.
 */
export function getAccount() {
  return JSON.parse(localStorage.getItem(paths.account) ?? 'null') as Account;
}

/**
 * Resets the logged in account (logout).
 */
export function resetAccount() {
  localStorage.removeItem(paths.account);
}

export default {
  setAccount,
  getAccount,
  resetAccount,
}
