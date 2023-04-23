<template>
  <v-bottom-navigation
    :elevation="5"
    class="overflow-x-auto"
    style="height: 4rem;"
    grow
    active
    color="primary"
  >
    <v-btn
      value="personal"
      :to="{ name: 'home.personal' }"
    >
      <v-icon>mdi-calendar-heart</v-icon>

      Mein Bereich
    </v-btn>

    <v-btn
      v-if="showManaged"
      value="manage"
      :to="{ name: 'home.manage' }"
    >
      <v-icon>mdi-pencil</v-icon>

      Verwalten
    </v-btn>

    <v-btn
      v-if="showExplore"
      value="explore"
      :to="{ name: 'home.explore' }"
    >
      <v-icon>mdi-compass</v-icon>

      Entdecken
    </v-btn>

    <v-btn
      value="account"
      :to="{ name: 'home.account' }"
    >
      <v-icon>mdi-account-circle</v-icon>

      Konto & Info
    </v-btn>
  </v-bottom-navigation>
</template>

<script setup lang="ts">
import {computed} from "vue";
import {OrganizationRole} from "@/models/organizationRole";
import {EventRole} from "@/models/eventRole";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const showManaged = computed( () => {return validateManaged()});
const showExplore = computed( () => {return validateExplore()});

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
}

function validateExplore(){
  if (account.value === null) return false;
  if (
    account.value.organizationPreviews
    && account.value.organizationPreviews.length > 0
  ) {
    return true;
  }
}
</script>

<style scoped>
.v-icon {
  margin-bottom: 0.25rem;
}
.v-btn {
  padding: 0.125rem;
  min-width: 200px;
}
</style>
