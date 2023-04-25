<template>
  <Heading text="Event erstellen" />
  <EventForm
    :back-route="{ name: 'home.manage' }"
    :submit-route="{ name: 'home.manage' }"
    :organizations="managedOrganizations"
    :preset-event="preset"
  />
</template>

<script setup lang="ts">
import EventForm from "@/components/EventForm.vue";
import Heading from "@/components/Heading.vue";
import {useRoute} from "vue-router";
import {computed} from "vue";
import {useEventStore} from "@/store/events";
import {useOrganizationStore} from "@/store/organizations";
import {storeToRefs} from "pinia";
import {FleventsEvent} from "@/models/fleventsEvent";

const route = useRoute();
const eventStore = useEventStore();
const organizationStore = useOrganizationStore();
const { managedOrganizations } = storeToRefs(organizationStore);

let preset = undefined as any;
const presetId = route.query.preset as string;
if (presetId) preset = eventStore.getEventGetter(presetId);

</script>

<style scoped>

</style>
