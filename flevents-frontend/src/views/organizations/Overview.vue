<template>
  <Heading
    :text="organization?.name ?? 'Lade Organisation...'"
    :logo-src="organization?.icon"
  />

  <v-card :loading="loading" :disabled="loading">

    <v-tabs
      v-model="tab"
      class="bg-primary"
    >
      <v-tab
        value="info"
      >
        Informationen
      </v-tab>
      <v-tab
        value="members"
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
                Name
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
              <td>{{item.firstname}} {{item.lastname}}</td>
              <td>{{item.email}}</td>
              <td>
                <v-btn
                  append-icon="mdi-chevron-down"
                  variant="outlined"
                  :disabled="
                    (
                      organization?.accountPreviews?.filter(a => a.role === OrganizationRole.admin).length <= 1
                      && item.role === OrganizationRole.admin
                    )
                    || currentAccountRole != OrganizationRole.admin
                  "
                >
                  {{ item.role }}

                  <v-menu activator="parent">
                    <v-list>
                      <v-list-subheader>
                        Rolle wechseln zu:
                      </v-list-subheader>
                      <v-list-item
                        v-for="(role, index) in [
                          OrganizationRole.admin,
                          OrganizationRole.organizer,
                          OrganizationRole.member
                        ]"
                        :key="index"
                        :value="index"
                        :disabled="role === item.role"
                        :title="role.toString()"
                        @click="updateRole(item, role)"
                        density="compact"
                      />
                    </v-list>
                  </v-menu>
                </v-btn>
              </td>
              <td v-if="currentAccountRole === OrganizationRole.admin">
                <v-btn
                  variant="text"
                  size="small"
                  icon="mdi-delete"
                  @click="removeAccount(item)"
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
import {OrganizationRole} from "@/models/organizationRole";
import {AccountPreview} from "@/models/accountPreview";
import axios, {AxiosError} from "axios";
import {useOrganizationStore} from "@/store/organizations";
import organizationsApi from "@/api/organizationsApi";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {useAppStore} from "@/store/app";
import {Account} from "@/models/account";

const route = useRoute();
const tab = ref(null);

const appStore = useAppStore();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const organizationStore = useOrganizationStore();
const organization = organizationStore.getOrganizationGetter(route.params.uuid as string);

const customLoading = ref(false);
const loading = computed(() =>
  customLoading.value
  || organizationStore.specificLoading.get(route.params.uuid as string)
);

// TODO: replace authorization with token auth
const currentAccountRole = computed(() => {
  return organization.value?.accountPreviews?.find(a => a.uuid === account.value!.uuid)?.role as OrganizationRole;
});

async function updateRole(updatedAccount: AccountPreview, newRole: OrganizationRole) {
  if (updatedAccount.uuid === account.value!.uuid) {
    const ok = window.confirm(`Sind Sie sicher, dass Sie Ihre eigene Rolle zu "${newRole}" ändern möchten?`);
    if (!ok) {
      return;
    }
  }
  customLoading.value = true;
  try {
    await organizationsApi.changeRole(route.params.uuid as string, updatedAccount.uuid, updatedAccount.role as OrganizationRole, newRole)
    await organizationStore.hydrateSpecific(route.params.uuid as string);
    appStore.addToast({
      text: 'Rolle aktualisiert.',
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
      text: `Rolle konnte nicht aktualisiert werden: ${errorMessage}`,
      color: 'error',
    });
  }
  customLoading.value = false;
  organizationStore.hydrate();
}

async function removeAccount(account: Account) {
  customLoading.value = true;
  try {
    await organizationsApi.removeMember(route.params.uuid as string, account.uuid);
    await organizationStore.hydrateSpecific(route.params.uuid as string);
    appStore.addToast({
      text: 'Mitglied entfernt.',
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
      text: `Mitglied konnte nicht entfernt werden: ${errorMessage}`,
      color: 'error',
    });
  }
  customLoading.value = false;
  organizationStore.hydrate();
}

</script>

<style scoped>

</style>
