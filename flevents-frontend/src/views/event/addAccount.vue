<script setup lang="ts">

import {onBeforeMount, onUpdated, Ref, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useRoute, useRouter} from 'vue-router';
import axios from "axios"
import Heading from "@/components/Heading.vue";
const route = useRoute()
const router = useRouter();
const adress = ref("");
const chips =  ref(new Array<any>());
const tooltip = ref('');
const event = ref({} as FleventsEvent)


function remove(item: any){
  chips.value.splice(chips.value.indexOf(item), 1)
}

// submit
async function submit() {
  try {
    await axios.post("http://h3005487.stratoserver.net:8082/api/mail/sendMail", { to: chips.value, cc: {}, bcc: {}, subject: `Anmeldung zu ${event.value.name} - TEST`, msgBody: `Sie wurden zu ${event.value.name} angemeldet.`, attachment: ""});
    await router.push( {name: 'eventInfo', params: { uuid: event.value.uuid }} );
  } catch (e) {
    tooltip.value = "Das Event konnte nicht erstellt werden.";
  }
}

onBeforeMount(async () => {
  adress.value = route.params.id as string;
  const response = await axios.get(`http://h3005487.stratoserver.net:8082/api/events/${adress.value}`);
  console.log(response);
  response.status == 200 ? event.value = response.data : event.value = {} as FleventsEvent;
})
</script>

<template>

    <v-card>
      <v-container>
      <Heading :text="`${event.name} - Teilnehmer hinzufügen`"></Heading>
      </v-container>
      <v-form class="marignt" validate-on="submit" @submit.prevent="submit">
        <v-container>
          <v-combobox
            v-model="chips"
            chips
            clearable
            label="Eingeladene E-Mail-Adressen"
            closable-chips
            multiple
            prepend-inner-icon="mdi-account"
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
      </v-container>
        <v-divider />
        <v-container class="d-flex flex-column flex-sm-row justify-end gap">
          <v-btn
            variant="outlined"
            :to="{ name: 'events' }"
          >
            Verwerfen
          </v-btn>
          <v-btn
            type="submit"
            color="primary"
          >
            Teilnehmer hinzufügen
          </v-btn>
        </v-container>
      </v-form>
    </v-card>

</template>

<style scoped>
.marignt{
  margin-top: -3rem;
}
</style>
