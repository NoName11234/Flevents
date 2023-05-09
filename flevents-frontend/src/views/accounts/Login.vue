<template>

  <Heading text="Login" />

  <v-card :loading="loading" :disabled="loading">
    <v-form validate-on="submit" @submit.prevent="performLogin">
      <v-container class="d-flex flex-column gap-3">
        <v-text-field
          label="Mailadresse"
          prepend-inner-icon="mdi-email"
          :rules="[
            () => account.email !== '' || 'Dieses Feld wird benötigt.',
            () => account.email!.match(VALIDATION.EMAIL) !== null || 'E-Mail-Adresse ist ungültig.'
            ]"
          v-model="account.email"
          hide-details="auto"
        />
        <v-text-field
          label="Passwort"
          v-model="account.secret"
          :append-inner-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPass ? 'text' : 'password'"
          @click:append-inner="showPass = !showPass"
          prepend-inner-icon="mdi-lock"
          :rules="[() => account.secret !== '' || 'Dieses Feld wird benötigt.']"
          hide-details="auto"
          required
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
          :to="{ name: 'accounts.forget', query: route.query }"
          variant="text"
        >
          Passwort vergessen
        </v-btn>
        <v-spacer />
        <v-btn
          :to="{ name: 'accounts.create', query: route.query }"
          variant="tonal"
          color="primary"
        >
          Registrieren
        </v-btn>
        <v-btn
          color="primary"
          prepend-icon="mdi-login-variant"
          type="submit"
        >
          Login
        </v-btn>
      </v-container>
    </v-form>
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
import {VALIDATION} from "@/constants";
const route = useRoute();
const showPass = ref(false);
const loading = ref(false);
const tooltip = ref("");
const router = useRouter();
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

async function performLogin(pendingValidation: Promise<any>) {
  loading.value = true;
  tooltip.value = '';
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    loading.value = false;
    tooltip.value = 'Erforderliche Angaben fehlen.';
    return;
  }
  try {
    await login(account.value.email!, account.value.secret!);
    await router.push(decodeURIComponent(route.query.location as string || '/'));
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST || e.code === AxiosError.ERR_BAD_RESPONSE) {
        tooltip.value = 'Ungültige Anmeldedaten';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        tooltip.value = 'Netzwerkfehler';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
  }
  loading.value = false;
}
</script>

<style scoped>

</style>
