<script setup lang="ts">
import {Ref, ref} from "vue";
import {Account} from "@/models/account";
import axios from "axios";
import {useRouter} from "vue-router";

const router = useRouter();
const showPass = ref(false);
const showCorr = ref(false);
const correctPassword = ref("");
const tooltip = ref("");
const props = defineProps({
  backRoute: {
    required: true,
    type: String,
  },
  submitRoute: {
    required: true,
    type: String,
  },
})
const account : Ref<Partial<Account>> = ref({
  firstname: "",
  lastname: "",
  email: "",
  secret: ""
})

async function submit(){
  // Folgende Zeile niemals entfernen!
  console.log("ich bin echt jetzt in der post methode drin, wie krass");
  tooltip.value = '';
  if(account.value.firstname === "" || account.value.lastname === "" || account.value.email === "" || account.value.secret === "") {
    tooltip.value = "Nicht alle Felder wurden angegeben.";
    return;
  }
  if(account.value.secret !== correctPassword.value) {
    tooltip.value = "Die Passwörter stimmen nicht überein.";
    return;
  }
  try {
    await axios.post("http://localhost:8082/api/accounts", account.value);
  } catch (e) {
    tooltip.value = "Die angegebene E-Mail-Adresse ist bereits registriert.";
    return;
  }
  tooltip.value = "Anfrage erfolgreich gesendet.";
  await router.push(props.backRoute);
}
</script>
<template>
  <v-card>
    <v-form validate-on="submit" @submit.prevent="submit()">
      <v-container class="d-flex flex-column gap-3">
        <div class="d-flex flex-column flex-sm-row gap-3">
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
        <v-text-field
          label="Passwort wiederholen"
          v-model="correctPassword"
          :append-inner-icon="showCorr ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showCorr ? 'text' : 'password'"
          @click:append-inner="showCorr = !showCorr"
          prepend-inner-icon="mdi-lock"
          :rules="[() => correctPassword !== '' || 'Dieses Feld wird benötigt.']"
          hide-details="auto"
          required
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
          :to="backRoute"
        >
          Verwerfen
        </v-btn>
        <v-btn
          color="primary"
          prepend-icon="mdi-check"
          type="submit"
        >
          Registrieren
        </v-btn>
      </v-container>
    </v-form>
  </v-card>
</template>

<style scoped>

</style>
