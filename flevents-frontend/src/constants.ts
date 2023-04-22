/**
 * Collection of constants for configuring certain parts of the application.
 * @author David Maier
 * @version %I%
 */

/**
 * Constants for store-configuration.
 */
export const STORES = {

  /**
   * The maximum age of cached data in ms before it is refreshed.
   */
  CACHE_MAX_AGE: 60000,

}

/**
 * Constants for auth-configuration.
 */
export const AUTH = {

  /**
   * How many milliseconds to refresh the token before its expiry.
   */
  TOKEN_REFRESH_BUFFER: 60000,

  /**
   * The key to store the most recent authorization token.
   */
  TOKEN_COOKIE_KEY: 'TOKEN',

}

/**
 * Constants for UI-configuration.
 */
export const UI = {

  /**
   * The time for which toasts will be in the toasts queue in ms.
   */
  TOAST_DISPLAY_TIMEOUT: 3000,

}

/**
 * Constants for API-configuration.
 */
export const API = {

  /**
   * The base-URL of the REST-API endpoints.
   */
  BASE_URL: 'http://localhost:8082/api',

}
