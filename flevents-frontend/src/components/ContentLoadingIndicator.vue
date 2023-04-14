<template>
  <div v-show="loading" class="my-2">
    <span>
      {{ text }}
    </span>
    <v-progress-linear
      indeterminate
    />
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  loading: {
    required: false,
    default: true,
    type: Boolean,
  },
  text: {
    required: false,
    default: "Lade...",
    type: String,
  }
})
</script>

<style scoped>

</style>
