<template>

  <Heading text="Passwort zurücksetzen" />

  <v-alert
    v-show="alert == 1"
    type="success"
    title="Neues Passwort versendet"
    text="Sofern sie die korrekte Mail zu ihrem Account eingegeben haben, erhalten sie eine Mail mit einem neuen Passwort!"
  ></v-alert>
  <v-alert
    v-show="alert == -1"
    type="success"
    title="Fehler"
    text="Es ist etwas schief gelaufen! Versuchen sie es nochmal."
  ></v-alert>



  <v-card :loading="loading" :disabled="loading">
    <v-container class="d-flex flex-column gap-3" @keydown.enter="performLogin()">
      <v-text-field
        label="Mailadresse"
        prepend-inner-icon="mdi-email"
        v-model="account.email"
        hide-details="auto"
      />
      <div
        v-if="tooltip !== ''"
        class="text-error">
        {{tooltip}}
      </div>
    </v-container>
    <v-divider />
    <v-container class="d-flex flex-column flex-sm-row justify-end gap">
      <v-btn
        @click="performReset()"
        color="primary"
        prepend-icon="mdi-login-variant"
      >
        neues Einmalpasswort versenden
      </v-btn>
    </v-container>
  </v-card>
</template>

<script setup lang="ts">
import {ref, Ref} from "vue";
import {Account} from "@/models/account";
import {AxiosError} from "axios";
import Heading from "@/components/Heading.vue";
import {useRoute, useRouter} from "vue-router";
import {login} from "@/service/authService";
import {load} from "webfontloader";
import accountsApi from "@/api/accountsApi";
const route = useRoute();
const showPass = ref(false);
const loading = ref(false);
const tooltip = ref("");
const router = useRouter();
const alert = ref(0);
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

async function performReset() {
  loading.value = true;
  tooltip.value = '';
  try {
    await accountsApi.resetPassword(account.value.email!);
   alert.value = 1;
  } catch (e) {
    alert.value = -1;
  }
  loading.value = false;

}
</script>

<style scoped>

</style>
