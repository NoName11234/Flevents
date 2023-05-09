<script setup lang="ts">
import {useAppStore} from "@/store/app";
import {storeToRefs} from "pinia";
import {Toast as ToastType} from "@/models/toast";
import Toast from "@/components/Toast.vue";

const appStore = useAppStore();
const { toasts } = storeToRefs(appStore);

function closeToast(toast: ToastType) {
  toasts.value.splice(toasts.value.indexOf(toast), 1);
}

</script>

<template>
  <v-container
    v-if="toasts.length > 0"
    class="c-toast-display"
  >
    <div
      class="d-flex flex-column justify-center justify-sm-start gap"
    >
      <Toast
        v-for="(toast, index) in toasts"
        :key="index"
        :toast="toast"
        @close="closeToast"
      />
    </div>
  </v-container>
</template>

<style scoped>
.c-toast-display {
  position: fixed;
  bottom: 4rem;
  right: 0;
  z-index: 1005;
  width: 100%;
  max-width: 350px;
  max-height: 50vh;
  overflow: auto;
}
</style>
