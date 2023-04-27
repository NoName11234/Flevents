<template>
  <Heading text="Error 404" />
  <v-card>
    <v-card-text>
      Die von Ihnen angefragte Seite "{{ route.fullPath }}" konnte leider nicht gefunden werden.
    </v-card-text>
    <v-card-actions>
      <v-btn
        color="primary"
        :to="{ name: 'home' }"
      >
        Zur Startseite
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {useRoute} from "vue-router";

const route = useRoute();
</script>

<style scoped>

</style>
