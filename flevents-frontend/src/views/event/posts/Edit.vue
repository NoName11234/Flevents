<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {computed, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import postApi from "@/api/postsApi";
import {AxiosError} from "axios";
import attachmentsApi from "@/api/attachmentsApi";
import {Attachment} from "@/models/attachment";
import {useAppStore} from "@/store/app";
import IconService from "@/service/iconService";
import {usePostStore} from "@/store/posts";

const router = useRouter();
const route = useRoute();

const eventUuid = route.params.uuid as string;
const postUuid = route.params.postUuid as string;

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'posts' } };

const appStore = useAppStore();

const postStore = usePostStore();
const post = postStore.getPostGetter(postUuid, eventUuid);

const tooltip = ref("");
const loading = ref(false);
const inputFiles = ref([] as File[]);
const files = ref([] as File[]);
const deletedAttachments = ref([] as Attachment[]);
const attachmentsInput = ref();
const remainingExistingAttachments = computed(() => post.value.attachments?.filter(a => !deletedAttachments.value.includes(a)));

async function removeAttachment(index: number) {
  files.value.splice(index, 1);
}

async function removeExistingAttachment(attachment: Attachment) {
  deletedAttachments.value.push(attachment);
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

  // First, try to remove all deleted attachments.
  let successful = [] as string[];
  let unsuccessful = [] as string[];
  for (let attachment of deletedAttachments.value) {
    try {
      await attachmentsApi.delete(attachment.uuid, eventUuid);
      successful.push(attachment.filename);
    } catch (e) {
      unsuccessful.push(attachment.filename);
    }
  }

  // Only notify if removal was unsuccessful.
  if (unsuccessful.length > 0) {
    appStore.addToast({
      text: `Fehler beim Entfernen folgender Anhänge: ${unsuccessful.join(', ')}`,
      color: 'error',
    });
  }

  // Then try to update the post itself.
  try {
    await postApi.edit(postUuid, eventUuid, post.value, files.value);
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
  <Heading text="Post bearbeiten" />

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

      <template v-if="(remainingExistingAttachments?.length ?? 0) > 0">
        <v-divider />
        <v-container class="d-flex flex-column gap-3">
          <small class="text-grey">
            Existierende Anhänge
          </small>
          <div
            v-for="(attachment, aIndex) in remainingExistingAttachments"
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
                  :icon="IconService.getIconForFileType(attachment.filename)"
                />
                <span
                  class="flex-grow-1"
                >
                    {{ attachment.filename }}
                  </span>
                <v-btn
                  icon="mdi-delete"
                  size="small"
                  variant="text"
                  color="error"
                  @click="removeExistingAttachment(attachment)"
                />
              </div>
            </v-card>
          </div>
        </v-container>
      </template>

      <v-divider />

      <v-container class="d-flex flex-column gap-3">

        <small
          v-if="(remainingExistingAttachments?.length ?? 0) > 0"
          class="text-grey"
        >
          Neue Anhänge
        </small>

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
            @change="addAttachment()"
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
          {{ tooltip }}
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

</style>
