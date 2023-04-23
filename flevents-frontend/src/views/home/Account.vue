<script setup lang="ts">
import accountApi from "@/api/accountApi";
import AccountInfo from "@/components/AccountInfo.vue";
import Heading from "@/components/Heading.vue";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import { onMounted, ref } from "vue";

const accountStore = useAccountStore();
const deleteUser = ref(false); 
const { currentAccount: account } = storeToRefs(accountStore);
const selectedOrga = ref();
const select = ref(false);

function selectOrga(orga : any){
  selectedOrga.value = orga; 
  select.value = true; 
}


function deletAcc(){

}
onMounted(() => {
  console.log(account.value?.organizationPreviews);
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
    <v-btn
      class="mt-n8 mr-5"
      color="red"
      icon="mdi-delete"
      elevation="5"
      @click="deleteUser = true"
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
  <v-card>
    <v-card-title>Ihre Organisationen</v-card-title>
    <v-card-text>
      <div v-for="orga in account.organizationPreviews" class="d-flex justify-space-between align-center"><h3>{{orga.name}}</h3> <v-btn  prepend-icon="mdi-account-remove" color="primary" @click="selectOrga(orga)">Verlassen</v-btn></div>
    </v-card-text>
  </v-card>

  <v-dialog
    persistent
    :model-value="select"
    class="align-center justify-center"
    width="auto"
  >
    <v-card><v-card-title>Organisation verlassen</v-card-title>
      <v-card-text>Sind sie sicher das sie die Organisation {{selectedOrga.name}} verlassen möchten?</v-card-text>
    <v-card-actions><v-btn color="red" prepend-icon="mdi-account-remove" variant="elevated">Verlassen</v-btn><v-btn @click="select = false">Abbrechen</v-btn></v-card-actions></v-card>
  </v-dialog>
  
  <v-dialog
    persistent
    :model-value="deleteUser"
    class="align-center justify-center"
    width="auto"
  >
    <v-card><v-card-title>Account löschen</v-card-title>
      <v-card-text>Sind sie sicher das sie ihren Account löschen möchten?</v-card-text>
    <v-card-actions><v-btn color="red" prepend-icon="mdi-delete" variant="elevated" @click="accountApi.delete(account?.uuid as string)">Löschen</v-btn><v-btn @click="deleteUser = false">Abbrechen</v-btn></v-card-actions></v-card>
  </v-dialog>


</template>
