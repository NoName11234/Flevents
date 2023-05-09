<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {Post} from "@/models/post";
import postApi from "@/api/postsApi";
import {AxiosError} from "axios";
import {useEventStore} from "@/store/events";
import IconService from "@/service/iconService";
import {usePostStore} from "@/store/posts";

const router = useRouter();
const route = useRoute();

const postStore = usePostStore();

const tooltip = ref("");
const loading = ref(false);
const eventUuid = route.params.uuid as string;
const inputFiles = ref([] as File[]);
const files = ref([] as File[]);
const attachmentsInput = ref();
const post = ref({
  title: '',
  content: '',
} as Post);

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'posts' } };

async function removeAttachment(index: number) {
  files.value.splice(index, 1);
}

async function addAttachments() {
  tooltip.value = '';
  if (inputFiles.value.length < 1) {
    tooltip.value = 'Es ist keine Datei ausgewählt.';
    return;
  }
  for (let file of inputFiles.value) {
    files.value.push(file);
  }
  inputFiles.value = [];
}

async function submit() {
  tooltip.value = '';
  if (post.value.title === '') {
    return;
  }
  if (post.value.content === '' && files.value.length === 0) {
    tooltip.value = 'Anhänge und Text dürfen nicht gleichzeitig leer sein.';
    return;
  }
  loading.value = true;
  try {
    await postApi.create(post.value, eventUuid, files.value);
    await router.push(backRoute);
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        tooltip.value = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        tooltip.value = 'Netzwerkfehler';
      }
    } else {
      tooltip.value = `Unerwarteter Fehler: ${e}`;
    }
  }
  loading.value = false;
  postStore.hydrateSpecificOf(eventUuid);
}

</script>

<template>
  <Heading text="Post erstellen" />

  <v-card :loading="loading" :disabled="loading">
    <v-form validate-on="submit" @submit.prevent="submit()">
      <v-container class="d-flex flex-column gap-3">

        <v-text-field
          v-model="post.title"
          label="Titel"
          :rules="[() => post.title !== '' || 'Dieses Feld wird benötigt.']"
          hide-details="auto"
          required
        />

        <v-textarea
          v-model="post.content"
          label="Text"
          hide-details="auto"
          prepend-inner-icon="mdi-text"
          required
        >

          <v-chip-group
            column
          >
            <v-chip
              text="test"
              closable
            >
              Text
            </v-chip>
            <v-chip
              text="test"
              closable
            >
              Text
            </v-chip>
          </v-chip-group>

        </v-textarea>

      </v-container>

      <v-divider/>

      <v-container class="d-flex flex-column gap-3">

        <div
          v-for="(attachment, aIndex) in files"
          :key="aIndex"
        >
          <v-card
            border
            elevation="0"
          >
            <div
              class="d-flex flex-row align-center"
            >
              <v-avatar
                :icon="IconService.getIconForFileType(attachment.name)"
              />
              <span
                class="flex-grow-1"
              >
                {{ attachment.name }}
              </span>
              <v-btn
                icon="mdi-delete"
                size="small"
                variant="text"
                color="error"
                @click="removeAttachment(aIndex)"
              />
            </div>
          </v-card>
        </div>

        <div class="d-flex flex-column flex-sm-row justify-start gap">
          <v-file-input
            v-show="false"
            label="Anhänge auswählen..."
            prepend-inner-icon="mdi-plus"
            color="primary"
            hide-details="auto"
            prepend-icon=""
            class="text-no-wrap"
            v-model="inputFiles"
            @change="addAttachments()"
            ref="attachmentsInput"
            multiple
          />
          <v-btn
            prepend-icon="mdi-plus-circle"
            color="primary"
            variant="text"
            @click="attachmentsInput.click()"
          >
            Anhänge hinzufügen
          </v-btn>
        </div>

        <div
          v-if="tooltip !== ''"
          class="text-error"
        >
          {{tooltip}}
        </div>

      </v-container>

      <v-divider />

      <v-container class="d-flex flex-column flex-sm-row justify-end gap">
        <v-btn
          variant="text"
          :to="backRoute"
        >
          Verwerfen
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          prepend-icon="mdi-check"
        >
          Speichern
        </v-btn>
      </v-container>
    </v-form>
  </v-card>

</template>

<style scoped>

.w-custom {
  min-width: 250px;
}

</style>
