import { defineStore } from 'pinia'
import {UI} from "@/constants";

export const useAppStore = defineStore('app', {
  state: () => ({
    /**
     * Whether the app is globally loading.
     */
    globallyLoading: false,
    /**
     * Queue for globally shown toast-messages.
     */
    toasts: [] as string[],
    /**
     * Date of when the current access token will expire.
     */
    accessTokenExpiry: -1,
    /**
     * Whether any user is currently logged-in.
     */
    loggedIn: false,
    /**
     * ID of the currently logged-in account.
     */
    currentAccountId: undefined as string|undefined,
  }),
  actions: {
    /**
     * Adds a toast message to the global toast queue.
     * @param toastText the text of the toast
     */
    addToast(toastText: string) {
      this.toasts.push(toastText);
      setTimeout(() => this.toasts.shift(), UI.TOAST_DISPLAY_TIMEOUT);
    }
  },
})
