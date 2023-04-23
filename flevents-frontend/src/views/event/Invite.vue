<script setup lang="ts">

import {onBeforeMount, Ref, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import Heading from "@/components/Heading.vue";
import {EventRole} from "@/models/eventRole";
import {useEventStore} from "@/store/events";
import api from "@/api/api";
import eventApi from "@/api/eventApi";
import {useAppStore} from "@/store/app";
const route = useRoute()
const router = useRouter();

const uuid = route.params.uuid as string;
const address = ref("");
const chips =  ref(new Array<any>());
const tooltip = ref('');
const role = ref(EventRole.attendee) as Ref<EventRole.attendee|EventRole.tutor>;

const appStore = useAppStore();

const eventStore = useEventStore();
const event = eventStore.getEventGetter(uuid);

const selectableRoles = [
  EventRole.attendee,
  EventRole.tutor,
];

function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

// submit
async function submit() {
  let failedInvitations = [];
  for (let i in chips.value) {
    let email = chips.value[i];
    try {
      const response = await eventApi.inviteAttendee(uuid, email, role.value);
    } catch (e) {
      console.log(`Invitation of ${email} failed.`);
      failedInvitations.push(email);
    }
  }
  appStore.addToast(`Das Einladen folgender E-Mail-Adressen ist gescheitert: ${failedInvitations.join(', ')}`)
  await router.push( { name: 'events.event', params: { uuid: uuid } } );
}
</script>

<template>

  <Heading :text="`Zu ${event.name} einladen`" />

  <v-card>
    <v-form validate-on="submit" @submit.prevent="submit()">
      <v-container>
        <v-combobox
          v-model="chips"
          chips
          clearable
          label="Eingeladene E-Mail-Adressen"
          closable-chips
          multiple
          prepend-inner-icon="mdi-account-multiple"
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
          :to="{ name: 'events.event', params: { uuid: uuid } }"
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
