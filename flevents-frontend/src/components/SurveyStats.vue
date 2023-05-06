<script setup lang="ts">
import DatetimeService from "@/service/datetimeService";
import {Questionnaire} from "@/models/questionnaire";
import {Statistics} from "@/models/statistics";

const props = defineProps({
  questionnaire: {
    required: true,
    type: Object as () => Questionnaire,
  },
  statistics: {
    required: false,
    type: Object as () => Statistics,
  }
});

</script>

<template>
  <v-list>
    <v-list-item
      prepend-icon="mdi-star-four-points-outline"
      subtitle="Erstelldatum"
    >
      {{ DatetimeService.getDateTime(new Date(questionnaire.creationDate)) }}
    </v-list-item>
    <v-list-item
      prepend-icon="mdi-timer-sand-complete"
      subtitle="Einsendeschluss"
    >
      {{ DatetimeService.getDateTime(new Date(questionnaire.closingDate)) }}
    </v-list-item>
    <v-list-item
      v-if="statistics"
      prepend-icon="mdi-file-document-multiple-outline"
      subtitle="Anzahl der Einsendungen"
    >
      {{ statistics.userCount }}
    </v-list-item>
  </v-list>
</template>

<style scoped>

</style>
