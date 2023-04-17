<template>
  <Heading
    :text="organization.name"
    :logo-src="organization.icon"
  />

  <v-card>
    <v-tabs
      v-model="tab"
      class="bg-primary"
    >
      <v-tab value="info">
        Informationen
      </v-tab>
      <v-tab value="mails">
        E-Mail-Vorlagen
      </v-tab>
      <v-tab value="members">
        Mitglieder
      </v-tab>
    </v-tabs>

    <v-window v-model="tab">
      <v-window-item value="info">
        <v-container>
          {{ organization.description }}
        </v-container>
        <v-divider></v-divider>
        <v-list>
          <v-list-item
            v-if="organization.phoneContact"
            prepend-icon="mdi-phone"
          >
            {{ organization.phoneContact }}
          </v-list-item>
          <v-list-item
            v-if="organization.address"
            prepend-icon="mdi-map-marker"
          >
            {{ organization.address }}
          </v-list-item>
        </v-list>
        <v-divider/>
        <v-container
          v-if="validateRole === OrganizationRole.admin"
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
          <MailConfigCard
            v-for="(c, i) in [
              {
                name: 'Einladungs-E-Mails',
                text: 'Herzlich willkommen\nHeude dies das',
              },
            ]"
            :key="i"
            :config="c"
          />
        </v-expansion-panels>
      </v-window-item>

      <v-window-item value="members">
        <v-container
          v-if="validateRole === OrganizationRole.admin"
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
        fixed-header>
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
              <th v-if="validateRole === OrganizationRole.admin">
                Entfernen
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
             v-for="(item, index) in organization.accountPreviews"
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
                      organization.accountPreviews.filter(a => a.role === OrganizationRole.admin).length <= 1
                      && item.role === OrganizationRole.admin
                    )
                    || validateRole != OrganizationRole.admin
                  "
                />
              </td>
              <td v-if="validateRole === OrganizationRole.admin">
                <v-btn
                  variant="text"
                  size="small"
                  icon="mdi-delete"
                  @click="removeAccount(item.uuid, item.role)"
                  :disabled="
                    organization.accountPreviews.filter(a => a.role === OrganizationRole.admin).length <= 1
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
import {Organization} from "@/models/organization";
import {OrganizationRole} from "@/models/organizationRole";
import axios from "axios";
import {AccountPreview} from "@/models/accountPreview";
import security from "@/service/security";
import MailConfigCard from "@/components/MailConfigCard.vue";

const tab = ref(null);
const account = security.getAccount()!;
const showRegisterEditDialog = ref(false);
const route = useRoute();
const organization = ref({
  uuid: route.params.uuid,
} as Organization);

onMounted(async () => {
  try {
    let response = await axios.get(`http://localhost:8082/api/organizations/${route.params?.uuid}`);
    organization.value = response.data;
  } catch (e) {
    console.error("Failed to load organization.");
  }
});

const validateRole = computed(() => {
  return organization.value?.accountPreviews?.find(a => a.uuid === account.uuid)?.role || OrganizationRole.member;
})

async function updateRole(account: AccountPreview) {
  const prevRole = security.getAccount()!
    .organizationPreviews
    .find(o => o.uuid === organization.value.uuid)!
    .role as OrganizationRole;
  if (account.uuid === security.getAccount().uuid) {
    const ok = window.confirm(`Sind Sie sicher, dass Sie Ihre eigene Rolle zu "${account.role}" ändern möchten`);
    if (!ok) {
      account.role = prevRole;
      return;
    }
  }
  console.log("changing role to: ", account.role);
  try {
    await axios.post(`http://localhost:8082/api/organizations/${organization.value.uuid}/change-role/${account.uuid}?role=${account.role}`)
  } catch (e) {
    console.log("Failed to update role.", e);
  }
}

async function removeAccount(uuid : string, role : string){
  try {
    await axios.post(`http://localhost:8082/api/organizations/${organization.value.uuid}/remove-account/${uuid}`)
    for (let i = 0; i < organization.value.accountPreviews.length; i++){
      if(organization.value.accountPreviews[i].uuid === uuid){
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
