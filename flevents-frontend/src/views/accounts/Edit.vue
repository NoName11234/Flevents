<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {Ref, ref} from "vue";
import {Account} from "@/models/account";
import axios from "axios";
import {useRouter} from "vue-router";
import security from "@/service/security";

const router = useRouter();
const showPass = ref(false);
const showCorr = ref(false);
const correctPassword = ref("");
const tooltip = ref("");
const account : Ref<Partial<Account>> = ref({
  secret: ""
});

// onMounted(() => {
//   account.value = JSON.parse(document.cookie.split(";")[0].split("=")[1]);
//   account.value.secret = "";
// })
account.value = security.getAccount() as Account;

async function submit(){
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
  tooltip.value = '';
  await axios.post(`http://h3005487.stratoserver.net:8082/api/accounts/${account.value.uuid}`, account.value);
  const response2 = await axios.get(`http://h3005487.stratoserver.net:8082/api/accounts/${account.value.uuid}`);
  document.cookie = `ACCOUNT=${JSON.stringify(response2.data)}`;
  security.setAccount(response2.data);
  tooltip.value = "Anfrage erfolgreich gesendet.";
  await router.push({name: 'home.account'});
}
</script>

<template>
  <Heading text="Konto bearbeiten" />

  <v-card>
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
