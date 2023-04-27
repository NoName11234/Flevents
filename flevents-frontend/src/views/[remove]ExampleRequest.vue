<script setup lang="ts">
import {ref} from "vue";
import {Post} from "@/models/post";
import api from "@/api/api";
import {useAppStore} from "@/store/app";

const appStore = useAppStore();

const eventUuid = ref('');
const post = ref({} as Post);
const tooltip = ref('');

const attachments = ref([] as File[]);

async function update() {
  console.log(attachments.value);
}

async function submit() {
  try {
    let data = new FormData();
    attachments.value.forEach(v => data.append('attachments', v));
    data.append('post', new Blob([JSON.stringify(post.value)], {type: 'application/json'}));
    await api.post(`/events/${eventUuid.value}/posts`, data, {
      headers: {"Content-Type": undefined}
    });
    tooltip.value = 'Hat geklappt.';
  } catch (e) {
    tooltip.value = 'Hat nicht geklappt. Grund:' + e;
  }
}
</script>

<template>
  <v-card>
    <v-form
      validate-on="submit"
      @submit.prevent="submit()"
    >
      <v-container class="d-flex flex-column gap-3">

        <v-text-field
          label="Event-ID"
          prepend-inner-icon="mdi-identifier"
          v-model="eventUuid"
          :rules="[() => eventUuid !== '' || 'UUID muss.']"
          hide-details="auto"
          required
        />

        <v-text-field
          label="Titel"
          prepend-inner-icon="mdi-format-title"
          v-model="post.title"
          :rules="[() => post.title !== '' || 'Titel muss.']"
          hide-details="auto"
          required
        />

        <v-textarea
          name="input-7-1"
          label="Post-Text"
          prepend-inner-icon="mdi-text"
          hide-details="auto"
          v-model="post.content"
        ></v-textarea>

        <v-file-input
          label="Anhänge"
          variant="filled"
          prepend-inner-icon="mdi-paperclip"
          hide-details="auto"
          prepend-icon=""
          v-model="attachments"
          @change="update"
          multiple
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
          variant="text"
          :to="{ name: 'home.personal' }"
        >
          Verwerfen
        </v-btn>
        <v-btn
          type="submit"
          color="primary"
          prepend-icon="mdi-check"
        >
          Posten
        </v-btn>
      </v-container>

    </v-form>
  </v-card>
</template>

<style scoped>

</style>
