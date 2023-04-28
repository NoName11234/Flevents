<template>
  <Heading text="Organisationsdetails bearbeiten" />
  <OrganizationForm
    :back-route="{ name: 'organizations.organization', params: { uuid: $route.params.uuid }, query: { tab: 'info' } }"
    :submit-route="{ name: 'organizations.organization', params: { uuid: $route.params.uuid }, query: { tab: 'info' } }"
  />
</template>

<script setup lang="ts">
import OrganizationForm from "@/components/OrganizationForm.vue";
import Heading from "@/components/Heading.vue";
</script>

<style scoped>

</style>
