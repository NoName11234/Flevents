<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import Heading from "@/components/Heading.vue";
import {useSurveyStore} from "@/store/surveys";
import DatetimeService from "@/service/datetimeService";
import {useSurveyStatisticsStore} from "@/store/surveyStatistics";
import {computed, ref} from "vue";
import {useAppStore} from "@/store/app";

const route = useRoute();
const router = useRouter();
const eventUuid = route.params.uuid as string;
const questionnaireUuid = route.params.questionnaireUuid as string;

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'polls' } };

const appStore = useAppStore();

const surveyStore = useSurveyStore();
const questionnaire = surveyStore.getQuestionnaireGetter(questionnaireUuid);
const questionnaireLoading = computed(() => surveyStore.specificLoading.get(questionnaireUuid));

const statisticsStore = useSurveyStatisticsStore();
const statistics = statisticsStore.getStatisticsGetterOf(questionnaireUuid);
const statisticsLoading = computed(() => statisticsStore.specificLoading.get(questionnaireUuid));



// Functionality for live-updating

const liveUpdates = ref(false);
const liveUpdateInterval = 10000;
const liveUpdateHandlerFunction = async () => statisticsStore.hydrateSpecific(questionnaireUuid);
let liveUpdateHandler: NodeJS.Timeout;

updateLiveUpdates();

function updateLiveUpdates() {
  if (liveUpdates.value !== true) {
    clearInterval(liveUpdateHandler);
    return;
  }
  liveUpdateHandlerFunction();
  liveUpdateHandler = setInterval(liveUpdateHandlerFunction, liveUpdateInterval);
}

</script>

<template>

  <Heading :text="questionnaire.title" />

  <v-card
    elevation="0"
    border
    class="border-dashed"
  >
    <v-list>
      <v-list-item
        prepend-icon="mdi-timer-sand-complete"
        subtitle="Einsendeschluss"
      >
        {{ DatetimeService.getDateTime(new Date(questionnaire.closingDate)) }}
      </v-list-item>
      <v-list-item
        prepend-icon="mdi-file-document-multiple-outline"
        subtitle="Anzahl der Einsendungen"
      >
        {{ statistics.value }}
      </v-list-item>
    </v-list>
  </v-card>

  <v-container class="d-flex flex-row justify-end align-center gap">
    <v-switch
      label="Live"
      hide-details="auto"
      density="compact"
      v-model="liveUpdates"
      @change="updateLiveUpdates"
      color="primary"
      inset
    />
    <v-spacer />
    <v-progress-circular
      indeterminate
      v-show="statisticsLoading"
      color="primary"
      size="small"
    />
  </v-container>

  {{ statistics }}

  <v-card>
    Henlo
  </v-card>

  <v-card
    v-for="(question, index) in questionnaire.questions"
    :key="index"
  >
    <v-container>
      <strong>
        {{ `Frage ${index + 1}: ${question.question}` }}
      </strong>
    </v-container>

    <v-divider />

    <v-textarea
      v-if="question.choices.length == 0"
      hide-details="auto"
      label="Eigene Antwort"
      variant="solo"
    >
    </v-textarea>

    <v-radio-group
      v-if="question.choices.length != 0"
      hide-details="auto"
      class="pa-2"
    >
      <v-radio
        v-for="(option, oIndex) in question.choices"
        :key="oIndex"
        :value="option"
        :label="option.choice"
      />
    </v-radio-group>

  </v-card>

</template>

<style scoped>

</style>
