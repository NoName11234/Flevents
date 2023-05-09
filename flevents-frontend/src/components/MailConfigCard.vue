<script setup lang="ts">
import {ref} from "vue";
import {MailConfig} from "@/models/mailConfig";
import {Duration} from "@/models/duration";
import DurationService from "@/service/durationService";
import * as events from "events";
import {VALIDATION} from "@/constants";

const props = defineProps({
  config: {
    required: true,
    type: Object as () => MailConfig,
  }
});

const emits = defineEmits<{
  (e: 'update', value: MailConfig): void
}>();

const tooltip = ref('');

async function submit(pendingValidation: Promise<any>) {
  tooltip.value = '';
  const validation = await pendingValidation;
  if (validation.valid !== true) {
    tooltip.value = 'Angaben sind ungültig.';
    return;
  }
  emits.call(emits, 'update', props.config);
}

</script>

<template>
  <v-form validate-on="submit" @submit.prevent="submit">
    <v-container class="d-flex flex-column gap-3">
      <v-textarea
        label="Registrierungs-E-Mail-Text"
        hide-details="auto"
        no-resize
        v-model="config.registerMessage"
      >
      </v-textarea>
    </v-container>

    <v-divider />

    <v-container class="d-flex flex-column gap-3">
      <v-textarea
        label="Info-E-Mail-Text"
        hide-details="auto"
        no-resize
        v-model="config.infoMessage"
      >
      </v-textarea>
      <v-text-field
        label="Sendezeitpunkt vor dem Event in h"
        prepend-inner-icon="mdi-clock"
        min="0"
        type="number"
        :model-value="DurationService.extractHours(config.infoMessageOffset)"
        @input="e => config.infoMessageOffset = DurationService.toDuration(Number.parseInt(e.target.value))"
        hide-details="auto"
        :rules="[
          v => v >= 0 || 'Kann nicht kleiner 0 sein.',
          v => v <= VALIDATION.MAX_MAIL_OFFSET || `Kann nicht größer als ${VALIDATION.MAX_MAIL_OFFSET} sein.`
          ]"
      />
    </v-container>

    <v-divider />

    <v-container class="d-flex flex-column gap-3">
      <v-textarea
        label="Abschluss-E-Mail-Text"
        hide-details="auto"
        no-resize
        v-model="config.feedbackMessage"
      >
      </v-textarea>
      <v-text-field
        label="Sendezeitpunkt nach dem Event in h"
        prepend-inner-icon="mdi-clock"
        min="0"
        type="number"
        :model-value="DurationService.extractHours(config.feedbackMessageOffset)"
        @input="e => config.feedbackMessageOffset = DurationService.toDuration(Number.parseInt(e.target.value))"
        hide-details="auto"
        :rules="[
          v => v >= 0 || 'Kann nicht kleiner 0 sein.',
          v => v <= VALIDATION.MAX_MAIL_OFFSET || `Kann nicht größer als ${VALIDATION.MAX_MAIL_OFFSET} sein.`
          ]"
      />
    </v-container>

    <v-divider />

    <v-container class="d-flex flex-column flex-sm-row justify-end gap">
      <span class="text-error w-100 h-100">
        {{ tooltip }}
      </span>
      <v-spacer />
      <v-btn
        prepend-icon="mdi-check"
        color="primary"
        type="submit"
      >
        Speichern
      </v-btn>
    </v-container>
  </v-form>
</template>

<style scoped>

</style>
