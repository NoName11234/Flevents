<script setup lang="ts">
import {onBeforeMount, Ref, ref} from "vue";
import {Account} from "@/models/account";
import axios from "axios";
import {useRoute} from "vue-router";
import Heading from "@/components/Heading.vue";
const showPass = ref(false);
const showCorr = ref(false);
const route = useRoute();
const correctPassword = ref("");
const tooltip = ref("");
const adress = ref("");
const account : Ref<Partial<Account>> = ref({
  firstname: "",
  lastname: "",
  email: "",
  secret: ""
})

</script>
<template>
  <h1>Nutzer bearbeiten</h1>
  <v-card>
    <v-form validate-on="submit">

        <div class="d-flex">
          <v-container class="w-25 d-flex justify-center align-center" >
            <v-badge icon="mdi-pencil" offset-x="15" offset-y="100" color="grey" bordered>
              <v-avatar size="120" class="mb-4" color="primary">
                <v-icon icon="mdi-account" size="100"></v-icon>
              </v-avatar>
            </v-badge>
          </v-container>
          <v-container>
            <div class="d-flex gap">
              <v-text-field
              v-model="account.firstname"
              label="Vorname"
              prepend-inner-icon="mdi-account"
              :rules="[() => account.firstname !== '' || 'Dieses Feld wird benötigt.']"
              required
              />
              <v-text-field
                v-model="account.lastname"
                label="Nachname"
                prepend-inner-icon="mdi-account"
                :rules="[() => account.lastname !== '' || 'Dieses Feld wird benötigt.']"
                required
              />
            </div>
            <v-text-field
            label="Mailadresse"
            v-model="account.email"
            prepend-inner-icon="mdi-email"
            :rules="[() => account.email !== '' || 'Dieses Feld wird benötigt.']"
            required
            />
            <div
              v-if="tooltip !== ''"
              class="text-error"
            >
              {{tooltip}}
            </div>
          </v-container>
      </div>
      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          prepend-icon="mdi-lock-reset"
          variant="tonal"
          color="primary"
          type="submit"
        >
          Passwort ändern
        </v-btn>
        <v-btn
          prepend-icon="mdi-content-save"
          color="primary"
          type="submit"
        >
          Speichern
        </v-btn>
      </v-container>
    </v-form>

  </v-card>
</template>

<script lang="ts">
export default {
  name: "AccountForm",
  props: {
    backRoute: Object as () => string,
    submitRoute: Object as () => string,
  }
}
</script>

<style scoped>

</style>
