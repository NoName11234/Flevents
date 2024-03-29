<template>
  <Heading
    :text="`Anmelden zu Event ${event.name}`"
  />
  <v-alert
    v-if="alertAnon == 1"
    type="success"
    title="Erfolgreich angemeldet"
    text="Sie wurden erfolgreich angemeldet! Sie erhalten nun Informationen per Mail zum Event. Sie können diese Seite jetzt schließen."
  ></v-alert>
  <v-alert
    v-if="alertAnon == -1"
    type="error"
    title="Fehler"
    text="Sie wurden nicht zum Event angemeldet! Versuchen sie es bitte nochmal."
  ></v-alert>
  <v-card>
    <v-img
      height="250"
      class="bg-gradient"
      cover
      :src="event?.image ?? ''"
    />
    <template v-if="event?.description">
      <v-container>
        {{event?.description}}
      </v-container>
      <v-divider />
    </template>
    <v-list>
      <v-list-item
        v-if="event?.startTime && event?.endTime"
        prepend-icon="mdi-clock"
        subtitle="Zeitraum"
      >
        {{ DatetimeService.formatDateRange(event?.startTime, event?.endTime)}}
      </v-list-item>
      <v-list-item
        v-if="event?.location"
        prepend-icon="mdi-map-marker"
        subtitle="Ort"
      >
        {{event?.location}}
      </v-list-item>
    </v-list>
  </v-card>

  <!-- Regular Account-Registration -->
  <v-card v-if="!anon">
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
      <div>
        <v-btn
          :to="{ name: 'accounts.forget' }"
          variant="plain"
          target="_blank"
        >
          Passwort vergessen
        </v-btn>
      </div>
      <div
        v-if="tooltip !== ''"
        class="text-error">
        {{tooltip}}
      </div>
    </v-container>
    <v-divider />
    <v-container  class="d-flex flex-column flex-sm-row justify-end gap">

      <v-btn
        @click="anon = true"
        variant="text"
        prepend-icon="mdi-incognito"
      >
        Nur mit Mail-Adresse anmelden
      </v-btn>
      <v-spacer />
      <v-btn
        :to="{ name: 'accounts.create' }"
        target="_blank"
        variant="text"
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

  <!-- Anonymous Registration -->

  <v-card v-if="anon">
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
    <v-container  class="d-flex flex-column flex-sm-row justify-end gap">
      <v-btn
        @click="anon = false"
        target="_blank"
        variant="text"
        prepend-icon="mdi-account"
      >
        Mit Flevents-Account anmelden
      </v-btn>
      <v-spacer />
      <v-btn
        :to="{ name: 'accounts.create' }"
        target="_blank"
        variant="text"
      >
        Registrieren
      </v-btn>
      <v-btn
        @click="anonlogged = true"
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
          am Event
          {{event.name}}
          teilnehmen wollen?
        </h4>
      </v-container>
      <v-container
        v-if="enrollToolTip != ''"
        class="text-red">
        {{enrollToolTip}}
      </v-container>
      <v-container
        class="d-flex justify-end gap"
      >
        <v-btn
          @click="showDialog = false"
          variant="text">
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

  <v-overlay
    max-width="600"
    :model-value="anonlogged"
    class="align-center justify-center"
  >
    <v-card>
      <v-container>
        <h4>
          Sind sie sicher das sie als
          {{account?.email}}
          anonym am Event
          {{event.name}}
          teilnehmen wollen?
        </h4>
      </v-container>
      <v-container
        v-if="enrollToolTip != ''"
        class="text-red">
        {{enrollToolTip}}
      </v-container>
      <v-container
        class="d-flex justify-end gap"
      >
        <v-btn
          @click="anonlogged = false"
          variant="text">
          Abbrechen
        </v-btn>
        <v-btn
          @click="performAnonLogin()"
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
import eventApi from "@/api/eventsApi";
import {login, logout} from "@/service/authService";
import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {useEventStore} from "@/store/events";
import {AxiosError} from "axios";
import {hydrateAll} from "@/service/storesService";
import organizationsApi from "@/api/organizationsApi";
import {OrganizationPreview} from "@/models/organizationPreview";
import {FleventsEventPreview} from "@/models/fleventsEventPreview";
import EventCard from "@/components/EventCard.vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import DatetimeService from "../../service/datetimeService";
import {AccountPreview} from "@/models/accountPreview";

const route = useRoute();
const router = useRouter();

const eventUuid = route.params.uuid as string;
const invitationToken = route.query.token as string;
const alertAnon = ref(0);
const appStore = useAppStore();
const { loggedIn } = storeToRefs(appStore);

const accountStore = useAccountStore()
const { currentAccount } = storeToRefs(accountStore);
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

const event = ref({} as FleventsEventPreview);

const address = ref("");
const showDialog = ref(false);
const loading = ref(false);
const showPass = ref(false);
const tooltip = ref("");
const enrollToolTip = ref("");
const anon = ref(false);
const anonlogged = ref(false);

onMounted(async () => {
  if (!invitationToken) {
    appStore.addToast({
      text: "Die URL beinhaltet kein Einladungs-Token. Haben Sie sie richtig eingefügt?",
      color: "error",
    });
    return;
  }
  try {
    const { data } = await eventApi.getPreview(eventUuid, invitationToken);
    event.value = data as FleventsEventPreview;
  } catch (e) {
    appStore.addToast({
      text: "Das in der URL angegebene Event kann nicht gefunden werden. Womöglich ist die Einladung ungültig.",
      color: "error",
    });
  }
});

async function performLogin(){
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
  try {
    const response = await eventApi.acceptInvitation(eventUuid, invitationToken);
    appStore.addToast({
      text: `Sie sind dem Event beigetreten.`,
      color: "success",
    });
    await router.push({ name: 'home.personal' });
    hydrateAll();
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      } else if (e.response) {
        // already enrolled
        errorMessage = 'Der Account gehört dem Event bereits an oder die Einladung ist ungültig.';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
    enrollToolTip.value = `Beitritt fehlgeschlagen: ${errorMessage}`;
  }
}
async function performAnonLogin(){
  try {
    const response = await eventApi.acceptInvitationAnonymously(eventUuid, account.value.email as string, invitationToken);
    appStore.addToast({
      text: `Sie sind dem Event anonym mit ihrer E-Mail-Adresse beigetreten.`,
      color: "success",
    });
    alertAnon.value = 1;
  } catch (e) {
    alertAnon.value = -1;
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      } else if (e.response) {
        // already enrolled
        errorMessage = 'Der Account gehört dem Event bereits an oder die Einladung ist ungültig.';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
    enrollToolTip.value = `Beitritt fehlgeschlagen: ${errorMessage}`;
  }
  anonlogged.value = false
}

</script>


<style scoped>

</style>
