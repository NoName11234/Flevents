<template>

  <Heading text="Login" />

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
import {onMounted, ref, Ref} from "vue";
import {Account} from "@/models/account";
import axios from "axios";
import Heading from "@/components/Heading.vue";
import security from "@/service/security";
import {useRoute, useRouter} from "vue-router";
const route = useRoute();
const showPass = ref(false);
const tooltip = ref("");
const router = useRouter();
const account : Ref<Partial<Account>> = ref({
  email: "",
  secret: ""
});

async function fu(){
  document.cookie = "";
  security.resetAccount();
  let response;
  await axios.get("http://h3005487.stratoserver.net:8082/api/accounts/validate",{params: { email: account.value.email, secret: account.value.secret } }).then(
    resp => {
      console.log(resp);
      response = resp.data
      if(resp.data === ""){
        document.cookie = "";
        security.resetAccount();
        tooltip.value = "Passwort oder Email ist falsch."
      }else{
        document.cookie = `ACCOUNT=${JSON.stringify(resp.data)}`;
        security.setAccount(resp.data as Account);
        let url = "/";
        if(route.query.location != null && route.query.location !== "/accounts/login"){
          url = route.query.location as string
        }
        router.push({path: url});
        setTimeout(() => router.go(0), 100);
      }
    }
  ).catch((e) => {
    tooltip.value = "E-Mail oder Passwort ungültig!";
  });
}

onMounted(() => {
  console.log();
})
</script>

<style scoped>

</style>
