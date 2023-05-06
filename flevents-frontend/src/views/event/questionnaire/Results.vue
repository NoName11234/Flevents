<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import Heading from "@/components/Heading.vue";
import {useSurveyStore} from "@/store/surveys";
import DatetimeService from "@/service/datetimeService";
import {useSurveyStatisticsStore} from "@/store/surveyStatistics";
import {computed, ref} from "vue";
import {useAppStore} from "@/store/app";
import {Bar, Pie} from "vue-chartjs";
import {Chart, BarElement, CategoryScale, Legend, LinearScale, Title, Tooltip, ArcElement} from "chart.js";
import StatisticsService from "@/service/statisticsService";
import {SingleChoiceQuestionSummary} from "@/models/singleChoiceQuestionSummary";
import {SingleChoiceQuestion} from "@/models/singleChoiceQuestion";
import {FreeTextQuestionSummary} from "@/models/freeTextQuestionSummary";
import {Statistics} from "@/models/statistics";

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
// const statistics = ref({
//   questionSummaries: [
//     {
//       userCount: 20,
//       votes: [5, 7, 3, 1]
//     } as SingleChoiceQuestionSummary,
//     {
//       answers: ['Moin', 'Test']
//     } as FreeTextQuestionSummary,
//   ]
// } as Statistics);
const statisticsLoading = computed(() => statisticsStore.specificLoading.get(questionnaireUuid));



// Functionality for live-updating

const liveUpdates = ref(false);
const liveUpdateInterval = 2500;
const liveUpdateHandlerFunction = async () => {
  statisticsStore.hydrateSpecific(questionnaireUuid);
  // let votes = (statistics.value.questionSummaries[0] as SingleChoiceQuestionSummary).votes;
  // votes[Math.floor(Math.random() * votes.length)] += Math.floor(Math.random() * 3);
};
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

const chartOptions = {
  responsive: true
};

Chart.register(Title, Tooltip, BarElement, CategoryScale, LinearScale, ArcElement);

</script>

<template>

  <Heading :text="questionnaire.title ?? ''" />

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
        {{ 32 }}
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

    <template v-if="(statistics as Statistics).questionSummaries?.length > 0">
      <!-- Free Text Question -->
      <v-list
        v-if="question.choices.length == 0"
        density="compact"
      >
        <v-list-item
          v-for="(answer, aIndex) in (statistics?.questionSummaries[index] as FreeTextQuestionSummary).answers"
          :key="aIndex"
        >
          {{ answer }}
        </v-list-item>
      </v-list>

      <!-- Single Choice Question -->
      <v-container
        v-if="question.choices.length != 0"
      >
        <Bar
          class="w-100 h-100"
          :options="chartOptions"
          :data="StatisticsService.toDiagramData(question as SingleChoiceQuestion, statistics.questionSummaries[index] as SingleChoiceQuestionSummary)"
        />
      </v-container>
    </template>

  </v-card>

</template>

<style scoped>

</style>
