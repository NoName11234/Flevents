<template>
  <v-app-bar>
    <v-container style="max-width: 800px;" class="d-flex flex-row align-center">
      <v-tooltip
        text="Zurück"
        open-delay="500"
        location="bottom"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            v-bind="props"
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
        text="Abmelden"
        open-delay="500"
        location="bottom"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            v-bind="props"
            :disabled="account == null"
            icon="mdi-logout-variant"
            @click="logout()"
          />
        </template>
      </v-tooltip>
    </v-container>
  </v-app-bar>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import router from "@/router";
import security from "@/service/security";

const account : any = ref(security.getAccount());

async function logout() {
  await router.push({ name: 'accounts.login' });
  security.resetAccount();
  account.value = null;
}
</script>
