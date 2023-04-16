<template>
  <Heading
    text="Mein Bereich"
    subtext="Behalten Sie alle Events, an denen Sie teilnehmen oder teilgenommen haben, im Blick."
  />
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
import {onMounted, Ref, ref} from "vue";
import CardBanner from "@/components/CardBanner.vue";
import Heading from "@/components/Heading.vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import ContentLoadingIndicator from "@/components/ContentLoadingIndicator.vue";
import security from "@/service/security";
import {Account} from "@/models/account";

const events: Ref<FleventsEvent[]> = ref([]);
const error = ref(false);
const loading = ref(true);
const account = security.getAccount() as Account;

// TODO: API-call durch pinia store ersetzen


onMounted(async () => {
  // console.log(JSON.parse(document.cookie.split(";")[0].split("=")[1]).uuid);
  // const response = await axios.get(`http://h3005487.stratoserver.net:8082/api/accounts/${JSON.parse(document.cookie.split(";")[0].split("=")[1]).uuid}/booked-events`)
  const response = await axios.get(`http://localhost:8082/api/accounts/${account.uuid}/booked-events`)
  console.log(response);
  response.status == 200 ? events.value = response.data : error.value = true;
  response.status == 200 ? loading.value = false : -1;
})

</script>
