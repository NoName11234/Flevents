<template>
  <Heading text="Account anlegen" />
  <CardBanner message="Achtung! Hierbei handelt es sich um eine Entwicklungsversion. Eingegebene Daten werden noch nicht verschlüsselt. Geben Sie deshalb keine sensiblen Daten (Passwörter, E-Mail) ein, wenn sie diese geheimhalten wollen." />
  <AccountForm  :back-route="backRoute" :submit-route="backRoute"/>
</template>

<script setup lang="ts">
import AccountForm from "@/components/AccountForm.vue";
import Heading from "@/components/Heading.vue";
import CardBanner from "@/components/CardBanner.vue";
import {RouteLocationRaw, useRoute} from "vue-router";

const route = useRoute();
const backRoute = {
  name: 'accounts.login',
  query: route.query
} as RouteLocationRaw;
</script>

<style scoped>

</style>
