<script setup lang="ts">
import {Questionnaire} from "@/models/questionnaire";
import {computed, onMounted, ref} from "vue";
import {AnsweredQuestionnaire} from "@/models/answeredQuestionnaire";
import {useRouter} from "vue-router";
import {EventRole} from "@/models/eventRole";
import {OrganizationRole} from "@/models/organizationRole";
import {FleventsEvent} from "@/models/fleventsEvent";
import {AnsweredQuestion} from "@/models/answeredQuestion";
import {useAccountStore} from "@/store/account";
import QuestionnaireApi from "@/api/questionnaireApi";
import DatetimeService from "@/service/datetimeService";
import {useSurveyStatisticsStore} from "@/store/surveyStatistics";
import {AxiosError} from "axios";
import {useAppStore} from "@/store/app";
import SurveyStats from "@/components/SurveyStats.vue";
import {Statistics} from "@/models/statistics";

const props = defineProps({
  questionnaire: {
    required: true,
    type: Object as () => Questionnaire,
  },
  event: {
    required: true,
    type: Object as () => FleventsEvent,
  }
});

const emits = defineEmits(['update'])
const accountStore = useAccountStore();
const appStore = useAppStore();
const router = useRouter();
const isDelete = ref(false);
const tooltip = ref('');
const user = accountStore.currentAccount!;
const aq = ref({
  uuid: '',
  questionnaireId: props.questionnaire.uuid,
  userId: accountStore.currentAccount!.uuid,
  answers: props.questionnaire.questions.map(question => {
    if(question.choices.length != 0) {
      return {choice: question.choices[0]}as AnsweredQuestion
    }else{
        return {answer: ''} as AnsweredQuestion;
    }
  }),
} as AnsweredQuestionnaire);
const alreadyVoted = ref(false);
const loading = ref(true);

const statisticsStore = useSurveyStatisticsStore();
let statistics = ref({} as Statistics);
if (hasRights()) {
  statistics = statisticsStore.getStatisticsGetterOf(props.questionnaire.uuid);
}

onMounted(setup);
async function setup() {
  loading.value = true;
  console.log(props.questionnaire);
  console.log(aq.value.answers);
  try {
    const response = await QuestionnaireApi.getAnswers(props.questionnaire?.uuid, accountStore.currentAccount!.uuid);
    console.log("11111111111111")
    console.log(response);
    aq.value = response.data as AnsweredQuestionnaire;
    alreadyVoted.value = true;
  } catch (e) {
    console.error('Failed to fetch answered questionnaire.');
    alreadyVoted.value = false;
  }
  loading.value = false;
}

async function submitAnswers(pendingValidation: Promise<any>) {
  tooltip.value = '';
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    tooltip.value = "Es wurden nicht alle erforderlichen Angaben gemacht.";
    return;
  }
  loading.value = true;
  try {
    console.log("------------------------")
    console.log(aq);
    const response = QuestionnaireApi.saveAnswer(aq.value, props.questionnaire?.uuid);
    console.log(response);
    alreadyVoted.value = true;
    // TODO: replace with async store hydration
    appStore.addToast({
      text: 'Abgestimmt.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Abstimmen fehlgeschlagen: ${errorMessage}`,
      color: 'error',
    });
  }
  //await setup();
  loading.value = false;
}

async function deleteQuestionnaire() {
  loading.value = true;
  try {
    const response = await QuestionnaireApi.delete(props.questionnaire?.uuid);
    // TODO: replace with async store hydration
    isDelete.value = true;
    emits("update", props.questionnaire?.uuid);
    appStore.addToast({
      text: 'Fragebogen gelöscht.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Fragebogen konnte nicht gelöscht werden: ${errorMessage}`,
      color: 'error',
    });
  }
  loading.value = false;
}

function hasRights() {
  let eventRoles = [
    EventRole.tutor,
    EventRole.organizer,
  ];
  let hasEventRights = props.event?.accountPreviews.find(a => a.uuid === accountStore.currentAccount!.uuid && eventRoles.includes(a.role as EventRole))
  if (hasEventRights) return true;
  let organizationRoles = [
    OrganizationRole.admin,
    OrganizationRole.organizer,
  ];
  let hasOrganizationRights = user.organizationPreviews.find(o => o.uuid === props.event?.organizationPreview?.uuid && organizationRoles.includes(o.role as OrganizationRole));
  if (hasOrganizationRights) return true;
  return false;
}

</script>

<template>
  <v-expansion-panel v-if="!isDelete">

    <v-expansion-panel-title>
      <div class="d-flex flex-row justify-start align-center gap-3 w-100">
        <strong>
          {{ questionnaire.title }}
        </strong>
        <span class="text-grey">
          {{ DatetimeService.getDate(new Date(questionnaire.closingDate)) }}
        </span>
        <v-spacer />
      </div>
    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-column gap-3 my-3">

        <v-card
          elevation="0"
          border
          class="border-dashed"
        >

          <SurveyStats
            :questionnaire="questionnaire"
            :statistics="statistics"
          />

          <template v-if="hasRights">
            <v-divider class="border-dashed" />

            <v-container class="d-flex flex-column flex-sm-row justify-end gap">
              <v-spacer/>
              <v-btn
                variant="tonal"
                color="primary"
                append-icon="mdi-chevron-right"
                :to="{ name: 'events.questionnaires.results', params: { uuid: event.uuid, questionnaireUuid: questionnaire.uuid } }"
              >
                Ergebnisse ansehen
              </v-btn>
            </v-container>
          </template>

        </v-card>

        <v-form
          style="display: contents;"
          validate-on="submit"
          @submit.prevent="submitAnswers"
        >
          <v-card
            v-for="(question, index) in questionnaire.questions"
            :key="index"
            elevation="0"
            border
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
              :disabled="alreadyVoted || loading"
              v-model="(aq.answers[index]).answer"
            >
            </v-textarea>

            <v-radio-group
              v-if="question.choices.length != 0"
              hide-details="auto"
              class="pa-2"
              :disabled="alreadyVoted || loading"
              v-model="(aq.answers[index]).choice"
            >
              <v-radio
                v-for="(option, oIndex) in question.choices"
                :key="oIndex"
                :value="option"
                :label="option.choice"
              />
            </v-radio-group>

          </v-card>

          <div
            v-if="tooltip !== ''"
            class="text-error"
          >
            {{ tooltip }}
          </div>

          <div class="d-flex flex-column flex-sm-row justify-end gap">
            <v-btn
              v-if="hasRights()"
              prepend-icon="mdi-delete"
              color="error"
              variant="text"
              @click="deleteQuestionnaire()"
              :loading="loading"
            >
              Löschen
            </v-btn>
            <v-spacer />
            <v-btn
              prepend-icon="mdi-check"
              color="primary"
              type="submit"
              :disabled="loading || alreadyVoted"
              :loading="loading"
            >
              {{ alreadyVoted ? 'Abgestimmt' : 'Abstimmen' }}
            </v-btn>
          </div>
        </v-form>

      </div>
    </v-expansion-panel-text>

  </v-expansion-panel>
</template>

<style scoped>

</style>
