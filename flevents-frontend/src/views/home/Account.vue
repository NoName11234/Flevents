<script setup lang="ts">
import AccountInfo from "@/components/AccountInfo.vue";
import { Account } from "@/models/account";
import Heading from "@/components/Heading.vue";
import {onMounted, ref, Ref} from "vue";
import security from "@/service/security";

// TODO: Get from Pinia
const account: Ref<Account> = ref({
  uuid: "a1b2c3",
  firstname: "Peter",
  lastname: "Korstens",
  email: "peter-korstens@hochweriges-mail.de",
  secret: "",
  role: ""
} as Account);

// function resetCookie(){
//   console.log(document.cookie);
//   document.cookie += "; Max-Age=-99999999; path=/;";
//   console.log(document.cookie);
//   router.push("/accounts/login")
// }

onMounted(() => {
  //account.value = JSON.parse(document.cookie.split(";")[0].split("=")[1]);
  account.value = security.getAccount() as Account;
})
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
