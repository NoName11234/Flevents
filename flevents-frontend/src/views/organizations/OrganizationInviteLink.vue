<template>
  <Heading v-if="organization != undefined" :text="`Anmelden zu Organisation: ${organization.name}`"></Heading>
  <v-card>
    <v-container class="d-flex flex-column gap-3" @keydown.enter="fu()">
      <v-text-field
        label="Mailadresse"
        prepend-inner-icon="mdi-email"
        v-model="account.email"
        hide-details="auto"
      />
      <v-text-field
        label="Passwort"
        v-model="account.secret"
        :append-inner-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
        :type="showPass ? 'text' : 'password'"
        @click:append-inner="showPass = !showPass"
        prepend-inner-icon="mdi-lock"
        :rules="[() => account.secret !== '' || 'Dieses Feld wird benötigt.']"
        hide-details="auto"
        required
      />
      <div
        v-if="tooltip !== ''"
        class="text-error">
        {{tooltip}}
      </div>
    </v-container>
    <v-container class="d-flex flex-column flex-sm-row justify-end gap">
      <v-btn
        :to="{ name: 'accounts.create' }"
        variant="text"
        target="_blank"
      >
        Registrieren
      </v-btn>
      <v-btn
        @click="fu()"
        color="primary"
        prepend-icon="mdi-login-variant"
      >
        Login
      </v-btn>
    </v-container>
  </v-card>

  <v-overlay
    max-width="600"
    :model-value="succesfullLogin"
    class="align-center justify-center"
  >
    <v-card>
      <v-container><h4>Sind sie sicher das sie als {{accountStore.currentAccount?.firstname}} {{accountStore.currentAccount?.lastname}} an der Organisation {{organization.name}} teilnehmen wollen?</h4></v-container>
      <v-container v-if="enrollToolTip != ''" class="text-red">{{enrollToolTip}}</v-container>
      <v-container class="d-flex justify-end gap"><v-btn @click="succesfullLogin = false" variant="text">Abbrechen</v-btn><v-btn @click="enroll()" prepend-icon="mdi-check" color="primary">Anmelden</v-btn></v-container>

    </v-card>
  </v-overlay>
</template>


<script setup lang="ts">
import {onBeforeMount, onMounted, ref, Ref} from "vue";
import {Account} from "@/models/account";
import axios from "axios";
import Heading from "@/components/Heading.vue";
import security from "@/service/security";
import {useRoute, useRouter} from "vue-router";
import CardBanner from "@/components/CardBanner.vue";
import {FleventsEvent} from "@/models/fleventsEvent";
import {Organization} from "@/models/organization";
import {login} from "@/service/authService";
import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import OrganizationsApi from "@/api/organizationsApi";
import accountApi from "@/api/accountApi";
const route = useRoute();
const succesfullLogin = ref(false);
const address = ref("");
const organization = ref({} as Organization);
const showPass = ref(false);
const tooltip = ref("");
const router = useRouter();
const enrollToolTip = ref("");
const appStore = useAppStore();
const accountStore = useAccountStore();
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

async function fu(){
  succesfullLogin.value = false;
  document.cookie = "";
  security.resetAccount();
  let response;
  succesfullLogin.value = false;
  document.cookie = "";
  security.resetAccount();
  await login(account.value.email!, account.value.secret!);
  if(appStore.loggedIn){
    console.log("asdasdadasdadsadasd");
    succesfullLogin.value = true;
  }
}
async function enroll(){
  // console.log(JSON.parse(document.cookie.split(";")[0].split("=")[1]).uuid);
  try {
    await OrganizationsApi.acceptInvitation(route.params.uuid as string,  route.query.token as string)
    //await axios.post(`http://localhost:8082/api/organizations/${address.value}/add-account/${accountStore.currentAccount!.uuid as string}`, {}, {params: {token: route.query.token}})
    //const newAccount = await accountApi.getMe();
    //security.setAccount(newAccount.data);
    await router.push(`/organizations/${route.params.uuid as string}`);
  } catch (e) {
    // already enrolled
    console.error("Enrollment failed, probably already enrolled.");
    enrollToolTip.value = "Sie sind mit diesem Account schon angemeldet."
  }
}
onMounted(async () => {
  address.value = route.params.uuid as string;
  organization.value = (await axios.get(`http://localhost:8082/api/organizations/${address.value}`)).data
  console.log(organization.value);
})

</script>


<style scoped>

</style>
