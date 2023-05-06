<script setup lang="ts">
import {computed, ref} from "vue";
import {MailConfig} from "@/models/mailConfig";

const props = defineProps({
  config: {
    required: true,
    type: Object as () => MailConfig,
  },
  eventStart: {
    required: true,
    type: Date,
  },
  eventEnd: {
    required: true,
    type: Date,
  },
});

const emits = defineEmits<{
  (e: 'update', value: MailConfig): void
}>();

const tooltip = ref('');

const infoOffset = ref(1);
const feedbackOffset = ref(1);

async function submit() {
  emits.call(emits, 'update', props.config);
}

</script>

  <template>
    <v-container class="d-flex flex-column gap-3">
      <v-textarea
        label="Organisations-Einladungs-Email-Text"
        hide-details="auto"
        no-resize
        v-model="config.organizationInvitation"
      >
      </v-textarea>
    </v-container>

  <v-divider />

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
<!--    <v-text-field-->
<!--      label="Sendezeitpunkt vor dem Event in h"-->
<!--      prepend-inner-icon="mdi-clock"-->
<!--      min="0"-->
<!--      type="number"-->
<!--      v-model="infoOffset"-->
<!--      hide-details="auto"-->
<!--      :rules="[() => infoOffset >= 0 || 'Muss größer 1 sein.']"-->
<!--    />-->
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
<!--    <v-text-field-->
<!--      label="Sendezeitpunkt nach dem Event in h"-->
<!--      prepend-inner-icon="mdi-clock"-->
<!--      min="0"-->
<!--      type="number"-->
<!--      v-model="feedbackOffset"-->
<!--      hide-details="auto"-->
<!--      :rules="[() => feedbackOffset >= 0 || 'Muss größer 1 sein.']"-->
<!--    />-->
  </v-container>

  <v-divider />

  <v-container class="d-flex flex-column flex-sm-row justify-end gap">
    <v-spacer />
    <v-btn
      prepend-icon="mdi-check"
      color="primary"
      @click="submit()"
    >
      Speichern
    </v-btn>
  </v-container>
</template>

<style scoped>

</style>
