// Utilities
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    globallyLoading: false,
    toasts: [] as string[],
  }),
  actions: {
    addToast(toastText: string) {
      this.toasts.push(toastText);
      setTimeout(() => this.toasts.shift(), 3000);
    }
  }
})
