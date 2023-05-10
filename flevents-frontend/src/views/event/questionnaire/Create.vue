<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {Questionnaire} from "@/models/questionnaire";
import {Question} from "@/models/question";
import {QuestionType} from "@/models/questionType";
import questionnaireApi from "@/api/questionnaireApi";
import {Choice} from "@/models/choice";
import {AxiosError} from "axios";
import {useQuestionnaireStore} from "@/store/questionnaires";

const router = useRouter();
const route = useRoute();
const tooltip = ref("");
const eventUuid = route.params.uuid as string;
const loading = ref(false);
const questionnaire = ref({
  eventId:  eventUuid as string,
  title: '',
  questions: [{
    question: '',
    questionType: QuestionType.FreeText
  }] as Question[],
} as Questionnaire);

const questionnaireStore = useQuestionnaireStore();

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'polls' } };

const questionTypes = [
  QuestionType.SingleChoice,
  QuestionType.FreeText,
];
const questionIcons = new Map<QuestionType, string>([
  [QuestionType.SingleChoice, 'mdi-format-list-bulleted'],
  [QuestionType.FreeText, 'mdi-text']
]);
const questionNames = new Map<QuestionType, string>([
  [QuestionType.SingleChoice, 'Single-Choice'],
  [QuestionType.FreeText, 'Freitext']
]);

async function addQuestion(type: QuestionType) {
  tooltip.value = '';
  let question: Question;
  switch (type) {
    case QuestionType.SingleChoice:
      question = {
        question: '',
        choices: [
          {choice: ''} as Choice,
          {choice: ''} as Choice,
        ],
        questionType: QuestionType.SingleChoice,
      } as Question;
      break;
    case QuestionType.FreeText:
      question = {
        question: '',
        questionType: QuestionType.FreeText,
      } as Question;
      break;
  }
  questionnaire.value.questions.push(question);
}

async function removeQuestion(index: number) {
  if (questionnaire.value.questions.length <= 1) {
    tooltip.value = 'Fragebögen müssen mindestens eine Frage enthalten.';
    return;
  }
  questionnaire.value.questions.splice(index, 1);
}

async function addChoice(question: Question) {
  tooltip.value = '';
  question.choices.push({choice: ''} as Choice);
}
async function removeChoice(question: any, index: number) {
  if (question.choices.length <= 2) {
    tooltip.value = 'Single-Choice-Fragen müssen mindestens zwei Antwortmöglichkeiten haben.';
    return;
  }
  question.choices.splice(index, 1);
}

async function submit(pendingValidation: Promise<any>){
  tooltip.value = '';
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    tooltip.value = "Es wurden nicht alle erforderlichen Angaben gemacht.";
    return;
  }
  loading.value = true;
  try {
    const response = await questionnaireApi.create(questionnaire.value, eventUuid);
    await router.push(backRoute);
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
    tooltip.value = `Speichern des Fragebogens fehlgeschlagen: ${errorMessage}`;
  }
  loading.value = false;
  questionnaireStore.hydrateSpecificOf(eventUuid);
}

</script>

<template>
  <Heading text="Fragebogen erstellen" />

  <v-card>
    <v-form validate-on="submit" @submit.prevent="submit">
      <v-container class="d-flex flex-column gap-3">

        <v-text-field
          v-model="questionnaire.title"
          label="Titel"
          :rules="[() => questionnaire.title !== '' || 'Dieses Feld wird benötigt.']"
          hide-details="auto"
          required
        />

        <v-text-field
          label="Einsendeschluss"
          type="datetime-local"
          v-model="questionnaire.closingDate"
          :rules="[
            () => questionnaire.closingDate !== '' || 'Fragebögen müssen einen Einsendeschluss haben.',
            () => new Date(questionnaire.closingDate).getTime() - new Date().getTime() > 0 || 'Einsendeschluss muss in der Zukunft liegen.'
            ]"
          hide-details="auto"
          required
          prepend-inner-icon="mdi-timer-sand-complete"
        />
      </v-container>
      <v-divider/>
      <v-container class="d-flex flex-column gap-3">
        <div
          v-for="(question, qIndex) in questionnaire.questions"
          :key="qIndex"
        >
          <v-card
            border
            elevation="0"
          >
            <v-text-field
              :label=" questionNames.get(question.questionType) + '-Frage'"
              :prepend-inner-icon="questionIcons.get(question.questionType) ?? 'mdi-help'"
              hide-details="auto"
              v-model="question.question"
              :rules="[() => question.question !== '' || 'Fragen brauchen einen Fragetext.']"
              bg-color="primary"
            />

            <!-- TYPE = SINGLE CHOICE -->
            <v-container
              v-if="question.questionType === QuestionType.SingleChoice"
              class="d-flex flex-column gap-3"
            >
              <div
                v-for="(choice, cIndex) in question.choices"
                :key="cIndex"
                class="d-flex flex-row"
              >
                <v-radio disabled class="flex-grow-0"/>
                <v-text-field
                  v-model="question.choices[cIndex].choice"
                  :label="`Option ${cIndex + 1}`"
                  variant="outlined"
                  density="compact"
                  :rules="[() => question.choices[cIndex].choice !== '' || 'Antwortmöglichkeiten dürfen nicht leer sein.']"
                  hide-details="auto"
                />
                <v-btn
                  icon="mdi-delete"
                  size="small"
                  variant="text"
                  color="error"
                  @click="removeChoice(question, cIndex)"
                />
              </div>
            </v-container>

            <!-- TYPE = FREE-TEXT -->
            <v-container
              v-else-if="question.questionType === QuestionType.FreeText"
              class="d-flex flex-column gap-3"
            >
              <span class="text-grey">
                Freitext-Antwort
              </span>
            </v-container>

            <v-divider />

            <v-container>
              <div class="d-flex flex-column flex-sm-row justify-start gap">
                <v-btn
                  v-if="question.questionType === QuestionType.SingleChoice"
                  prepend-icon="mdi-plus-circle-outline"
                  color="primary"
                  variant="text"
                  @click="addChoice(question)"
                >
                  Option hinzufügen
                </v-btn>
                <v-spacer />
                <v-btn
                  prepend-icon="mdi-delete"
                  variant="text"
                  color="error"
                  @click="removeQuestion(qIndex)"
                >
                  Frage entfernen
                </v-btn>
              </div>
            </v-container>
          </v-card>
        </div>

        <div class="d-flex flex-column flex-sm-row justify-start gap">
          <v-btn
            prepend-icon="mdi-plus-circle"
            color="primary"
            variant="text"
          >
            Frage hinzufügen

            <v-menu activator="parent">
              <v-list>
                <v-list-item
                  v-for="(entry, index) in questionTypes"
                  :key="index"
                  :title="questionNames.get(entry) ?? ''"
                  :prepend-icon="questionIcons.get(entry) ?? ''"
                  @click="addQuestion(entry)"
                />
              </v-list>
            </v-menu>
          </v-btn>
        </div>

        <div
          v-if="tooltip !== ''"
          class="text-error"
        >
          {{tooltip}}
        </div>
      </v-container>
      <v-divider />
      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="text"
          :to="backRoute"
        >
          Verwerfen
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          prepend-icon="mdi-check"
        >
          Speichern
        </v-btn>
      </v-container>
    </v-form>
  </v-card>

</template>

<style scoped>

</style>
