<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {useAppStore} from "@/store/app";
import {usePlatformStore} from "@/store/platform";
import {storeToRefs} from "pinia";
import {ref} from "vue";
import {Organization} from "@/models/organization";
import {AxiosError} from "axios";
import {VALIDATION} from "@/constants";
import ConsoleApi from "@/api/consoleApi";

const appStore = useAppStore();

const platformStore = usePlatformStore();
const { organizations } = storeToRefs(platformStore);
platformStore.hydrate();

const tooltip = ref('');
const loading = ref(false);
const organization = ref({ name: '' } as Organization);
const firstAdminEmail = ref('');
const form = ref({} as any);

function resetValues() {
  form.value.reset();
  organization.value = { name: '' } as Organization;
  firstAdminEmail.value = '';
}

async function createOrganization() {
  tooltip.value = '';
  if (organization.value.name === '') {
    return;
  }
  if (firstAdminEmail.value === '' || !firstAdminEmail.value.match(VALIDATION.EMAIL)) {
    return;
  }
  loading.value = true;
  try {
    const response = await ConsoleApi.createOrganization(organization.value, firstAdminEmail.value);
    resetValues();
    appStore.addToast({
      text: 'Organisation erfolgreich erstellt.',
      color: 'success',
    });
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        tooltip.value = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        tooltip.value = 'Netzwerkfehler';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
  }
  loading.value = false;
  platformStore.hydrate();
}

async function deleteOrganization(uuid: string) {
  try {
    const response = await ConsoleApi.deleteOrganization(uuid);
    appStore.addToast({
      text: 'Organisation erfolgreich gelöscht.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
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
      text: `Löschen fehlgeschlagen: ${errorMessage}`,
      color: 'error',
    })
  }
  platformStore.hydrate();
}

</script>

<template>
  <Heading text="Plattformkonsole" />

  <v-card :loading="loading" :disabled="loading">
    <v-card-title>
      Neue Organisation
    </v-card-title>
    <v-divider />
    <v-form
      ref="form"
      validate-on="submit"
      @submit.prevent="createOrganization()"
    >
      <v-container class="d-flex flex-column gap-3">
        <v-text-field
          v-model="organization.name"
          label="Name der Organisation"
          prepend-inner-icon="mdi-account-group"
          :rules="[() => organization.name !== '' || 'Dieses Feld wird benötigt.']"
          required
          hide-details="auto"
        />
        <v-text-field
          v-model="firstAdminEmail"
          label="E-Mail-Adresse des ersten Administrators"
          prepend-inner-icon="mdi-at"
          :rules="[() => firstAdminEmail !== '' || 'Dieses Feld wird benötigt.', () => firstAdminEmail?.match(VALIDATION.EMAIL)?.length > 0 || 'Muss eine gültige E-Mail-Adresse sein.']"
          required
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
          prepend-icon="mdi-reload mdi-flip-h"
          @click="resetValues()"
        >
          Zurücksetzen
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          prepend-icon="mdi-content-save"
        >
          Speichern
        </v-btn>
      </v-container>
    </v-form>
  </v-card>

  <v-card>
    <v-card-title>
      Bestehende Organisationen
    </v-card-title>
    <v-divider />
    <v-container class="d-flex flex-column gap-3">
      <v-card
        v-for="(o, oI) in organizations"
        :key="oI"
        elevation="0"
        border
      >
        <v-container
          class="d-flex flex-row align-center gap-3"
        >
          <v-avatar
            :image="o.icon"
            :icon="o.icon ? '' : 'mdi-account-group'"
            class="bg-gradient"
            size="80"
          />
          <div class="flex-grow-1 d-flex flex-column justify-start">
            <small class="text-grey text-break">ID: {{ o.uuid }}</small>
            {{ o.name }}
          </div>
          <v-btn
            icon="mdi-delete"
            size="small"
            variant="text"
            color="error"
            @click="deleteOrganization(oI)"
          />
        </v-container>
      </v-card>
    </v-container>
  </v-card>

</template>

<style scoped>

</style>
