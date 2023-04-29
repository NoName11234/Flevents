<script setup lang="ts">
import ColorService from "@/service/colorService";
import {Comment} from "@/models/comment";
import {ref} from "vue";
import {useAccountStore} from "@/store/account";
import {storeToRefs} from "pinia";
import commentApi from "@/api/commentsApi";
import {AxiosError} from "axios";
import {useAppStore} from "@/store/app";
import {useEventStore} from "@/store/events";

const props = defineProps({
  eventUuid: {
    required: true,
    type: String,
  },
  postUuid: {
    required: true,
    type: String,
  }
});

const appStore = useAppStore();
const eventStore = useEventStore();

const accountStore = useAccountStore();
const { currentAccount: account } = storeToRefs(accountStore);

const plainComment = {
  author: account.value,
  content: '',
} as Comment;
const comment = ref({ ...plainComment });

const tooltip = ref('');
const loading = ref(false);

async function submit() {
  tooltip.value = '';
  if (comment.value.content.length === 0) {
    tooltip.value = 'Es können keine leeren Kommentare gepostet werden.'
    loading.value = false;
    return;
  }
  loading.value = true;
  try {
    await commentApi.create(comment.value, props.postUuid, props.eventUuid);
    comment.value = { ...plainComment };
    appStore.addToast({
      text: 'Kommentar gepostet.',
      color: 'success',
    });
  } catch (e) {
    let errorMessage = '';
    if (e instanceof AxiosError) {
      if (e.code === AxiosError.ERR_BAD_REQUEST) {
        errorMessage = 'Ungültige Anfrage';
      }
      else if (e.code === AxiosError.ERR_NETWORK) {
        errorMessage = 'Netzwerkfehler';
      }
    } else {
      errorMessage = `Unerwarteter Fehler: ${e}`;
    }
    appStore.addToast({
      text: `Kommentieren fehlgeschlagen: ${errorMessage}`,
      color: 'error',
    });
  }
  loading.value = false;
  eventStore.hydrateSpecific(props.eventUuid);
}

</script>

<template>
  <v-timeline-item
    icon="mdi-account"
    :dot-color="ColorService.getAvatarColor(comment.author)"
    fill-dot
    width="100%"
  >
    <v-card
      elevation="0"
      border
      class="ms-n4"
      :loading="loading"
    >
      <v-textarea
        hide-details="auto"
        v-model="comment.content"
        :disabled="loading"
      >
      </v-textarea>
      <v-container class="d-flex flex-column flex-sm-row justify-end gap align-sm-center pa-2">
        <span
          v-if="tooltip"
          class="text-error"
        >
          {{ tooltip }}
        </span>
        <v-spacer />
        <v-btn
          :disabled="loading"
          prepend-icon="mdi-check"
          color="primary"
          size="small"
          @click="submit()"
        >
          Kommentieren
        </v-btn>
      </v-container>
    </v-card>
  </v-timeline-item>

</template>

<style scoped>

</style>
