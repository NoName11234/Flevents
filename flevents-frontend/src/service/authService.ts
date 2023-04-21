/**
 * Service that handles authentication with JSON-Web-Tokens (JWTs).
 * Gets and renews the tokens.
 * Also handles logout.
 * @author David Maier
 * @version %I%
 */

import {useAppStore} from "@/store/app";
import accountApi from "@/api/accountApi";
import api from "@/api/api";
import {AxiosResponse} from "axios";
import router from "@/router";
import {useJwt} from "@vueuse/integrations/useJwt";

// How many milliseconds to refresh the token before its expiry
const refreshBuffer = 60000;

const appStore = useAppStore();
let refreshTimeout: NodeJS.Timeout;
let isRefreshing = false;

/**
 * Processes a response received containing a new access-token.
 * Configures the API to use the token and sets all corresponding states in the stores.
 * Schedules a refreshment of the token.
 * @param response the received response containing the new access-token
 */
function processResponse(response: AxiosResponse) {

  console.log(response)
  const accessToken = response.data.accessToken;

  // Configure API to include token in all requests
  api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

  const decodedToken = useJwt(accessToken);

  const expiryDate = decodedToken.payload.value?.exp;

  if (expiryDate === undefined) throw new Error(`Invalid access token received.`);

  // Set timeout for token refresh
  refreshTimeout = setTimeout(refresh, expiryDate - refreshBuffer);

  appStore.accessTokenExpiry = Date.now() + expiryDate;
  appStore.loggedIn = true;
}

/**
 * Performs an API-call to try to log in with given details.
 * Also sets the 'Authorization'-header in the API-config and schedules the refreshment of the token.
 * @see https://github.com/directus/directus/blob/main/app/src/auth.ts
 * @param email a valid email-address
 * @param plainSecret the corresponding password in plain text (will be encoded inside this function)
 */
export async function login(email: string, plainSecret: string) {

  // TODO: encode secret!
  const encodedSecret = plainSecret;

  const response = await accountApi.validate(email, encodedSecret);

  processResponse(response);
}

/**
 * Performs an API-call to try to refresh the token.
 * Also sets the 'Authorization'-header in the API-config and schedules the refreshment of the token.
 */
export async function refresh() {
  // Do not refresh if not logged-in.
  if (!api.defaults.headers.common['Authorization']) return;

  // Do not refresh if currently refreshing.
  if (isRefreshing) return;

  // Do not refresh if token ist still sufficiently valid.
  if (appStore.accessTokenExpiry && Date.now() < appStore.accessTokenExpiry - refreshBuffer) {
    refreshTimeout = setTimeout(refresh, appStore.accessTokenExpiry - refreshBuffer - Date.now());
    return;
  }

  isRefreshing = true;

  try {
    const response = await accountApi.revalidate();
    processResponse(response);
  } catch (e) {
    await logout();
  } finally {
    isRefreshing = false;
  }
}

/**
 * Performs all necessary actions to log out the currently logged-in user.
 * Also clears any scheduled token-refreshments.
 */
export async function logout() {
  delete api.defaults.headers.common['Authorization'];
  //TODO: post to logout-endpoint of api?
  clearTimeout(refreshTimeout);
  appStore.loggedIn = false;
  router.push({ path: '/' });
}
