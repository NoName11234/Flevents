<template>
  <v-app class="bg-grey-lighten-4">
    <default-bar />
    <toast-display />
    <v-main class="c-main">
      <ContentFlexContainer>
        <router-view />
      </ContentFlexContainer>
    </v-main>
    <desktop-nav-bar
      v-show="showDesktopBar"
      :show-personal="showPersonal"
      :show-managed="showManaged"
      :show-explore="showExplore"
      :show-account="showAccount"
      :show-console="showConsole"
    />
    <mobile-nav-bar
      v-show="showMobileBar"
      :show-personal="showPersonal"
      :show-managed="showManaged"
      :show-explore="showExplore"
      :show-account="showAccount"
      :show-console="showConsole"
    />
  </v-app>
</template>

<script setup lang="ts">
import DefaultBar from '@/layouts/default/TitleBar.vue'
import ContentFlexContainer from "@/layouts/default/ContentFlexContainer.vue";
import {useAppStore} from "@/store/app";
import ToastDisplay from "@/layouts/default/ToastDisplay.vue";
import {useDisplay} from "vuetify";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import {computed} from "vue";
import {OrganizationRole} from "@/models/organizationRole";
import {EventRole} from "@/models/eventRole";
import MobileNavBar from "@/layouts/default/MobileNavBar.vue";
import DesktopNavBar from "@/layouts/default/DesktopNavBar.vue";

const appStore = useAppStore();

const { lgAndUp: navigationBreakpoint } = useDisplay();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const showDesktopBar = computed(() => (appStore.loggedIn && navigationBreakpoint.value));
const showMobileBar = computed(() => (appStore.loggedIn && !navigationBreakpoint.value));

const showPersonal = computed( () => true);
const showManaged = computed( () => validateManaged());
const showExplore = computed( () => validateExplore());
const showAccount = computed( () => true);
const showConsole = computed(() => validateConsole());

function validateManaged(){
  if (account.value === null) return false;
  for (let i = 0; i < account.value.organizationPreviews.length; i++) {
    if (
      account.value.organizationPreviews[i].role == OrganizationRole.admin
      || account.value.organizationPreviews[i].role == OrganizationRole.organizer
    ){
      return true;
    }
  }
  for (let i = 0; i < account.value.eventPreviews.length; i++) {
    if(
      account.value.eventPreviews[i].role == EventRole.tutor
      || account.value.eventPreviews[i].role == EventRole.organizer
    ){
      return true;
    }
  }
  return false;
}

function validateExplore(){
  if (account.value === null) return false;
  return account.value.organizationPreviews
    && account.value.organizationPreviews.length > 0;
}

function validateConsole() {
  if (account.value === null) return false;
  return account.value.platformAdmin === true;
}

</script>

<style scoped>
.c-main {
  width: 100%;
  max-width: 800px;
  margin: auto;
}
</style>
