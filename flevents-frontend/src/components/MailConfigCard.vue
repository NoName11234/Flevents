<script setup lang="ts">
import {computed, ref} from "vue";
import {MailConfig} from "@/models/mailConfig";
import DurationService from "@/service/durationService";
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

const infoMessageOffset = computed({
  get: () => DurationService.extractHours(props.config.infoMessageOffset),
  set: v => props.config.infoMessageOffset = DurationService.toDuration(v)
});

const feedbackMessageOffset = computed({
  get: () => DurationService.extractHours(props.config.feedbackMessageOffset),
  set: v => props.config.feedbackMessageOffset = DurationService.toDuration(v)
});

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
        v-model="infoMessageOffset"
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
        v-model="feedbackMessageOffset"
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
