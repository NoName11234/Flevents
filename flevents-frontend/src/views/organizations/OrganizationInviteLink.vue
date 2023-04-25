<template>
  <Heading v-if="organization != undefined" :text="`Anmelden zu Organisation: ${organization.name}`"></Heading>
  <v-card>
    <v-container class="d-flex flex-column gap-3" @keydown.enter="performLogin()">
      <v-text-field
        label="Mailadresse"
        prepend-inner-icon="mdi-email"
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
    <v-container class="d-flex flex-column flex-sm-row justify-end gap">
      <v-btn
        :to="{ name: 'accounts.create' }"
        variant="text"
        target="_blank"
      >
        Registrieren
      </v-btn>
      <v-btn
        @click="performLogin()"
        color="primary"
        prepend-icon="mdi-login-variant"
      >
        Login
      </v-btn>
    </v-container>
  </v-card>

  <v-overlay
    max-width="600"
    :model-value="showDialog"
    class="align-center justify-center"
  >
    <v-card>
      <v-container>
        <h4>
          Sind sie sicher das sie als {{accountStore.currentAccount?.firstname}} {{accountStore.currentAccount?.lastname}} an der Organisation {{organization.name}} teilnehmen wollen?
        </h4>
      </v-container>
      <v-container
        v-if="enrollToolTip != ''"
        class="text-red"
      >
        {{enrollToolTip}}
      </v-container>
      <v-container class="d-flex justify-end gap">
        <v-btn
          @click="showDialog = false"
          variant="text"
        >
          Abbrechen
        </v-btn>
        <v-btn
          @click="enroll()"
          prepend-icon="mdi-check"
          color="primary"
        >
          Anmelden
        </v-btn>
      </v-container>
    </v-card>
  </v-overlay>
</template>


<script setup lang="ts">
import {ref, Ref} from "vue";
import {Account} from "@/models/account";
import Heading from "@/components/Heading.vue";
import {useRoute, useRouter} from "vue-router";
import {login} from "@/service/authService";
import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import OrganizationsApi from "@/api/organizationsApi";
import {useOrganizationStore} from "@/store/organizations";
import {storeToRefs} from "pinia";
import {AxiosError} from "axios";

const route = useRoute();
const router = useRouter();

const appStore = useAppStore();
const { loggedIn } = storeToRefs(appStore);

const accountStore = useAccountStore();
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

const organizationStore = useOrganizationStore();
const organization = organizationStore.getOrganizationGetter(route.params.uuid as string);

const address = ref("");
const showDialog = ref(false);
const loading = ref(false);
const showPass = ref(false);
const tooltip = ref("");
const enrollToolTip = ref("");

async function performLogin() {
  loading.value = true;
  tooltip.value = '';
  try {
    await login(account.value.email!, account.value.secret!);
    // Für performLogin als globale Funktion:
    // Hier ist im normalen login-Formular noch router.push nach /home
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
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
async function enroll(){
  // console.log(JSON.parse(document.cookie.split(";")[0].split("=")[1]).uuid);
  try {
    await OrganizationsApi.acceptInvitation(route.params.uuid as string,  route.query.token as string)
    //await axios.post(`http://localhost:8082/api/organizations/${address.value}/add-account/${accountStore.currentAccount!.uuid as string}`, {}, {params: {token: route.query.token}})
    //const newAccount = await accountApi.getMe();
    //security.setAccount(newAccount.data);
    await router.push({ name: 'organizations.organization', params: { uuid: route.params.uuid as string }});
  } catch (e) {
    // already enrolled
    console.error("Enrollment failed, probably already enrolled.");
    enrollToolTip.value = "Sie sind mit diesem Account schon angemeldet.";
  }
}

</script>


<style scoped>

</style>
