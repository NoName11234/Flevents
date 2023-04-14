// Utilities
import { defineStore } from 'pinia'
import axios from "axios";

export const useLogin = defineStore('login', {
  state: () => ({
    loggedin: false
  }),
})
