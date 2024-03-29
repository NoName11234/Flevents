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
        value="mails"
      >
        E-Mail-Vorlagen
      </v-tab>
      <v-tab
        value="members"
      >
        Mitglieder
      </v-tab>
    </v-tabs>

    <v-window v-model="tab">

      <v-window-item value="info">
        <template v-if="organization?.description">
          <v-container>
            {{ organization?.description }}
          </v-container>
          <v-divider />
        </template>
        <template v-if="organization.address || organization.phoneContact">
          <v-list>
            <v-list-item
              v-if="organization?.customerNumber"
              prepend-icon="mdi-identifier"
              subtitle="Kundennummer"
            >
              {{ organization?.customerNumber}}
            </v-list-item>
            <v-list-item
              v-if="organization?.phoneContact"
              prepend-icon="mdi-phone"
              subtitle="Telefonnummer"
            >
              {{ organization?.phoneContact}}
            </v-list-item>
            <v-list-item
              v-if="organization?.address"
              prepend-icon="mdi-map-marker"
              subtitle="Adresse"
            >
              {{ organization?.address }}
            </v-list-item>
          </v-list>
          <v-divider/>
        </template>
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

      <v-window-item value="mails">
        <v-expansion-panels
          variant="accordion"
          multiple
        >
          <MailConfigCardOrganization
            :config="organization?.mailConfig"
            @update="updateMailConfig"
          />
        </v-expansion-panels>
      </v-window-item>

      <v-window-item value="members">
        <v-container
          v-if="currentAccountRole === OrganizationRole.admin"
          class="d-flex flex-column flex-sm-row justify-start gap"
        >
          <v-btn
            :to="{ name: 'organizations.invite', params: { uuid: organization?.uuid } }"
            prepend-icon="mdi-email-fast"
            color="primary"
            variant="tonal"
          >
            Mitglieder einladen
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
            v-if="members"
          >
            <tr
              v-for="(item, index) in members"
              :key="index"
            >
              <td>
                {{item.firstname}}&nbsp;{{item.lastname}}
              </td>
              <td>
                {{item.email}}
              </td>
              <td>
                <v-btn
                  append-icon="mdi-chevron-down"
                  variant="text"
                  class="d-flex flex-row justify-space-between"
                  block
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
import {computed, onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import Heading from "@/components/Heading.vue";
import {OrganizationRole} from "@/models/organizationRole";
import {AccountPreview} from "@/models/accountPreview";
import {AxiosError} from "axios";
import {useOrganizationStore} from "@/store/organizations";
import organizationsApi from "@/api/organizationsApi";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {useAppStore} from "@/store/app";
import router from "@/router";
import MailConfigCardOrganization from "@/components/MailConfigCardOrganization.vue";
import {MailConfig} from "@/models/mailConfig";

const route = useRoute();
const organizationUuid = route.params.uuid as string;
const tab = computed({
  get: () => route.query.tab ?? 'info',
  set: (tabValue) => router.replace({ ...route, query: { ...route.query, tab: tabValue }}),
});

const appStore = useAppStore();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const organizationStore = useOrganizationStore();
const organization = organizationStore.getOrganizationGetter(organizationUuid);

const members = computed(() => {
  return organization?.value?.accountPreviews as AccountPreview[];
});

const customLoading = ref(false);
const loading = computed(() =>
  customLoading.value
  || organizationStore.specificLoading.get(route.params.uuid as string)
);

const currentAccountRole = computed(() => {
  return organization.value?.accountPreviews?.find(a => a.uuid === account.value!.uuid)?.role as OrganizationRole;
});

async function updateRole(updatedAccount: AccountPreview, newRole: OrganizationRole) {
  if (updatedAccount.uuid === account.value!.uuid) {
    // Current user changes own role and possibly removes his access
    const ok = window.confirm(
      `Sind Sie sicher, dass Sie Ihre eigene Rolle zu "${newRole}" ändern möchten?`
      + ` Sie verlieren damit alle Rechte, die Sie als ${currentAccountRole.value} besitzen.`
    );
    if (!ok) {
      return;
    }
  }
  customLoading.value = true;
  try {
    await organizationsApi.changeRole(organizationUuid, updatedAccount.uuid, updatedAccount.role as OrganizationRole, newRole)
    await organizationStore.hydrateSpecific(organizationUuid);
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

async function updateMailConfig(config: MailConfig) {
  try {
    const response = await organizationsApi.addMailConfig(organization.value.uuid, config);
    appStore.addToast({
      text: 'Mail-Konfiguration aktualisiert!',
      color: 'success',
    });
  } catch (e) {
    console.error('Failed to delete event.', e);
    appStore.addToast({
      text: 'Fehler beim Aktualisieren der Mail-Konfiguration.',
      color: 'error',
    });
  }
  organizationStore.hydrateSpecific(organization.value.uuid);
}
async function removeAccount(removeAccount: AccountPreview) {
  let ok = false;
  if (removeAccount.uuid === account.value!.uuid) {
    // Current user removes himself and therefore removes his access to the organization
    ok = window.confirm(
      `Sind Sie sicher, dass Sie Sich aus der Organisation ${organization.value.name} entfernen wollen?`
      + ` Ihnen werden damit alle Rechte in ihr entzogen und Sie haben keinen Zugriff mehr auf deren Events.`
      + ` Der Zugriff auf vergangene Events, an denen Sie teilgenommen haben und zukünftige Events, für die Sie registriert sind, bleibt erhalten.`
      + ` Um erneut Zugriff zu erhalten, müssen Sie erneut zur Organisation eingeladen werden.`
    );
  } else {
    // Someone else is being removed
    ok = window.confirm(
      `Sind Sie sicher, dass Sie ${removeAccount.firstname} ${removeAccount.lastname} (${removeAccount.email}) aus der Organisation ${organization.value.name} entfernen wollen?`
      + ` Der Person werden damit alle Rechte in ihr entzogen und sie hat keinen Zugriff mehr auf Events der Organisation.`
      + ` Der Zugriff auf vergangene Events, an denen sie teilgenommen hat und zukünftige Events, für die sie registriert ist, bleibt erhalten.`
      + ` Um erneut Zugriff zu erhalten, muss sie erneut zur Organisation eingeladen werden.`
    );
  }
  if (!ok) {
    return;
  }
  customLoading.value = true;
  try {
    await organizationsApi.removeMember(organizationUuid, removeAccount.uuid);
    await organizationStore.hydrateSpecific(organizationUuid);
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
