<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {Questionnaire} from "@/models/questionnaire";
import {Question} from "@/models/question";
import {SingleChoiceQuestion} from "@/models/singleChoiceQuestion";
import {QuestionType} from "@/models/questionType";
import questionnaireApi from "@/api/questionnaireApi";
import {Choices} from "@/models/choices";

const router = useRouter();
const route = useRoute();
const tooltip = ref("");
const eventId = route.params.uuid;
const questionnaire = ref({
  eventId:  eventId as string,
  creationDate: new Date().toISOString(),
  questions: [{
    question: '',
    questionType: QuestionType.FreeText
  }] as Question[],
} as unknown as Questionnaire);

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
          {choice: ''} as Choices,
          {choice: ''} as Choices,
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
  question.choices.push({choice: ''} as Choices);
}
async function removeChoice(question: SingleChoiceQuestion, index: number) {
  if (question.choices.length <= 2) {
    tooltip.value = 'Single-Choice-Fragen müssen mindestens zwei Antwortmöglichkeiten haben.';
    return;
  }
  question.choices.splice(index, 1);
}

async function submit(){
  console.log(JSON.stringify(questionnaire.value));

  try {
    const response = questionnaireApi.create( questionnaire.value, eventId as string)
    console.log(response);
  } catch (e) {
    console.error('Failed to save questionnaire.', e);
    tooltip.value = 'Speichern des Fragebogens fehlgeschlagen.';
  }
  await router.push({ name: 'events.event', params: { uuid: eventId }});
}

</script>

<template>
  <Heading text="Fragebogen erstellen" />

  <v-card>
    <v-form validate-on="submit" @submit.prevent="submit()">
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
          :rules="[() => questionnaire.closingDate !== '' || 'Fragebögen müssen einen Einsendeschluss haben.']"
          hide-details="auto"
          required
          prepend-inner-icon="mdi-timer-sand"
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
              :prepend-inner-icon="questionIcons.get(question.questionType)"
              hide-details="auto"
              v-model="question.question"
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
                  :title="questionNames.get(entry)"
                  :prepend-icon="questionIcons.get(entry)"
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
          :to="{ name: 'events.event', params: { uuid: eventId } }"
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
