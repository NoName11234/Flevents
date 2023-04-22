<script setup lang="ts">
import AccountInfo from "@/components/AccountInfo.vue";
import Heading from "@/components/Heading.vue";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);
</script>

<template>
  <Heading
    text="Konto & Info"
    subtext="Ihre Daten und wie wir damit umgehen."
  />
  <AccountInfo :account="account"/>
  <div class="d-flex flex-row justify-end">
    <v-btn
      class="mt-n8 mr-5"
      color="primary"
      icon="mdi-pencil"
      elevation="5"
      :to="{ name: 'accounts.edit' }"
    />
<!--    <v-btn-->
<!--      class="mt-n8 mr-5"-->
<!--      color="primary"-->
<!--      icon="mdi-logout-variant"-->
<!--      elevation="5"-->
<!--      @click="/* resetCookie(); */ security.resetAccount(); router.push({ name: 'home' });"-->
<!--    />-->
  </div>
  <v-card>
    <v-card-title>Weitere Informationen</v-card-title>
    <v-card-text>
      "Flevents" ist ein Studentenprojekt im Rahmen einer Vorlesung der DHBW Stuttgart Campus Horb.
    </v-card-text>
    <v-card-actions>
      <v-btn color="primary" role="link" target="_blank" href="https://www.flyndre.de/impressum">
        Impressum
      </v-btn>
      <v-btn color="primary" role="link" target="_blank" href="https://www.flyndre.de/datenschutz">
        Datenschutzerklärung
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
