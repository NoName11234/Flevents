<script setup lang="ts">
import {useAppStore} from "@/store/app";
import {storeToRefs} from "pinia";
import {Toast as ToastType} from "@/models/toast";
import Toast from "@/components/toast.vue";

const appStore = useAppStore();
const { toasts } = storeToRefs(appStore);

function closeToast(toast: ToastType) {
  toasts.value.splice(toasts.value.indexOf(toast), 1);
}

</script>

<template>
  <v-container
    class="d-flex flex-column justify-center justify-sm-start gap c-toast-display"
  >
    <Toast
      v-for="(toast, index) in toasts"
      :key="index"
      :toast="toast"
      @close="closeToast"
    />
  </v-container>
</template>

<style scoped>
.c-toast-display {
  position: fixed;
  bottom: 4rem;
  left: 0;
  z-index: 1008;
  width: 100%;
  max-width: 350px;
}
</style>
