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
import {useQuestionnaireStatisticsStore} from "@/store/questionnaireStatistics";
import {AxiosError} from "axios";
import {useAppStore} from "@/store/app";
import SurveyStats from "@/components/QuestionnaireStats.vue";
import {Statistics} from "@/models/statistics";
import {useQuestionnaireStore} from "@/store/questionnaires";
import {useAnsweredQuestionnaireStore} from "@/store/answeredQuestionnaires";
import {storeToRefs} from "pinia";
import {Choice} from "@/models/choice";

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

const accountStore = useAccountStore();
const appStore = useAppStore();
const questionnaireStore = useQuestionnaireStore();

const tooltip = ref('');
const { currentAccount: account } = storeToRefs(accountStore);

const answerStore = useAnsweredQuestionnaireStore();
const savedAq = answerStore.getQuestionnaireGetter(props.questionnaire?.uuid, account.value!.uuid);
const answerError = computed(() => answerStore.specificError.get(props.questionnaire?.uuid));
const aq = ref({
    uuid: '',
    questionnaireId: props.questionnaire.uuid,
    userId: accountStore.currentAccount!.uuid,
    answers: props.questionnaire.questions.map(question => {
      if (question?.choices?.length != 0) {
        return { choice: question?.choices[0] } as AnsweredQuestion
      } else {
        return { answer: '' } as AnsweredQuestion;
      }
    }),
  } as AnsweredQuestionnaire);

const alreadyVoted = computed(() => answerError.value === false );
const loading = ref(false);

const isClosed = computed(() =>
  new Date(props.questionnaire.closingDate).getTime() - new Date().getTime() <= 0
);

const hasRights = computed(() => {
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
  let hasOrganizationRights = account.value!.organizationPreviews.find(o => o.uuid === props.event?.organizationPreview?.uuid && organizationRoles.includes(o.role as OrganizationRole));
  if (hasOrganizationRights) return true;
  return false;
});

const statisticsStore = useQuestionnaireStatisticsStore();
let statistics = ref({} as Statistics);
if (hasRights.value) {
  statistics = statisticsStore.getStatisticsGetterOf(props.questionnaire.uuid);
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
    const response = await QuestionnaireApi.saveAnswer(aq.value, props.questionnaire?.uuid);
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
  loading.value = false;
  answerStore.hydrateSpecific(props.questionnaire?.uuid, account.value!.uuid);
}

async function deleteQuestionnaire() {
  loading.value = true;
  try {
    const response = await QuestionnaireApi.delete(props.questionnaire?.uuid);
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
  questionnaireStore.hydrateSpecificOf(props.event.uuid!);
}

const badgeData = computed(() => {
  if (alreadyVoted.value) {
    return { code: 0, color: 'primary', text: 'abgestimmt', icon: 'mdi-check' };
  }
  if (isClosed.value) {
    return { code: -1, color: 'grey', text: 'geschlossen', icon: 'mdi-close' };
  }
  return { code: 1, color: 'success', text: 'offen', icon: 'mdi-circle' };
});

function setChoice(index: number, choice: Choice) {
  (aq.value.answers[index]).choice = choice;
}

</script>

<template>
  <v-expansion-panel>

    <v-expansion-panel-title>
      <div class="d-flex flex-row justify-start align-center w-100">
        <strong>
          {{ questionnaire.title }}
        </strong>
        <v-spacer />
        <v-badge
          :content="badgeData.text"
          :color="badgeData.color"
          class="text-capitalize"
          inline
        />
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
          :disabled="isClosed"
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
              :model-value="(savedAq?.answers?.[index])?.answer"
              @input="(e: Event) => (aq.answers[index]).answer = (e.target as HTMLInputElement).value"
            >
            </v-textarea>

            <v-radio-group
              v-if="question.choices.length != 0"
              hide-details="auto"
              class="pa-2"
              :disabled="alreadyVoted || loading"
              :model-value="(savedAq?.answers?.[index])?.choice || question.choices[0]"
            >
              <v-radio
                v-for="(option, oIndex) in question.choices"
                :key="oIndex"
                :value="option"
                :label="option.choice"
                @focus="() => setChoice(index, option)"
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
              v-if="hasRights"
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
              :disabled="loading || alreadyVoted || isClosed"
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
