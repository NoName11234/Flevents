<template>
  <v-app class="bg-grey-lighten-4">
    <default-bar />
    <v-main
      style="width: 100%; max-width: 800px; margin: auto"
    >
      <ContentFlexContainer>
        <router-view />
      </ContentFlexContainer>
    </v-main>
    <navigation-bar />
  </v-app>
</template>

<script lang="ts" setup>
import DefaultBar from '@/layouts/default/TitleBar.vue'
import NavigationBar from "@/layouts/default/NavigationBar.vue";
import ContentFlexContainer from "@/layouts/default/ContentFlexContainer.vue";
</script>

<style scoped>

</style>
