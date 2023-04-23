import { defineStore } from 'pinia'
import {UI} from "@/constants";
import {Toast} from "@/models/toast";

export const useAppStore = defineStore('app', {
  state: () => ({
    /**
     * Whether the app is globally loading.
     */
    globallyLoading: false,
    /**
     * Queue for globally shown toast-messages.
     * **Do not modify directly or the toasts will be displayed permanently!**
     */
    toasts: [] as Toast[],
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
     * @param toast the toast
     */
    addToast(toast: Partial<Toast>) {
      toast.timeout = setTimeout(() => this.toasts.shift(), UI.TOAST_DISPLAY_TIMEOUT);
      this.toasts.push(toast as Toast);
    },

    dehydrate() {
      this.accessTokenExpiry = -1;
      this.loggedIn = false;
      this.currentAccountId = undefined;
    },
  },
})
