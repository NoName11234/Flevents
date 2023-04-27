<template>
  <Heading
    :text="`Anmelden zu Organisation ${organization.name}`"
  />

  <v-card>
    <v-container
      class="bg-gradient d-flex flex-column justify-center align-center"
      cover
    >
      <v-avatar
        v-if="organization.icon"
        :image="organization.icon"
        size="180"
        class="bg-grey-lighten-2"
      />
    </v-container>
    <v-container>
      {{organization.description}}
    </v-container>
    <v-divider />
    <v-list>
      <v-list-item
        v-if="organization?.phoneContact"
        prepend-icon="mdi-phone"
      >
        {{ organization?.phoneContact }}
      </v-list-item>
      <v-list-item
        v-if="organization?.address"
        prepend-icon="mdi-map-marker"
      >
        {{ organization?.address }}
      </v-list-item>
    </v-list>
  </v-card>

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
    <v-divider />
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
          Sind sie sicher das sie als
          {{currentAccount?.firstname}}
          {{currentAccount?.lastname}}
          ({{currentAccount?.email}})
          an der Organisation
          {{organization?.name}}
          teilnehmen wollen?
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
import {onMounted, ref, Ref} from "vue";
import {Account} from "@/models/account";
import Heading from "@/components/Heading.vue";
import {useRoute, useRouter} from "vue-router";
import {login, logout} from "@/service/authService";
import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import OrganizationsApi from "@/api/organizationsApi";
import {useOrganizationStore} from "@/store/organizations";
import {storeToRefs} from "pinia";
import {AxiosError} from "axios";
import {hydrateAll} from "@/service/storesService";
import {Organization} from "@/models/organization";
import {OrganizationPreview} from "@/models/organizationPreview";
import organizationsApi from "@/api/organizationsApi";
import DatetimeService from "@/service/datetimeService";

const route = useRoute();
const router = useRouter();

const organizationUuid = route.params.uuid as string;
const invitationToken = route.query.token as string;

const appStore = useAppStore();
const { loggedIn } = storeToRefs(appStore);

const accountStore = useAccountStore();
const { currentAccount } = storeToRefs(accountStore);
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

const organization = ref({} as OrganizationPreview);

const address = ref("");
const showDialog = ref(false);
const loading = ref(false);
const showPass = ref(false);
const tooltip = ref("");
const enrollToolTip = ref("");

onMounted(async () => {
  if (!invitationToken) {
    appStore.addToast({
      text: "Die URL beinhaltet kein Einladungs-Token. Haben Sie sie richtig eingefügt?",
      color: "error",
    });
    return;
  }
  try {
    const { data } = await organizationsApi.getPreview(organizationUuid, invitationToken);
    organization.value = data as OrganizationPreview;
  } catch (e) {
    appStore.addToast({
      text: "Die in der URL angegebene Organisation kann nicht gefunden werden. Womöglich ist die Einladung ungültig.",
      color: "error",
    });
  }
});

async function performLogin() {
  loading.value = true;
  tooltip.value = '';
  try {
    await logout();
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
  showDialog.value = true;
}
async function enroll(){
  // console.log(JSON.parse(document.cookie.split(";")[0].split("=")[1]).uuid);
  try {
    await OrganizationsApi.acceptInvitation(organizationUuid,  invitationToken);
    appStore.addToast({
      text: `Sie sind der Organisation beigetreten.`,
      color: "success",
    });
    await router.push({ name: 'home.account' });
    hydrateAll()
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      } else if (e.response) {
        // already enrolled
        errorMessage = 'Der Account gehört der Organisation bereits an oder die Einladung ist ungültig.';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
    enrollToolTip.value = `Beitritt fehlgeschlagen: ${errorMessage}`;
  }
}

</script>


<style scoped>

</style>
