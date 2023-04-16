<script setup lang="ts">
import {Questionnaire} from "@/models/questionnaire";
import {computed, onMounted, ref} from "vue";
import {QuestionType} from "@/models/questionType";
import {SingleChoiceQuestion} from "@/models/singleChoiceQuestion";
import {AnsweredQuestionnaire} from "@/models/answeredQuestionnaire";
import security from "@/service/security";
import {AnsweredSingleChoiceQuestion} from "@/models/answeredSingleChoiceQuestion";
import {AnsweredFreeTextQuestion} from "@/models/answeredFreeTextQuestion";
import axios from "axios";
import {useRouter} from "vue-router";
import {EventRole} from "@/models/eventRole";
import {OrganizationRole} from "@/models/organizationRole";
import {FleventsEvent} from "@/models/fleventsEvent";
import {Question} from "@/models/question";
import {AnsweredQuestion} from "@/models/answeredQuestion";

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

const emits = defineEmits<{
  (e: 'update'): void
}>();

const router = useRouter();
const tooltip = ref('');
const user = security.getAccount()!;
const aq = ref({
  uuid: '',
  questionnaire: props.questionnaire.uuid,
  userId: user.uuid,
  answers: props.questionnaire.questions.map(question => {
    switch (question.type) {
      case QuestionType.SingleChoice:
        return {choice: question.choices[0]} as AnsweredSingleChoiceQuestion
      case QuestionType.FreeText:
        return {answer: ''} as AnsweredFreeTextQuestion;
    }
  }),
} as AnsweredQuestionnaire);
const alreadyVoted = ref(false);
const loading = ref(true);

onMounted(setup);
async function setup() {
  loading.value = true;
  console.log(aq.value.answers);
  try {
    const response = await axios.get(
      `http://localhost:8082/api/questionnaires/${props.questionnaire.uuid}/answers/${user.uuid}`
    );
    console.log(response);
    aq.value = response.data as AnsweredQuestionnaire;
    alreadyVoted.value = true;
  } catch (e) {
    console.error('Failed to fetch answered questionnaire.');
    alreadyVoted.value = false;
  }
  loading.value = false;
}

async function submit() {
  loading.value = true;
  try {
    const response = await axios.post(
      `http://localhost:8082/api/questionnaires/${props.questionnaire.uuid}/answers`,
      aq.value
    );
    console.log(response);
  } catch (e) {
    console.error('Failed to answer questionnaire.', e);
    tooltip.value = 'Abstimmen fehlgeschlagen.';
  }
  await setup();
  loading.value = false;
}

async function remove(this: any) {
  loading.value = true;
  try {
    const response = await axios.delete(
      `http://localhost:8082/api/questionnaires/${props.questionnaire.uuid}`
    );
    console.log(response);
  } catch (e) {
    console.error('Failed to delete questionnaire.', e);
    tooltip.value = 'Löschen fehlgeschlagen.';
  }
  emits.call(emits, 'update');
  loading.value = false;
}

function hasRights() {
  let eventRoles = [
    EventRole.tutor,
    EventRole.organizer,
  ];
  let hasEventRights = props.event?.accountPreviews.find(a => a.uuid === user.uuid && eventRoles.includes(a.role as EventRole))
  if (hasEventRights) return true;
  let organizationRoles = [
    OrganizationRole.admin,
    OrganizationRole.organizer,
  ];
  let hasOrganizationRights = user.organizationPreviews.find(o => o.uuid === props.event?.organizationPreview.uuid && organizationRoles.includes(o.role as OrganizationRole));
  if (hasOrganizationRights) return true;
  return false;
}

</script>

<template>
  <v-expansion-panel>

    <v-expansion-panel-title>
      <div class="d-flex flex-row justify-start align-center gap-3 w-100">
        <strong>
          {{ questionnaire.title }}
        </strong>
        <span class="text-grey">
          {{ new Date(questionnaire.creationDate).toLocaleDateString("DE-de", {dateStyle: 'medium'}) }}
        </span>
        <v-spacer />
      </div>
    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-column gap-3 my-3">
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
          v-if="question.type === QuestionType.FreeText"
          hide-details="auto"
          label="Eigene Antwort"
          variant="solo"
          :disabled="alreadyVoted || loading"
          v-model="(aq.answers[index] as AnsweredFreeTextQuestion).answer"
        >
        </v-textarea>

        <v-radio-group
          v-if="question.type === QuestionType.SingleChoice"
          hide-details="auto"
          class="pa-2"
          :disabled="alreadyVoted || loading"
          v-model="(aq.answers[index] as AnsweredSingleChoiceQuestion).choice"
        >
          <v-radio
            v-for="(option, oIndex) in (question as SingleChoiceQuestion).choices"
            :key="oIndex"
            :value="option"
            :label="option"
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
            @click="remove()"
            :loading="loading"
          >
            Löschen
          </v-btn>
          <v-spacer />
          <v-btn
            prepend-icon="mdi-check"
            color="primary"
            @click="submit()"
            :disabled="alreadyVoted || loading"
            :loading="loading"
          >
            Abstimmen
          </v-btn>
        </div>

      </div>
    </v-expansion-panel-text>

  </v-expansion-panel>
</template>

<style scoped>

</style>
