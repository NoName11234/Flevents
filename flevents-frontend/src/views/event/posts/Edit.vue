<script setup lang="ts">
import Heading from "@/components/Heading.vue";
import {computed, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {Post} from "@/models/post";
import postApi from "@/api/postsApi";
import {AxiosError} from "axios";
import {useEventStore} from "@/store/events";
import attachmentsApi from "@/api/attachmentsApi";
import {Attachment} from "@/models/attachment";
import {useAppStore} from "@/store/app";

const router = useRouter();
const route = useRoute();

const eventUuid = route.params.uuid as string;
const postUuid = route.params.postUuid as string;

const backRoute = { name: 'events.event', params: { uuid: eventUuid }, query: { tab: 'posts' } };

const appStore = useAppStore();

const eventStore = useEventStore();
const event = eventStore.getEventGetter(eventUuid);

const post = computed({
  get: () => event.value.posts.find(p => p.uuid === postUuid) as Post,
  set: v => event.value.posts[event.value.posts!.findIndex(p => p.uuid)] = v as Post,
});

const tooltip = ref("");
const loading = ref(false);
const inputFiles = ref([] as File[]);
const files = ref([] as File[]);
const deletedAttachments = ref([] as Attachment[]);
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
  eventStore.hydrateSpecific(eventUuid);
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

      <template v-if="remainingExistingAttachments.length > 0">
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
                  icon="mdi-file"
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

        <small class="text-grey">Neue Anhänge</small>

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

.w-custom {
  min-width: 250px;
}

</style>
