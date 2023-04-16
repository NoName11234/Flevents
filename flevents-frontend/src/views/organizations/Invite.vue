<script setup lang="ts">

import {onBeforeMount, ref} from "vue";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import Heading from "@/components/Heading.vue";
import {Organization} from "@/models/organization";
import {OrganizationRole} from "@/models/organizationRole";
const route = useRoute()
const router = useRouter();
const address = ref("");
const chips =  ref(new Array<any>());
const tooltip = ref('');
const role = ref(OrganizationRole.member);
// TODO: obtain from database
const organization = ref({
  uuid: route.params.uuid ?? '0',
  name: 'Team Flyndre',
} as Organization);


function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

// submit
async function submit_old() {
  try {
    console.log(chips.value);

    const tos = [chips.value.pop()];

    while(chips.value.length != 0){
      tos.push(chips.value.pop());
    }

    const mailinfo = { to: tos, cc: [], bcc: [], subject: `Anmeldung zu ${organization.value.name} - TEST`, msgBody: `Sie wurden zu ${organization.value.name} angemeldet.`, attachment: ""};
    console.log(mailinfo);
    await axios.post("http://localhost:8082/api/mail/sendMail", mailinfo);
    await router.push( {name: 'organizations.organization', params: { uuid: organization.value.uuid }} );
  } catch (e) {
    tooltip.value = "Die Teilnehmer konnten nicht eingeladen werden.";
  }
}
async function submit() {
  try {
    console.log(chips.value);
    for (let i in chips.value) {
      await axios.post(`http://localhost:8082/api/organizations/${address.value}/invite`, {}, {
        params: {
          email: chips.value[i],
          role: role.value
        }
      });
    }
    await router.push( {name: 'organizations.organization', params: { uuid: organization.value.uuid }} );
  } catch (e) {
    tooltip.value = "Einer oder mehrere Mitgleider konnten nicht eingeladen werden.";
    console.error("Invitation failed", e);
  }
}

onBeforeMount(async () => {
  address.value = route.params.uuid as string;
  try {
    const response = await axios.get(`http://localhost:8082/api/organizations/${address.value}`);
    console.log(response);
    response.status == 200 ? organization.value = response.data : organization.value = {} as Organization;
  } catch (e) {
    console.error("Failed to load organization data", e);
  }
})
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
          :items="[
            OrganizationRole.admin,
            OrganizationRole.organizer,
            OrganizationRole.member,
          ]"
          v-model="role"
        />
      </v-container>
      <v-divider />
      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="flat"
          :to="{ name: 'organizations.organization', params: { uuid: organization.uuid } }"
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
