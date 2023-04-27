<template>
  <v-app class="bg-grey-lighten-4">
    <default-bar
      :show-back="false"
      :show-logout="false"
    />
    <toast-display />
    <v-main
      style="width: 100%; max-width: 800px; margin: auto"
    >
      <ContentFlexContainer>
        <router-view />
      </ContentFlexContainer>
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
import ContentFlexContainer from "@/layouts/default/ContentFlexContainer.vue";
import ToastDisplay from "@/layouts/default/ToastDisplay.vue";
import DefaultBar from "@/layouts/default/TitleBar.vue";
</script>

<style scoped>

</style>
