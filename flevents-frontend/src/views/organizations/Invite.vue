<script setup lang="ts">

import {onBeforeMount, Ref, ref} from "vue";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import Heading from "@/components/Heading.vue";
import {Organization} from "@/models/organization";
import {OrganizationRole} from "@/models/organizationRole";
import {useOrganizationStore} from "@/store/organizations";
import {useAppStore} from "@/store/app";
import organizationsApi from "@/api/organizationsApi";
import {VALIDATION} from "@/constants";
const route = useRoute()
const router = useRouter();

const uuid = route.params.uuid as string;
const address = ref("");
const chips =  ref(new Array<any>());
const tooltip = ref('');
const role = ref(OrganizationRole.member) as Ref<OrganizationRole>;

const appStore = useAppStore();

const organizationStore = useOrganizationStore();
const organization = organizationStore.getOrganizationGetter(uuid);

const selectableRoles = [
  OrganizationRole.admin,
  OrganizationRole.organizer,
  OrganizationRole.member,
];

function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

async function submit() {
  let failedInvitations = [];
  let successfulInvitations = [];
  for (let i in chips.value) {
    let email = chips.value[i];
    if (!email.match(VALIDATION.EMAIL)) {
      failedInvitations.push(email);
      continue;
    }
    try {
      const response = await organizationsApi.invite(uuid, email, role.value);
      successfulInvitations.push(email);
    } catch (e) {
      console.log(`Invitation of ${email} failed.`);
      failedInvitations.push(email);
    }
  }
  if (failedInvitations.length > 0) {
    appStore.addToast({
      text: `Das Einladen folgender E-Mail-Adressen ist gescheitert: ${failedInvitations.join(', ')}`,
      color: 'error',
    });
  }
  if (successfulInvitations.length > 0) {
    appStore.addToast({
      text: `Das Einladen folgender E-Mail-Adressen war erfolgreich: ${successfulInvitations.join(', ')}`,
      color: 'success',
    });
  }
  await router.push( { name: 'organizations.organization', params: { uuid: uuid } } );
}
</script>

<template>

  <Heading :text="`Zu ${organization.name} einladen`" />

  <v-card>
    <v-form validate-on="submit" @submit.prevent="submit()">

      <v-container class="d-flex flex-column gap-3" >
        <v-combobox
          v-model="chips"
          chips
          clearable
          label="Eingeladene E-Mail-Adressen"
          closable-chips
          multiple
          prepend-inner-icon="mdi-account-multiple"
          hide-details="auto"
        >
          <template v-slot:selection="{ attrs, select, selected }">
            <v-chip
              v-bind="attrs"
              :model-value="selected"
              closable
              @click="select"
              @click:close="remove"
            >
              <span>(interest)</span>
            </v-chip>
          </template>
        </v-combobox>
        <v-select
          label="Zugewiesene Rolle"
          hide-details="auto"
          :items="selectableRoles"
          v-model="role"
        />
      </v-container>
      <v-divider />
      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="flat"
          :to="{ name: 'organizations.organization', params: { uuid: uuid } }"
        >
          Verwerfen
        </v-btn>
        <v-btn
          type="submit"
          color="primary"
          prepend-icon="mdi-email-fast"
        >
          Einladen
        </v-btn>
      </v-container>

    </v-form>
  </v-card>

</template>

<style scoped>

</style>
