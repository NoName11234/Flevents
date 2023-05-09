<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import Heading from "@/components/Heading.vue";
import {useQuestionnaireStore} from "@/store/questionnaires";
import {useQuestionnaireStatisticsStore} from "@/store/questionnaireStatistics";
import {computed, onMounted, onUnmounted, ref} from "vue";
import {useAppStore} from "@/store/app";
import {Bar, Pie} from "vue-chartjs";
import {
  Chart,
  BarElement,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  ArcElement,
  ChartOptions, Legend, ScaleChartOptions,
} from "chart.js";
import StatisticsService from "@/service/statisticsService";
import {SingleChoiceQuestionSummary} from "@/models/singleChoiceQuestionSummary";
import {SingleChoiceQuestion} from "@/models/singleChoiceQuestion";
import {FreeTextQuestionSummary} from "@/models/freeTextQuestionSummary";
import {Statistics} from "@/models/statistics";
import SurveyStats from "@/components/QuestionnaireStats.vue";

const route = useRoute();
const router = useRouter();
const eventUuid = route.params.uuid as string;
const questionnaireUuid = route.params.questionnaireUuid as string;

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'polls' } };

const appStore = useAppStore();

const surveyStore = useQuestionnaireStore();
const questionnaire = surveyStore.getQuestionnaireGetter(questionnaireUuid);
const questionnaireLoading = computed(() => surveyStore.specificLoading.get(questionnaireUuid));

const statisticsStore = useQuestionnaireStatisticsStore();
const statistics = statisticsStore.getStatisticsGetterOf(questionnaireUuid);
const statisticsLoading = computed(() => statisticsStore.specificLoading.get(questionnaireUuid));

const diagramType = ref('bar');

const isClosed = computed(() =>
  new Date(questionnaire.value.closingDate).getTime() - new Date().getTime() <= 0
);



// Functionality for live-updating

const liveUpdates = ref(false);
const liveUpdateInterval = 2500;
const liveUpdateHandlerFunction = () => {
  statisticsStore.hydrateSpecific(questionnaireUuid);
};
let liveUpdateHandler: NodeJS.Timeout;

onMounted(updateLiveUpdates);
onUnmounted(() => clearInterval(liveUpdateHandler));

function updateLiveUpdates() {
  if (liveUpdates.value !== true) {
    clearInterval(liveUpdateHandler);
    return;
  }
  liveUpdateHandlerFunction();
  liveUpdateHandler = setInterval(liveUpdateHandlerFunction, liveUpdateInterval);
}

const barChartOptions = {
  responsive: true,
  plugins: {
    legend: {
      display: false,
    },
  }
} as any;

const pieChartOptions = {
  responsive: true,
  scales: {

  } as ScaleChartOptions,
  plugins: {
    legend: {
      display: true,
      position: "right",
    },
  }
} as any;

Chart.register(Title, Tooltip, BarElement, CategoryScale, LinearScale, ArcElement, Legend);

function print() {
  window.print();
}

</script>

<template>

  <Heading :text="questionnaire.title ?? ''" />

  <v-card
    elevation="0"
    border
    class="border-dashed"
  >
    <SurveyStats
      :questionnaire="questionnaire"
      :statistics="statistics"
    />
    <v-divider class="border-dashed d-print-none"/>
    <v-container class="d-flex flex-row justify-end align-center gap-3 d-print-none">
      <v-spacer />
      <v-btn
        prepend-icon="mdi-printer"
        variant="text"
        @click="print"
      >
        Drucken
      </v-btn>
    </v-container>
  </v-card>

  <div class="d-flex flex-row justify-end align-center gap-3 d-print-none">
    <v-btn-toggle
      v-model="diagramType"
      density="default"
      variant="outlined"
      color="primary"
      mandatory
    >
      <v-btn
        value="bar"
        icon="mdi-chart-bar"
      />
      <v-btn
        value="pie"
        icon="mdi-chart-pie"
      />
    </v-btn-toggle>
    <v-switch
      v-if="!isClosed"
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
  </div>

  <v-card
    elevation="0"
    border
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
          v-for="[answer, count] of StatisticsService.unifyTextResults(statistics?.questionSummaries[index] as FreeTextQuestionSummary)"
          :key="answer"
        >
          <v-badge
            inline
            color="secondary"
            :content="count"
          />
          {{ answer }}
        </v-list-item>
      </v-list>

      <!-- Single Choice Question -->
      <v-container
        v-if="question.choices.length != 0"
      >
        <v-window v-model="diagramType">
          <v-window-item value="bar">
            <Bar
              class="w-100 h-100"
              :options="barChartOptions"
              :data="StatisticsService.toDiagramData(question as SingleChoiceQuestion, statistics.questionSummaries[index] as SingleChoiceQuestionSummary)"
            />
          </v-window-item>
          <v-window-item value="pie">
            <Pie
              class="w-100 h-100 mx-auto"
              style="max-width: 360px"
              :options="pieChartOptions"
              :data="StatisticsService.toDiagramData(question as SingleChoiceQuestion, statistics.questionSummaries[index] as SingleChoiceQuestionSummary)"
            />
          </v-window-item>
        </v-window>
      </v-container>
    </template>

    <template v-else>
      <v-container class="text-grey">
        Keine Antworten
      </v-container>
    </template>

  </v-card>

</template>

<style scoped>

</style>
