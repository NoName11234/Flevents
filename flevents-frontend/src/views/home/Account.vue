<script setup lang="ts">
import accountApi from "@/api/accountApi";
import AccountInfo from "@/components/AccountInfo.vue";
import Heading from "@/components/Heading.vue";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import { onMounted, ref } from "vue";
import {logout} from "@/service/authService";
import {useAppStore} from "@/store/app";
import {AxiosError} from "axios";
import organizationsApi from "@/api/organizationsApi";
import {Organization} from "@/models/organization";
import {useRouter} from "vue-router";

const router = useRouter();
const accountStore = useAccountStore();
const appStore = useAppStore();
const showDeleteDialog = ref(false);
const showLeaveDialog = ref(false);
const { currentAccount: account } = storeToRefs(accountStore);
const selectedOrga = ref(undefined as undefined|Organization);
const select = ref(false);

async function deleteAccount() {
  try {
    const response = await accountApi.deleteMe();
    appStore.addToast({
      text: `Ihr Account wurde erfolgreich gelöscht.`,
      color: "success",
    });
    await logout();
  } catch (e) {
    let errorMessage;
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Löschen des Accounts fehlgeschlagen: ${errorMessage}`,
      color: "error",
    });
  } finally {
    showDeleteDialog.value = false;
  }
}

async function leaveOrganization() {
  const organization = selectedOrga.value;
  if (organization === undefined) {
    showLeaveDialog.value = false;
    return;
  }
  try {
    const response = await organizationsApi.leave(organization.uuid, account.value!.uuid);
    appStore.addToast({
      text: `Sie haben die Organisation ${organization.name} erfolgreich verlassen. Zur Sicherheit müssen Sie sich erneut anmelden.`,
      color: "success",
    });
    await router.push({ name: 'accounts.login' });
    await logout();
  } catch (e) {
    let errorMessage;
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Verlassen der Organisation ${organization.name} fehlgeschlagen: ${errorMessage}`,
      color: "error",
    });
  } finally {
    showLeaveDialog.value = false;
  }
}

</script>

<template>
  <Heading
    text="Konto & Info"
    subtext="Ihre Daten und wie wir damit umgehen."
  />
  <AccountInfo :account="account"/>
  <div class="d-flex flex-row justify-end">
    <v-btn
      class="mt-n8 mr-5"
      color="primary"
      icon="mdi-pencil"
      elevation="5"
      :to="{ name: 'accounts.edit' }"
    />
    <v-btn
      class="mt-n8 mr-5"
      color="error"
      icon="mdi-delete"
      elevation="5"
      @click="showDeleteDialog = true"
    />
<!--    <v-btn-->
<!--      class="mt-n8 mr-5"-->
<!--      color="primary"-->
<!--      icon="mdi-logout-variant"-->
<!--      elevation="5"-->
<!--      @click="/* resetCookie(); */ security.resetAccount(); router.push({ name: 'home' });"-->
<!--    />-->
  </div>
  <v-card>
    <v-card-title>
      Ihre Organisationen
    </v-card-title>
    <v-card-text class="d-flex flex-column gap-3">
      <div
        v-for="orga in account.organizationPreviews"
        class="d-flex justify-space-between align-center gap-2"
      >
        <h3>
          {{orga.name}}
        </h3>
        <v-btn
          prepend-icon="mdi-account-remove"
          color="error"
          variant="tonal"
          @click="selectedOrga = orga; showLeaveDialog = true;"
        >
          Verlassen
        </v-btn>
      </div>
    </v-card-text>
  </v-card>


  <v-card>
    <v-card-title>Weitere Informationen</v-card-title>
    <v-card-text>
      "Flevents" ist ein Studentenprojekt im Rahmen einer Vorlesung der DHBW Stuttgart Campus Horb.
    </v-card-text>
    <v-card-actions>
      <v-btn color="primary" role="link" target="_blank" href="https://www.flyndre.de/impressum">
        Impressum
      </v-btn>
      <v-btn color="primary" role="link" target="_blank" href="https://www.flyndre.de/datenschutz">
        Datenschutzerklärung
      </v-btn>
    </v-card-actions>
  </v-card>

  <v-dialog
    persistent
    :model-value="showDeleteDialog"
    class="align-center justify-center"
    width="auto"
  >
    <v-card>
      <v-card-title>
        Account löschen
      </v-card-title>
      <v-card-text>
        Sind sie sicher das sie ihren Account löschen möchten?
        Dies entfernt Sie von allen Organisationen und löscht alle Registrierungen auf Events.
        Falls sie der letzte Administrator einer Organisation oder ein Tutor eines zukünftigen Events sind, können Sie Ihren Account nicht löschen.
      </v-card-text>
    <v-card-actions>
      <v-btn
        color="error"
        prepend-icon="mdi-delete"
        variant="elevated"
        @click="deleteAccount"
      >
        Löschen
      </v-btn>
      <v-btn
        @click="showDeleteDialog = false"
      >
        Abbrechen
      </v-btn>
    </v-card-actions>
    </v-card>
  </v-dialog>

  <v-dialog
    persistent
    :model-value="showLeaveDialog"
    class="align-center justify-center"
    width="auto"
  >
    <v-card>
      <v-card-title>
        Organisation verlassen
      </v-card-title>
      <v-card-text>
        Sind Sie sicher, dass Sie die Organisation {{ selectedOrga?.name }} verlassen wollen?
        Sie können dadurch deren Events nicht mehr erkunden.
        Wenn Sie der letzte Administrator der Organisation sind, können Sie die Organisation nicht verlassen.
      </v-card-text>
      <v-card-actions>
        <v-btn
          color="error"
          prepend-icon="mdi-account-minus"
          variant="elevated"
          @click="leaveOrganization"
        >
          Verlassen
        </v-btn>
        <v-btn
          @click="showLeaveDialog = false"
        >
          Abbrechen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>


</template>
