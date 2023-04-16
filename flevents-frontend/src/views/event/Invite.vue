<script setup lang="ts">

import {onBeforeMount, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import Heading from "@/components/Heading.vue";
import {EventRole} from "@/models/eventRole";
const route = useRoute()
const router = useRouter();
const address = ref("");
const chips =  ref(new Array<any>());
const tooltip = ref('');
const role = ref(EventRole.attendee);
// TODO: obtain from backend
const event = ref({
  uuid: route.params.uuid as string,
  name: 'Beispielevent',
  description: 'Beispielbeschreibung'
} as FleventsEvent)


function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

// submit
async function submit() {
  try {
    console.log(chips.value);
    for (let i in chips.value) {
      await axios.post(`http://localhost:8082/api/events/${address.value}/invite`, {}, {
        params: {
          email: chips.value[i],
          role: role.value
        }
      });
    }
    await router.push( {name: 'events.event', params: { uuid: event.value.uuid }} );
  } catch (e) {
    tooltip.value = "Einer oder mehrere Teilnehmer konnten nicht eingeladen werden.";
    console.error("Invitation failed", e);
  }
}

onBeforeMount(async () => {
  address.value = route.params.uuid as string;
  try {
    const response = await axios.get(`http://localhost:8082/api/events/${address.value}`);
    console.log(response);
    response.status == 200 ? event.value = response.data : event.value = {} as FleventsEvent;
  } catch (e) {
    console.error("Failed to load event data", e);
  }
})
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
          :items="[
            EventRole.attendee,
            EventRole.tutor,
          ]"
          v-model="role"
        />
      </v-container>

      <v-divider />

      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="flat"
          :to="{ name: 'events.event', params: { uuid: event.uuid } }"
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
