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
   * The base-URL of the REST-API endpoints _without_ a trailing slash.
   */
  BASE_URL: 'http://localhost:8082/api',

}

/**
 * Constants for input-validation
 */
export const VALIDATION = {

  /**
   * RegEx to match any valid e-mail-address.
   */
  EMAIL: /([-!#-'*+/-9=?A-Z^-~]+(\.[-!#-'*+/-9=?A-Z^-~]+)*|"([]!#-[^-~ \t]|(\\[\t -~]))+")@([0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?(\.[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?)*|\[((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|IPv6:((((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){6}|::((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){5}|[0-9A-Fa-f]{0,4}::((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){4}|(((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):)?(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}))?::((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){3}|(((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){0,2}(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}))?::((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){2}|(((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){0,3}(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}))?::(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):|(((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){0,4}(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}))?::)((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3})|(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3})|(((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){0,5}(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}))?::(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3})|(((0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}):){0,6}(0|[1-9A-Fa-f][0-9A-Fa-f]{0,3}))?::)|(?!IPv6:)[0-9A-Za-z-]*[0-9A-Za-z]:[!-Z^-~]+)])/,

  /**
   * Maximum file size of thumbnails for events in byte.
   */
  MAX_THUMBNAIL_SIZE: 2000000,

  /**
   * Maximum file size of avatars such as organization icons in byte.
   */
  MAX_AVATAR_SIZE: 500000,

  /**
   * Maximum file size of attachments of posts in byte.
   */
  MAX_ATTACHMENT_SIZE: Number.MAX_VALUE,

}
