<template>
  <Heading
    text="Verwalten"
    subtext="Verwalten Sie Events und Organisationen in Ihrer Verantwortung."
    icon="mdi-pencil"
  />
  <div class="d-flex flex-column flex-sm-row align-stretch align-sm-center justify-start flex-fill gap">
    <v-btn
      v-if="showCreateButton"
      :to="{ name: 'events.create' }"
      prepend-icon="mdi-calendar-plus"
      variant="tonal"
      color="primary"
    >
      Neues Event
    </v-btn>
    <v-btn
      v-if="managedOrganizations.length > 0"
      prepend-icon="mdi-account-group"
      append-icon="mdi-chevron-down"
      variant="text"
    >
      Organisationsverwaltung

      <v-menu activator="parent">
        <v-list>
          <v-list-item
            v-for="(item, index) in managedOrganizations"
            :key="index"
            :value="index"
            :title="item.name"
            :to="{ name: 'organizations.organization', params: { uuid: item.uuid } }"
            :prepend-avatar="item.icon"
          />
        </v-list>
      </v-menu>
    </v-btn>
  </div>
  <EventList
    :loading="loading"
    :error="error"
    :events="managedEvents"
    :show-manage-tools="true"
  />

</template>

<script setup lang="ts">
import axios from "axios";
import EventList from "@/layouts/home/EventList.vue";
import {computed, onMounted, ref} from "vue";
import Heading from "@/components/Heading.vue";
import {Organization} from "@/models/organization";
import security from "@/service/security";
import {Account} from "@/models/account";
import {OrganizationRole} from "@/models/organizationRole";
import {useEventStore} from "@/store/events";
import {storeToRefs} from "pinia";

const managedOrganizations = ref([] as Organization[]);

const eventStore = useEventStore();
const { managedEvents, loading, error } = storeToRefs(eventStore);

eventStore.hydrate();

const account = security.getAccount() as Account;
const showCreateButton = computed( () => {return validateManaged()});

function validateManaged(){
  for(let i = 0; i < account.organizationPreviews.length; i++){
    if(account.organizationPreviews[i].role == OrganizationRole.admin || account.organizationPreviews[i].role == OrganizationRole.organizer){
      return true;
    }
  }
  return false;
}

// TODO: Replace with pinia store
onMounted(async () => {
  try {
    let resp = await axios.get(`http://localhost:8082/api/accounts/${account.uuid}/managed-organizations`);
    managedOrganizations.value = resp.data;
  } catch (e) {
    console.error("Failed to fetch managed organizations.");
  }
})

</script>

<style scoped>

</style>
