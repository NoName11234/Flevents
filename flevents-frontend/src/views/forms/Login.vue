<template>

  <Heading text="Login" />

  <v-card :loading="loading" :disabled="loading">
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
    <v-divider />
    <v-container class="d-flex flex-column flex-sm-row justify-end gap">
      <v-btn
        :to="{ name: 'accounts.create' }"
        variant="text"
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
</template>

<script setup lang="ts">
import {ref, Ref} from "vue";
import {Account} from "@/models/account";
import {AxiosError} from "axios";
import Heading from "@/components/Heading.vue";
import {useRoute, useRouter} from "vue-router";
import {login} from "@/service/authService";
import {load} from "webfontloader";
const route = useRoute();
const showPass = ref(false);
const loading = ref(false);
const tooltip = ref("");
const router = useRouter();
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

async function fu() {
  loading.value = true;
  tooltip.value = '';
  try {
    await login(account.value.email!, account.value.secret!);
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        tooltip.value = 'Falsche Anmeldedaten';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        tooltip.value = 'Netzwerkfehler';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
  }
  loading.value = false;
  // console.log(response);
  // if(response.data === ""){
  //   document.cookie = "";
  //   security.resetAccount();
  //   tooltip.value = "Passwort oder Email ist falsch."
  // }else{
  //   document.cookie = `TOKEN=${JSON.stringify(response.data.accessToken)}`;
  //   let account = (await axios.get(`http://localhost:8082/api/accounts/${response.data.id}`, {headers: {'Authorization': `Bearer ${response.data.accessToken}` }})).data as Account
  //   security.setAccount(account);
  //   console.log(account);
  //   const decodedJwt = useJwt()
  //   console.log(decodedJwt);
   /* let url = "/";
    if(route.query.location != null && route.query.location !== "/accounts/login"){
      url = route.query.location as string
    }
    router.push({path: url});
    setTimeout(() => router.go(0), 100);*/
  // }

}
</script>

<style scoped>

</style>
