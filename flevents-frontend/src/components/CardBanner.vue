<template>
  <v-card>
    <v-banner
      color="error"
      :text="message"
      class="border-b-0 text-error"
      stacked
    />
  </v-card>
</template>

<script setup lang="ts">
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  message: {
    required: true,
    type: String,
  }
})
</script>

<style scoped>

</style>
