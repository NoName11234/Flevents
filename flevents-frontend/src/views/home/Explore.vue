<template>
  <Heading
    text="Entdecken"
    subtext="Durchstöbern Sie das Event-Angebot aller Organisationen, denen Sie angehören!"
  />
  <EventList
    :loading="loading"
    :error="error"
    :events="exploreEvents"
  />
</template>

<script lang="ts" setup>
import EventList from "@/layouts/home/EventList.vue";
import Heading from "@/components/Heading.vue";
import {useEventStore} from "@/store/events";
import {storeToRefs} from "pinia";

const eventStore = useEventStore();
const { exploreEvents, loading, error } = storeToRefs(eventStore);

eventStore.hydrate();

</script>
