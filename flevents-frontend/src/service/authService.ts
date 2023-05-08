/**
 * Service that handles authentication with JSON-Web-Tokens (JWTs).
 * Gets and renews the tokens.
 * Also handles logout.
 * Partly taken from Directus' auth.ts.
 * @author David Maier
 * @version %I%
 * @see https://github.com/directus/directus/blob/main/app/src/auth.ts
 */

import {useAppStore} from "@/store/app";
import accountApi from "@/api/accountsApi";
import api from "@/api/api";
import {useJwt} from "@vueuse/integrations/useJwt";
import {dehydrateAll, requestHydrateAll} from "@/service/storesService";
import {AUTH} from "@/constants";

let refreshTimeout: NodeJS.Timeout;
let isRefreshing = false;

/**
 * Internal function to process a response received, containing a new access-token.
 * Configures the API to use the token and sets all corresponding states in the stores.
 * Schedules a refreshment of the token.
 * @param accessToken the access-token
 */
async function processResponse(accessToken: string) {
  const appStore = useAppStore();

  // Configure API to include token in all requests
  api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

  const decodedToken = useJwt(accessToken);

  const expiryDate = decodedToken.payload.value?.exp;
  const accountId = decodedToken.payload.value?.sub;

  if (!expiryDate||!accountId) throw new Error(`Invalid access token received.`);

  // Set timeout for token refresh
  refreshTimeout = setTimeout(refresh, expiryDate - AUTH.TOKEN_REFRESH_BUFFER);

  appStore.accessTokenExpiry = Date.now() + expiryDate;
  appStore.loggedIn = true;
  appStore.currentAccountId = accountId;

  localStorage.setItem(AUTH.TOKEN_COOKIE_KEY, accessToken);

  appStore.globallyLoading = true;
  // Start loading stuff asynchronously to not slow down login
  // Therefore: Keep following statement in .then() syntax!
  await requestHydrateAll();
  appStore.globallyLoading = false
}

/**
 * Performs an API-call to try to log in with given details.
 * Also sets the 'Authorization'-header in the API-config and schedules the refreshment of the token.
 * @param email a valid email-address
 * @param plainSecret the corresponding password in plain text (will be encoded inside this function)
 */
export async function login(email: string, plainSecret: string) {

  const response = await accountApi.login(email, plainSecret);

  await processResponse(response.data.accessToken);
}

/**
 * Performs an API-call to try to refresh the token.
 * Also sets the 'Authorization'-header in the API-config and schedules the refreshment of the token.
 */
export async function refresh() {
  const appStore = useAppStore();

  // Do not refresh if not logged-in.
  if (!api.defaults.headers.common['Authorization']) return;

  // Do not refresh if currently refreshing.
  if (isRefreshing) return;

  // Do not refresh if token ist still sufficiently valid.
  if (appStore.accessTokenExpiry && Date.now() < appStore.accessTokenExpiry - AUTH.TOKEN_REFRESH_BUFFER) {
    refreshTimeout = setTimeout(refresh, appStore.accessTokenExpiry - AUTH.TOKEN_REFRESH_BUFFER - Date.now());
    return;
  }

  isRefreshing = true;

  try {
    const response = await accountApi.refresh();
    await processResponse(response.data.accessToken);
  } catch (e) {
    appStore.addToast({
      text: `Erneuern der Session fehlgeschlagen. Sie werden abgemeldet.`,
      color: 'error',
    });
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
  const appStore = useAppStore();

  // Try to invalidate token in backend
  try {
    const token = api.defaults.headers.common['Authorization'] as string;
    await accountApi.logout(token);
  } catch (e) {
    // Ignore if failed
    console.warn('Failed to kill session in backend.', e);
  }

  // Remove auth header from all requests
  delete api.defaults.headers.common['Authorization'];

  // Stop refreshment of token
  clearTimeout(refreshTimeout);

  // Clear all related data
  localStorage.removeItem(AUTH.TOKEN_COOKIE_KEY);
  appStore.loggedIn = false;
  appStore.accessTokenExpiry = -1;
  appStore.currentAccountId = undefined;
  await dehydrateAll();
}

/**
 * Tries to restore a previous session using information stored in localstorage.
 * @returns `true` if the restoration was successful, `false` otherwise
 */
export async function tryRestoreSession() {
  const encodedToken = localStorage.getItem(AUTH.TOKEN_COOKIE_KEY);
  if (encodedToken === null) return false;
  await processResponse(encodedToken);
  return true;
}
