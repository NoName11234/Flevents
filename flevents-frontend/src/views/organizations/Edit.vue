<template>
  <Heading text="Organisationsdetails bearbeiten" />
  <OrganizationForm
    :back-route="{ name: 'organizations.organization', params: { uuid: $route.params.uuid } }"
    :submit-route="{ name: 'organizations.organization', params: { uuid: $route.params.uuid } }"
  />
</template>

<script setup lang="ts">
import OrganizationForm from "@/components/OrganizationForm.vue";
import Heading from "@/components/Heading.vue";
</script>

<style scoped>

</style>
