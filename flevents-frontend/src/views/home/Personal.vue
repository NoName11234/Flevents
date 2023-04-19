<template>
  <Heading
    text="Mein Bereich"
    subtext="Behalten Sie alle Events, an denen Sie teilnehmen oder teilgenommen haben, im Blick."
  />
  <EventList
    :loading="loading"
    :error="error"
    :events="bookedEvents"
  />
</template>

<script lang="ts" setup>
import EventList from "@/layouts/home/EventList.vue";
import Heading from "@/components/Heading.vue";
import {useEventStore} from "@/store/events";
import {storeToRefs} from "pinia";

const eventStore = useEventStore();
const { bookedEvents, loading, error } = storeToRefs(eventStore);

eventStore.hydrate();

</script>
