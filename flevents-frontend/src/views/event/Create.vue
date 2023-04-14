<template>
  <Heading text="Event erstellen" />
  <EventForm
    :back-route="'home.manage'"
    :submit-route="'home.manage'"
  />
</template>

<script lang="ts">
import EventForm from "@/components/EventForm.vue";
import Heading from "@/components/Heading.vue";
export default {
  name: "CreateAccount",
  components: {Heading, EventForm}
}
</script>

<style scoped>

</style>
