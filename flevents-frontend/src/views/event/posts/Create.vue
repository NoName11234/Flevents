<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {Post} from "@/models/post";

const router = useRouter();
const route = useRoute();
const tooltip = ref("");
const eventId = route.params.uuid;
const inputFiles = ref([] as File[]);
const files = ref([] as File[]);
const post = ref({
  title: 'post1',
  content: 'lalala',
} as Post);

async function removeAttachment(index: number) {
  files.value.splice(index, 1);
}

async function addAttachment() {
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

async function submit(){
  await router.push({ name: 'events.event', params: { uuid: eventId }});
}

</script>

<template>
  <Heading text="Post erstellen" />

  <v-card>
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
                icon="mdi-file"
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

        <v-btn-group
          border
          class="align-center overflow-x-auto"
          density="default"
        >
            <v-file-input
              label="Anhänge auswählen..."
              prepend-inner-icon="mdi-file"
              hide-details="auto"
              prepend-icon=""
              class="text-no-wrap w-custom overflow-hidden"
              v-model="inputFiles"
              multiple
            />
            <v-divider vertical />
            <v-btn
              prepend-icon="mdi-plus-circle-outline"
              color="primary"
              variant="text"
              class="h-100"
              @click="addAttachment()"
            >
              Hinzufügen
            </v-btn>
        </v-btn-group>

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
          :to="{ name: 'events.event', params: { uuid: eventId } }"
        >
          Verwerfen
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          prepend-icon="mdi-check"
          @click="submit()"
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