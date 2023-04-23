<template>
  <div class="my-10 d-flex flex-row align-center justify-start gap-3">
    <v-avatar
      v-if="logoSrc"
      :image="logoSrc"
      size="80"
      class="bg-grey-lighten-2"
    />
    <div class="d-flex flex-column align-start justify-center">
      <h1>
        {{ text }}
      </h1>
<!--      <span-->
<!--        v-if="subtext"-->
<!--        class="text-grey-lighten-1"-->
<!--      >-->
<!--        {{ subtext }}-->
<!--      </span>-->
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  text: {
    required: true,
    type: String,
  },
  subtext: {
    required: false,
    type: String,
  },
  logoSrc: {
    required: false,
    type: String,
  }
});
</script>

<style scoped>

</style>
