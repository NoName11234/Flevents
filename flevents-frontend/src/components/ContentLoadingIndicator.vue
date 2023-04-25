<template>
  <div v-show="loading">
    <span v-if="text">
      {{ text }}
    </span>
    <v-progress-linear
      indeterminate
      rounded-bar
      rounded
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
    default: undefined,
    type: String,
  }
})
</script>

<style scoped>

</style>
