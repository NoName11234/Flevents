<script setup lang="ts">

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

</script>

<template>
  <v-card
    class="c-navigation-menu"
    elevation="0"
    border
  >
    <v-list
      density="comfortable"
      color="primary"
      variant="text"
      class="d-flex flex-column h-100"
      nav
    >

      <v-list-item
        v-if="showPersonal"
        value="personal"
        :to="{ name: 'home.personal' }"
        prepend-icon="mdi-heart"
        title="Mein Bereich"
      />

      <v-list-item
        v-if="showManaged"
        value="manage"
        :to="{ name: 'home.manage' }"
        prepend-icon="mdi-pencil"
        title="Verwalten"
      />

      <v-list-item
        v-if="showExplore"
        value="explore"
        :to="{ name: 'home.explore' }"
        prepend-icon="mdi-compass"
        title="Entdecken"
      />

      <v-list-item
        v-if="showAccount"
        value="account"
        :to="{ name: 'home.account' }"
        prepend-icon="mdi-account-circle"
        title="Konto & Info"
      />

      <v-spacer />

      <v-list-item
        v-if="showConsole"
        value="console"
        :to="{ name: 'home.console' }"
        prepend-icon="mdi-wrench"
        title="Plattformkonsole"
      />

    </v-list>
  </v-card>
</template>

<style scoped>
.c-navigation-menu {
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1002;
  width: 230px;
  height: 100vh;
  padding-top: 5rem;
  overflow: auto;
}
</style>
