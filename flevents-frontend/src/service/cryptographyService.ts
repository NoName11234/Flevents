import {sha3_256} from "js-sha3";

/**
 * Performs cryptography on the account to make it send-ready.
 * Encodes the password and makes the email lowercase.
 * @param account the account to perform cryptography on
 */
export function performCryptography(account: { email?: string, secret?: string }) {
  if (account.email)
    account.email = account.email.toLowerCase();
  if (account.secret)
    account.secret = sha3_256(account.secret);
  return account;
}
