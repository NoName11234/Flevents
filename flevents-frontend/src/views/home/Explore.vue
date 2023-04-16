<template>
  <Heading
    text="Entdecken"
    subtext="Durchstöbern Sie das Event-Angebot aller Organisationen, denen Sie angehören!"
  />
  <!-- TODO: Filtermöglichkeiten (Organisation, Suche, Datumsauswahl, Sortierung), Liste aller verfügbaren Events (die ersten 20 oder so) -->
  <CardBanner
    v-show="error"
    message="Error: Es konnten keine Events abgerufen werden."
  />
  <ContentLoadingIndicator
    :loading="loading"
    :text="'Lade Events...'"
  />
  <Suspense v-if="events.length !== 0">
    <EventList :events="events" />
  </Suspense>
</template>

<script lang="ts" setup>
import axios from "axios";
import EventList from "@/layouts/home/EventList.vue";
import {Ref, ref} from "vue";
import CardBanner from "@/components/CardBanner.vue";
import Heading from "@/components/Heading.vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import ContentLoadingIndicator from "@/components/ContentLoadingIndicator.vue";
import Security, {getAccount} from "@/service/security";

const events: Ref<FleventsEvent[]> = ref([]);
const error = ref(false);
const loading = ref(true);

// TODO: API-call durch pinia store ersetzen
axios.get(`http://h3005487.stratoserver.net:8082/api/accounts/${Security.getAccount().uuid}/explore-events`)
  .then((response) => events.value = response.data)
  .catch((e) => {
    console.log(e);
    error.value = true;
  }).finally(() => loading.value = false);

</script>
