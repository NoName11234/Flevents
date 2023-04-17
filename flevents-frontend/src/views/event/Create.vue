<template>
  <Heading text="Event erstellen" />
  <EventForm
    v-if="presetLoaded"
    :back-route="'home.manage'"
    :submit-route="'home.manage'"
    :preset-event="preset"
  />
</template>

<script setup lang="ts">
import EventForm from "@/components/EventForm.vue";
import Heading from "@/components/Heading.vue";
import {useRoute} from "vue-router";
import {onBeforeMount, ref} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import axios from "axios";

const route = useRoute();
const presetId = route.query.preset || null;
let preset = ref(undefined as FleventsEvent|undefined);
const presetLoaded = ref(false);

async function setup() {
  if (presetId !== null) {
    try {
      const response = await axios.get(`http://localhost:8082/api/events/${presetId}`);
      preset.value = response.data as FleventsEvent;
    } catch (e) {
      console.warn(`Could not load event with uuid ${presetId} as preset.`, e);
    }
  }
  presetLoaded.value = true;
}

onBeforeMount(setup);

</script>

<style scoped>

</style>
