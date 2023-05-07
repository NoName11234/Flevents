<template>
  <Heading text="Account anlegen" />
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
