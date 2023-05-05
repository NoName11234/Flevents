<script setup lang="ts">

import {useDisplay} from "vuetify";

const props = defineProps({
  showPersonal: {
    required: false,
    type: Boolean,
    default: true,
  },
  showManaged: {
    required: false,
    type: Boolean,
    default: true,
  },
  showExplore: {
    required: false,
    type: Boolean,
    default: true,
  },
  showAccount: {
    required: false,
    type: Boolean,
    default: true,
  },
  showConsole: {
    required: false,
    type: Boolean,
    default: true,
  }
});

const { xs: chevronBreakpoint } = useDisplay();

const minWidth = '130px';

</script>

<template>
  <v-bottom-navigation
    :elevation="5"
    color="primary"
    style="height: auto"
  >
    <div
      class="d-flex flex-row overflow-x-auto"
    >
      <v-btn
        v-if="showPersonal"
        value="personal"
        size="small"
        :to="{ name: 'home.personal' }"
        prepend-icon="mdi-heart"
        stacked
        :min-width="minWidth"
      >
        <small>Mein Bereich</small>
      </v-btn>

      <v-btn
        v-if="showManaged"
        value="manage"
        size="small"
        :to="{ name: 'home.manage' }"
        prepend-icon="mdi-pencil"
        stacked
        :min-width="minWidth"
      >
        <small>Verwalten</small>
      </v-btn>

      <v-btn
        v-if="showExplore"
        value="explore"
        size="small"
        :to="{ name: 'home.explore' }"
        prepend-icon="mdi-compass"
        stacked
        :min-width="minWidth"
      >
        <small>Entdecken</small>
      </v-btn>

      <v-btn
        v-if="showAccount"
        value="account"
        size="small"
        :to="{ name: 'home.account' }"
        prepend-icon="mdi-account"
        stacked
        :min-width="minWidth"
      >
        <small>Konto & Info</small>
      </v-btn>

      <v-btn
        v-if="showConsole"
        value="console"
        size="small"
        :to="{ name: 'home.console' }"
        prepend-icon="mdi-wrench"
        stacked
        :min-width="minWidth"
      >
        <small>Plattformkonsole</small>
      </v-btn>
    </div>
    <div
      v-if="chevronBreakpoint"
      class="d-flex flex-row justify-space-between align-center bg-transparent position-absolute h-100 w-100 opacity-50 noclip"
    >
      <v-icon
        icon="mdi-chevron-left"
      />
      <v-spacer />
      <v-icon
        icon="mdi-chevron-right"
      />
    </div>
  </v-bottom-navigation>
</template>

<style scoped>

</style>
