<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {computed, ref} from "vue";
import {AxiosError} from "axios";
import {useRouter} from "vue-router";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import accountApi from "@/api/accountsApi";
import {VALIDATION} from "@/constants";
import {Account} from "@/models/account";

const router = useRouter();
const accountStore = useAccountStore();
const { currentAccount: savedAccount, loading: storeLoading, error } = storeToRefs(accountStore);
const account = ref({} as Account);

const showPass = ref(false);
const showCorr = ref(false);
const correctPassword = ref("");
const tooltip = ref("");
const formLoading = ref(false);
const loading = computed(() => formLoading.value||storeLoading.value);

async function submit(pendingValidation: Promise<any>) {
  tooltip.value = "";
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    tooltip.value = "Es wurden nicht alle erforderlichen Angaben gemacht.";
    return;
  }
  if (account.value === null) {
    tooltip.value = "Error: Account ist null.";
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
    const response = await accountApi.editMe(account.value);
    if (Math.floor(response.status/100) !== 2) {
      throw new Error(`Request failed ${response}`);
    }
    accountStore.hydrate().then();
    await router.push({name: 'home.account'});
  } catch (e) {
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
    <v-form
      v-if="account"
      validate-on="submit"
      @submit.prevent="submit"
    >
      <v-container class="d-flex flex-column gap-3">

        <div
          class="d-flex flex-column flex-sm-row gap-3"
        >

          <v-text-field
            :model-value="savedAccount!.firstname"
            @input="(e: Event) => account.firstname = (e.target as HTMLInputElement).value"
            label="Vorname"
            prepend-inner-icon="mdi-account"
            :rules="[() => account?.firstname !== '' || 'Dieses Feld wird benötigt.']"
            hide-details="auto"
            required
          />
          <v-text-field
            :model-value="savedAccount!.lastname"
            @input="(e: Event) => account.lastname = (e.target as HTMLInputElement).value"
            label="Nachname"
            prepend-inner-icon="mdi-account"
            :rules="[() => account?.lastname !== '' || 'Dieses Feld wird benötigt.']"
            hide-details="auto"
            required
          />
        </div>

        <v-text-field
          label="Mailadresse"
          :model-value="savedAccount!.email"
          @input="(e: Event) => account.email = (e.target as HTMLInputElement).value"
          prepend-inner-icon="mdi-email"
          :rules="[
            () => account?.email !== '' || 'Dieses Feld wird benötigt.',
            () => account?.email?.match(VALIDATION.EMAIL) !== null || 'Die angegebene E-Mail-Adresse ist ungültig.'
            ]"
          hide-details="auto"
          required
        />
      </v-container>
        <v-divider />
      <v-container class="d-flex flex-column gap-3">
        <small class="text-grey">
          Um Ihr Passwort zu ändern, geben Sie die folgenden Felder an:
        </small>
        <v-text-field
          label="Neues Passwort"
          :model-value="savedAccount!.secret"
          @input="(e: Event) => account.secret = (e.target as HTMLInputElement).value"
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
