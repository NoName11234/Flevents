<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {computed, Ref, ref} from "vue";
import {Account} from "@/models/account";
import axios, {AxiosError, HttpStatusCode} from "axios";
import {useRouter} from "vue-router";
import security from "@/service/security";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import accountApi from "@/api/accountApi";
import api from "@/api/api";

const router = useRouter();
const accountStore = useAccountStore();
const { currentAccount: account, loading: storeLoading, error } = storeToRefs(accountStore);

const showPass = ref(false);
const showCorr = ref(false);
const correctPassword = ref("");
const tooltip = ref("");
const formLoading = ref(false);
const loading = computed(() => formLoading.value||storeLoading.value);

async function submit() {
  tooltip.value = "";
  if (account.value === null) {
    tooltip.value = "Error: Account ist null.";
    return;
  }
  if (account.value.firstname === "" || account.value.lastname === "" || account.value.email === "") {
    tooltip.value = "Nicht alle Felder wurden angegeben.";
    return;
  }
  let secret = account.value.secret;
  if (secret && secret.length > 0) {
    if (correctPassword.value !== secret) {
      tooltip.value = "Die neuen Passwörter stimmen nicht überein.";
      return;
    }
  } else {
    account.value.secret = undefined;
  }
  formLoading.value = true;
  try {
    console.log(api.defaults.headers.common['Authorization']);
    const response = await accountApi.editMe(account.value);
    // const response = await api.post()
    if (Math.floor(response.status/100) !== 2) {
      throw new Error(`Request failed ${response}`);
    }
    accountStore.hydrate().then();
    await router.push({name: 'home.account'});
  } catch (e) {
    console.log('Failed to edit account', e);
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        tooltip.value = 'Ungültige Eingaben';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        tooltip.value = 'Netzwerkfehler';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
  } finally {
    formLoading.value = false;
  }
}
</script>

<template>
  <Heading text="Konto bearbeiten" />

  <v-card :loading="loading" :disabled="loading">
    <v-form validate-on="submit" @submit.prevent="submit()">
      <v-container class="d-flex flex-column gap-3">

        <div class="d-flex gap-2">

          <v-text-field
            v-model="account.firstname"
            label="Vorname"
            prepend-inner-icon="mdi-account"
            :rules="[() => account.firstname !== '' || 'Dieses Feld wird benötigt.']"
            hide-details="auto"
            required
          />
          <v-text-field
            v-model="account.lastname"
            label="Nachname"
            prepend-inner-icon="mdi-account"
            :rules="[() => account.lastname !== '' || 'Dieses Feld wird benötigt.']"
            hide-details="auto"
            required
          />
        </div>

        <v-text-field
          label="Mailadresse"
          v-model="account.email"
          prepend-inner-icon="mdi-email"
          :rules="[() => account.email !== '' || 'Dieses Feld wird benötigt.']"
          hide-details="auto"
          required
        />
      </v-container>
        <v-divider />
      <v-container class="d-flex flex-column gap-3">
        <v-text-field
          label="Neues Passwort"
          v-model="account.secret"
          :append-inner-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPass ? 'text' : 'password'"
          @click:append-inner="showPass = !showPass"
          prepend-inner-icon="mdi-lock"
          hide-details="auto"
        />
        <v-text-field
          label="Neues Passwort wiederholen"
          v-model="correctPassword"
          :append-inner-icon="showCorr ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showCorr ? 'text' : 'password'"
          @click:append-inner="showCorr = !showCorr"
          prepend-inner-icon="mdi-lock"
          hide-details="auto"
        />
        <div
          v-if="tooltip !== ''"
          class="text-error"
        >
          {{tooltip}}
        </div>
      </v-container>
      <v-divider />
      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="text"
          :to="{ name: 'home.account' }"
        >
          Verwerfen
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          prepend-icon="mdi-check"
        >
          Speichern
        </v-btn>
      </v-container>
    </v-form>
  </v-card>

</template>

<style scoped>

</style>
