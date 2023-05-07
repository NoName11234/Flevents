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
import EventList from "@/layouts/home/EventList.vue";
import {computed} from "vue";
import Heading from "@/components/Heading.vue";
import {OrganizationRole} from "@/models/organizationRole";
import {useEventStore} from "@/store/events";
import {storeToRefs} from "pinia";
import {useOrganizationStore} from "@/store/organizations";
import {useAccountStore} from "@/store/account";

const eventStore = useEventStore();
const { managedEvents, loading, error } = storeToRefs(eventStore);
eventStore.requestHydration();

const organizationStore = useOrganizationStore();
const { managedOrganizations } = storeToRefs(organizationStore);
organizationStore.requestHydration();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const showCreateButton = computed( () => {return validateManaged()});

function validateManaged() {
  if (account.value === null) return false;
  for (let i = 0; i < account.value.organizationPreviews.length; i++) {
    if (
      account.value.organizationPreviews[i].role == OrganizationRole.admin
      || account.value.organizationPreviews[i].role == OrganizationRole.organizer
    ) {
      return true;
    }
  }
  return false;
}

</script>

<style scoped>

</style>
