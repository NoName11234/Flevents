<template>
  <Heading
    :text="organization?.name ?? 'Lade Organisation...'"
    :logo-src="organization?.icon"
  />

  <v-card :loading="loading" :disabled="loading">

    <v-progress-linear
      indeterminate
      absolute
      location="top"
      color="secondary"
      :active="organizationStore.loading"
    />

    <v-tabs
      v-model="tab"
      class="bg-primary"
    >
      <v-tab
        value="info"
        :disabled="organizationStore.loading"
      >
        Informationen
      </v-tab>
      <v-tab
        value="members"
        :disabled="organizationStore.loading"
      >
        Mitglieder
      </v-tab>
    </v-tabs>

    <v-window v-model="tab">
      <v-window-item value="info">
        <v-container>
          {{ organization?.description }}
        </v-container>
        <v-divider></v-divider>
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
        <v-divider/>
        <v-container
          v-if="currentAccountRole === OrganizationRole.admin"
          class="d-flex flex-column flex-sm-row justify-end gap"
        >
          <v-btn
            variant="text"
            prepend-icon="mdi-pencil"
            :to="{ name: 'organizations.edit', params: { uuid: organization?.uuid }}"
          >
            Bearbeiten
          </v-btn>
        </v-container>
      </v-window-item>
      <v-window-item value="members">
        <v-container
          v-if="currentAccountRole === OrganizationRole.admin"
          class="d-flex flex-column flex-sm-row justify-start gap"
        >
          <v-btn
            :to="{ name: 'organizations.invite', params: { uuid: organization?.uuid } }"
            prepend-icon="mdi-account-plus"
            color="primary"
            variant="tonal"
          >
            Mitglieder hinzufügen
          </v-btn>
        </v-container>
        <v-divider />
        <v-table
          fixed-header
        >
          <thead>
            <tr>
              <th>
                Vorname
              </th>
              <th>
                Nachname
              </th>
              <th>
                E-Mail
              </th>
              <th>
                Organisationsrolle
              </th>
              <th v-if="currentAccountRole === OrganizationRole.admin">
                Entfernen
              </th>
            </tr>
          </thead>
          <tbody
            v-if="organization?.accountPreviews"
          >
            <tr
              v-for="(item, index) in organization?.accountPreviews"
              :key="index"
            >
              <td>{{item.firstname}}</td>
              <td>{{item.lastname}}</td>
              <td>{{item.email}}</td>
              <td>
                <v-select
                  :items="[
                    OrganizationRole.admin,
                    OrganizationRole.organizer,
                    OrganizationRole.member
                  ]"
                  density="compact"
                  v-model="item.role"
                  variant="outlined"
                  hide-details="auto"
                  @update:model-value="updateRole(item)"
                  :disabled="
                    (
                      organization?.accountPreviews?.filter(a => a.role === OrganizationRole.admin).length <= 1
                      && item.role === OrganizationRole.admin
                    )
                    || currentAccountRole != OrganizationRole.admin
                  "
                />
              </td>
              <td v-if="currentAccountRole === OrganizationRole.admin">
                <v-btn
                  variant="text"
                  size="small"
                  icon="mdi-delete"
                  @click="removeAccount(item.uuid)"
                  :disabled="
                    organization?.accountPreviews?.filter(a => a.role === OrganizationRole.admin).length <= 1
                    && item.role === OrganizationRole.admin
                  "
                ></v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-window-item>
    </v-window>
  </v-card>
</template>

<script setup lang="ts">
import {computed, ref} from "vue";
import {useRoute} from "vue-router";
import Heading from "@/components/Heading.vue";
import {Organization} from "@/models/organization";
import {OrganizationRole} from "@/models/organizationRole";
import {AccountPreview} from "@/models/accountPreview";
import axios from "axios";
import security from "@/service/security";
import {useOrganizationStore} from "@/store/organizations";
import organizationsApi from "@/api/organizationsApi";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import api from "@/api/api";
import {load} from "webfontloader";
import {useAppStore} from "@/store/app";

const route = useRoute();
const tab = ref(null);
const loading = ref(false);

const appStore = useAppStore();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const organizationStore = useOrganizationStore();
const organization = organizationStore.getOrganizationGetter(route.params.uuid as string);

// TODO: replace authorization with token auth
const currentAccountRole = computed(() => {
  return organization.value?.accountPreviews?.find(a => a.uuid === account.value!.uuid)?.role as OrganizationRole;
});

async function updateRole(newAccount: AccountPreview) {
  const prevRole = account.value!
    .organizationPreviews
    .find(o => o.uuid === organization.value.uuid)!
    .role as OrganizationRole;
  if (newAccount.uuid === account.value!.uuid) {
    const ok = window.confirm(`Sind Sie sicher, dass Sie Ihre eigene Rolle zu "${newAccount.role}" ändern möchten?`);
    if (!ok) {
      newAccount.role = prevRole;
      return;
    }
  }
  try {
    loading.value = true;
    const response = await organizationsApi.changeRole(organization.value.uuid, newAccount.uuid, newAccount.role as OrganizationRole);
  } catch (e) {
    console.log("Failed to update role.", e);
    appStore.addToast("Rolle konnte nicht geupdated werden.");
  } finally {
    loading.value = false;
  }
}

async function removeAccount(uuid : string) {
  try {
    await organizationsApi.removeMember(organization.value.uuid, uuid);
    for (let i = 0; i < organization.value.accountPreviews.length; i++) {
      if (organization.value.accountPreviews[i].uuid === uuid) {
        organization.value.accountPreviews.splice(i,1);
      }
    }
  } catch (e) {
    console.error("Failed to remove account.", e);
  }
}

</script>

<style scoped>

</style>
