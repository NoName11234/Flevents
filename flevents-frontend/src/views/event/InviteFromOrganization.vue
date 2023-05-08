<script setup lang="ts">

import {onBeforeMount, onMounted, Ref, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import Heading from "@/components/Heading.vue";
import {EventRole} from "@/models/eventRole";
import {useEventStore} from "@/store/events";
import api from "@/api/api";
import eventApi from "@/api/eventsApi";
import {useAppStore} from "@/store/app";
import {VALIDATION} from "@/constants";
import {useOrganizationStore} from "@/store/organizations";
import {AccountPreview} from "@/models/accountPreview";
import ColorService from "@/service/colorService";
import {useAccountStore} from "@/store/account";
const route = useRoute()
const router = useRouter();

const eventUuid = route.params.uuid as string;
const address = ref("");
const tooltip = ref('');
const loading = ref(true);
const appStore = useAppStore();
const items = ref([] as AccountPreview[]);
const select  = ref([] as AccountPreview[])
const eventStore = useEventStore();
const orgaStore = useOrganizationStore();
const accountStore = useAccountStore();

const event = eventStore.getEventGetter(eventUuid);
const organization = orgaStore.getOrganizationGetter(event.value.organizationPreview.uuid);
const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'attendees' } };

onBeforeMount(() => {
  organization.value.accountPreviews.forEach(obj => {
      for(const user of event.value.accountPreviews){
        if(obj.uuid === user.uuid && user.role !== EventRole.organizer){
          return;
        }
      }
      items.value.push(obj);
  })
  loading.value = false;
})

// submit
async function submit() {
  let failedInvitations = [];
  let successfulInvitations = [];
  for (const obj of select.value) {
    try {
      const response = await eventApi.bookAccount(eventUuid, obj.uuid);
      successfulInvitations.push(obj.email);
    } catch (e) {
      failedInvitations.push(obj.email);
    }
  }
  if (failedInvitations.length > 0) {
    appStore.addToast({
      text: `Das Hinzufügen folgender Nutzer war nicht erfolgreich: ${failedInvitations.join(', ')}`,
      color: 'error',
    });
  }
  if (successfulInvitations.length > 0) {
    appStore.addToast({
      text: `Das Hinzufügen folgender Nutzer war erfolgreich: ${successfulInvitations.join(', ')}`,
      color: 'success',
    });
  }
  eventStore.hydrateSpecific(eventUuid);
  await router.push(backRoute);
}
</script>

<template>

  <Heading :text="`Zu ${event.name} einladen`" />

  <v-card v-if="!loading">
    <v-form validate-on="submit" @submit.prevent="submit()">
      <v-container class="d-flex flex-column gap-3">
        <v-combobox
          v-model="select"
          :items="items"
          :item-title="item => `${item.firstname} ${item.lastname} (${item.email})`"
          label="Mitglieder der Organisation"
          messages="Rollen können nach dem Hinzufügen in der Teilnehmerübersicht zugewiesen werden."
          menu-icon="mdi-chevron-down"
          hide-details="auto"
          return-object
          multiple
        >
          <template v-slot:selection="data">
            <v-chip
              :key="JSON.stringify(data.item)"
              v-bind="data.attrs"
              :model-value="data.selected"
              :disabled="data.disabled"
              size="small"
              @click:close="data.parent.selectItem(data.item)"
            >
              {{ data.item.title }}
            </v-chip>
          </template>
        </v-combobox>
      </v-container>

      <v-divider />

      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="flat"
          :to="backRoute"
        >
          Verwerfen
        </v-btn>
        <v-btn
          type="submit"
          color="primary"
          prepend-icon="mdi-account-plus"
        >
          Hinzufügen
        </v-btn>
      </v-container>

    </v-form>
  </v-card>

</template>

<style scoped>

</style>
