<script setup lang="ts">

import {Toast} from "@/models/toast";
import {computed, ref} from "vue";
import {UI} from "@/constants";

const props = defineProps({
  toast: {
    required: true,
    type: Object as () => Toast,
  }
});

const emits = defineEmits<{
  (e: 'close', value: Toast): void
}>();

const show = ref(true);
const timeLeft = ref(UI.TOAST_DISPLAY_TIMEOUT);
const percentLeft = computed(() => timeLeft.value / UI.TOAST_DISPLAY_TIMEOUT * 100);
const updateInterval = 10;

let updateTimeLeft = setInterval(async () => {
  if (timeLeft.value <= 0) {
    clearInterval(updateTimeLeft);
    return;
  }
  timeLeft.value -= 1.5 * updateInterval;
}, updateInterval);

function close() {
  //show.value = false;
  emits.call(emits, 'close', props.toast);
}

</script>

<template>
  <v-card
    v-if="show"
    density="compact"
    elevation="5"
    :color="toast.color"
  >
    <v-progress-linear
      :model-value="percentLeft"
      rounded-bar
      rounded
    />
    <v-card-text class="d-flex flex-row gap-2">
      <span class="text-break">
        {{ toast.text }}
      </span>
      <v-spacer />
      <v-btn
        icon="mdi-close"
        size="small"
        density="comfortable"
        variant="text"
        @click="close()"
      />
    </v-card-text>
  </v-card>
</template>

<style scoped>

</style>
