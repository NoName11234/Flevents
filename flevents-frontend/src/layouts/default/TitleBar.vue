<template>
  <v-app-bar>
    <v-container style="max-width: 800px;" class="d-flex flex-row align-center">
      <v-tooltip
        v-if="showBack"
        text="Zurück"
        open-delay="500"
        location="bottom"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            v-bind="props"
            :disabled="!loggedIn"
            icon="mdi-arrow-left"
            @click="$router.options.history.state.back === null ? null : $router.go(-1);"
          />
        </template>
      </v-tooltip>

      <v-img
        src="@/assets/logo-and-text.png"
        style="cursor: pointer"
        height="50px"
        @click="$router.push('/')"
      />

      <v-tooltip
        v-if="showLogout"
        text="Abmelden"
        open-delay="500"
        location="bottom"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            v-bind="props"
            :disabled="!loggedIn"
            icon="mdi-logout-variant"
            @click="logout()"
          />
        </template>
      </v-tooltip>
    </v-container>
    <v-progress-linear
      indeterminate
      absolute
      color="secondary"
      location="top"
      :active="globallyLoading"
    />
  </v-app-bar>
</template>

<script lang="ts" setup>
import router from "@/router";
import {useAppStore} from "@/store/app";
import {storeToRefs} from "pinia";
import {logout as authLogout} from "@/service/authService";

const props = defineProps({
  showLogout: {
    required: false,
    type: Boolean,
    default: true,
  },
  showBack: {
    required: false,
    type: Boolean,
    default: true,
  }
});

const appStore = useAppStore();
const { globallyLoading, loggedIn } = storeToRefs(appStore);

async function logout() {
  await router.push({ name: 'accounts.login' });
  await authLogout();
}
</script>
