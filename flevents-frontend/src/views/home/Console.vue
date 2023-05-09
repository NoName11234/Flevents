<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {useAppStore} from "@/store/app";
import {usePlatformStore} from "@/store/platform";
import {storeToRefs} from "pinia";
import {computed, ref} from "vue";
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
const organization = ref({
  name: '',
  customerNumber: '',
  phoneContact: ''
} as Organization);
const firstAdminEmail = ref('');
const form = ref({} as any);

function resetValues() {
  form.value.reset();
  organization.value = {
    name: '',
    customerNumber: '',
    phoneContact: ''
  } as Organization;
  firstAdminEmail.value = '';
}

async function createOrganization(pendingValidation: Promise<any>) {
  tooltip.value = '';
  loading.value = true;
  const validation = await pendingValidation;
  if (validation?.valid != true) {
    tooltip.value = 'Eine oder mehrere Angaben sind fehlerhaft.';
    loading.value = false;
    return;
  }
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

async function deleteOrganization(organization: Organization) {
  let ok = false;
  ok = confirm(
    `Sind Sie sicher, dass Sie die Organisation ${organization.name} löschen wollen?`
    + ` Damit löschen Sie ${organization.eventPreviews.length} zugehörige Events und deren zugehörige Daten wie Posts und Umfragen.`
    + ` Außerdem löschen Sie die Verbindungen zu ${organization.accountPreviews.length} Accounts.`
    + ` Diese Daten sind danach verloren und können nicht wiederhergestellt werden.`
  );
  if (!ok) return;
  try {
    const response = await ConsoleApi.deleteOrganization(organization.uuid);
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
function checkmacher(string : string | undefined) : string{
  if(string !== undefined){
    return string;
  }
  return "";
}

const searchTerm = ref('');
const filteredOrganizations = computed(() => {
  const term = searchTerm.value.toLowerCase();
  return organizations.value.filter(o => (
    o.description?.toLowerCase().includes(term)
      || o.name?.toLowerCase().includes(term)
      || o.uuid?.toLowerCase().includes(term)
      || o.customerNumber?.toLowerCase().includes(term)
      || o.address?.toLowerCase().includes(term)
      || o.phoneContact?.toLowerCase().includes(term)
    )
  ).sort();
});

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
      @submit.prevent="createOrganization"
    >
      <v-container class="d-flex flex-column gap-3">
        <v-text-field
          v-model="organization.name"
          label="Name"
          prepend-inner-icon="mdi-account-group"
          :rules="[() => organization.name !== '' || 'Dieses Feld wird benötigt.']"
          required
          hide-details="auto"
        />
        <v-text-field
          v-model="organization.customerNumber"
          label="Kundennummer"
          prepend-inner-icon="mdi-identifier"
          :rules="[() => organization.customerNumber !== '' || 'Dieses Feld wird benötigt.']"
          messages="Ist unveränderbar und muss einzigartig sein."
          required
          hide-details="auto"
        />
        <v-text-field
          v-model="organization.phoneContact"
          label="Telefonnummer"
          prepend-inner-icon="mdi-phone"
          :rules="[
            () => organization.phoneContact !== '' || 'Dieses Feld wird benötigt.',
            () => (organization.phoneContact?.match(VALIDATION.PHONE)?.length ?? 0) > 0 || 'Muss mit +(Ländervorwahl) oder 0 beginnen.'
            ]"
          required
          hide-details="auto"
        />
        <v-text-field
          v-model="firstAdminEmail"
          label="E-Mail-Adresse des ersten Administrators"
          prepend-inner-icon="mdi-account-tie"
          :rules="[
            () => firstAdminEmail !== '' || 'Dieses Feld wird benötigt.',
            () => (firstAdminEmail?.match(VALIDATION.EMAIL)?.length ?? 0) > 0 || 'Muss eine gültige E-Mail-Adresse sein.'
            ]"
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

    <v-container>
      <v-text-field
        v-model="searchTerm"
        type="search"
        hide-details="auto"
        variant="outlined"
        label="Suche"
        append-inner-icon="mdi-magnify"
        density="compact"
      />
    </v-container>

    <v-divider />

    <v-expansion-panels
      variant="accordion"
    >
      <v-expansion-panel
        v-for="(o, oI) in filteredOrganizations"
        :key="oI"
        elevation="0"
      >
        <v-expansion-panel-title>
          <div
            class="d-flex flex-row align-center gap-3"
          >
            <v-avatar
              :image="o.icon"
              :icon="o.icon ? '' : 'mdi-account-group'"
              class="bg-gradient"
            />
            <div class="flex-grow-1 d-flex flex-column justify-start">
              {{ o.name }}
            </div>
          </div>
        </v-expansion-panel-title>

        <v-expansion-panel-text>
          <div class="mx-n6 mt-n2 mb-n4">
            <template v-if="o.description">
              <v-container>
                {{ o.description }}
              </v-container>
              <v-divider />
            </template>
            <v-list>
              <v-list-item
                prepend-icon="mdi-database"
                subtitle="Datenbank-UUID"
              >
                {{ o?.uuid}}
              </v-list-item>
              <v-list-item
                v-if="o?.customerNumber"
                prepend-icon="mdi-identifier"
                subtitle="Kundennummer"
              >
                {{ o?.customerNumber}}
              </v-list-item>
              <v-list-item
                v-if="o?.phoneContact"
                prepend-icon="mdi-phone"
                subtitle="Telefonnummer"
              >
                {{ o?.phoneContact}}
              </v-list-item>
              <v-list-item
                v-if="o?.address"
                prepend-icon="mdi-map-marker"
                subtitle="Adresse"
              >
                {{ o?.address }}
              </v-list-item>
            </v-list>
            <v-divider />
            <v-container
              class="d-flex flex-column flex-sm-row justify-end gap"
            >
              <v-btn
                variant="text"
                prepend-icon="mdi-delete"
                color="error"
                @click="deleteOrganization(o)"
              >
                Löschen
              </v-btn>
            </v-container>
          </div>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

  </v-card>

</template>

<style scoped>

</style>
