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
import {computed, onMounted, ref} from "vue";
import {getAccount} from "@/service/security";
import {OrganizationRole} from "@/models/organizationRole";
import {EventRole} from "@/models/eventRole";

const showManaged = computed( () => {return validateManaged()});
const showExplore = computed( () => {return validateExplore()});

function validateManaged(){
  let user = getAccount();
  console.log(user);
  if(user != null){
  for(let i = 0; i < getAccount().organizationPreviews.length; i++){
    if(getAccount().organizationPreviews[i].role == OrganizationRole.admin || getAccount().organizationPreviews[i].role == OrganizationRole.organizer){
      return true;
    }
  }
  for(let i = 0; i < getAccount().eventPreviews.length; i++){
    if(getAccount().eventPreviews[i].role == EventRole.tutor || getAccount().eventPreviews[i].role == EventRole.organizer){
      return true;
    }
  }
  }
  return false;
}

function validateExplore(){
  let user = getAccount();
  console.log(user);
  if(user != null){
    if (user.organizationPreviews && user.organizationPreviews.length > 0) {
      return true;
    }
  }
  return false;
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
