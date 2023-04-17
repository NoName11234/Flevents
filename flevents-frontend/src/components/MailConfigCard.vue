<template>
  <v-expansion-panel>

    <v-expansion-panel-title>
      <div class="d-flex flex-row justify-start align-center gap-3 w-100">
        <strong>
          {{ config['name'] }}
        </strong>
      </div>
    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-column gap-3 my-3">
        <v-card
          elevation="0"
          border
        >
          <v-textarea
            hide-details="auto"
            label="E-Mail-Text"
            variant="solo"
            v-model="configText"
          >
          </v-textarea>
        </v-card>

        <div
          v-if="tooltip !== ''"
          class="text-error"
        >
          {{ tooltip }}
        </div>

        <div class="d-flex flex-column flex-sm-row justify-end gap">
          <v-spacer />
          <v-btn
            prepend-icon="mdi-check"
            color="primary"
            @click="submit()"
          >
            Speichern
          </v-btn>
        </div>

      </div>
    </v-expansion-panel-text>

  </v-expansion-panel>
</template>

<script setup lang="ts">
import {ref} from "vue";

const props = defineProps({
  config: {
    required: true,
    type: Object,
  },
});

const configText = ref(props.config.text);
const tooltip = ref('');

async function submit() {
  //TODO: Post updated config to backend
  alert('Speichern fehlt noch');
}

</script>

<style scoped>

</style>
