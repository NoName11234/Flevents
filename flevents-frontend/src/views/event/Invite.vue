<script setup lang="ts">

import {onBeforeMount, Ref, ref} from "vue";
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
import {load} from "webfontloader";
const route = useRoute()
const router = useRouter();

const eventUuid = route.params.uuid as string;
const address = ref("");
const chips =  ref(new Array<any>());
const tooltip = ref('');
const loading = ref(false);
const role = ref(EventRole.attendee) as Ref<EventRole.attendee|EventRole.tutor>;

const appStore = useAppStore();

const eventStore = useEventStore();
const event = eventStore.getEventGetter(eventUuid);

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'attendees' } };

const selectableRoles = [
  EventRole.attendee,
  EventRole.tutor,
];

function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

// submit
async function submit() {
  loading.value = true;
  let failedInvitations = [];
  let successfulInvitations = [];
  for (let i in chips.value) {
    let email = chips.value[i];
    if (!email.match(VALIDATION.EMAIL)) {
      failedInvitations.push(email);
      continue;
    }
    try {
      const response = await eventApi.inviteAttendee(eventUuid, email, role.value);
      successfulInvitations.push(email);
    } catch (e) {
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
  loading.value = true;
  await router.push(backRoute);
}
</script>

<template>

  <Heading :text="`Zu ${event.name} einladen`" />

  <v-card :loading="loading" :disabled="loading">
    <v-form validate-on="submit" @submit.prevent="submit()">
      <v-container class="d-flex flex-column gap-3">
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
          menu-icon="mdi-chevron-down"
        />
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
