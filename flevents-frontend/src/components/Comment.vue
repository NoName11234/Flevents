<script setup lang="ts">
import {Comment} from "@/models/comment";
import ColorService from "@/service/colorService";
import DatetimeService from "../service/datetimeService";
import {computed, ref} from "vue";
import {storeToRefs} from "pinia";
import {useAccountStore} from "@/store/account";
import commentsApi from "@/api/commentsApi";
import {useEventStore} from "@/store/events";
import {useAppStore} from "@/store/app";
import {AxiosError} from "axios";

const props = defineProps({
  comment: {
    required: true,
    type: Object as () => Comment,
  },
  adminView: {
    required: false,
    type: Boolean,
    default: false,
  },
  eventUuid: {
    required: true,
    type: String,
  },
  postUuid: {
    required: true,
    type: String,
  },
});

const eventStore = useEventStore();
const appStore = useAppStore();

const loading = ref(false);

const canDelete = computed(() => {
  if (props.adminView) return true;
  const { currentAccount } = storeToRefs(useAccountStore());
  return currentAccount.value?.uuid === props.comment.author.uuid;
});

async function deleteComment() {
  loading.value = true;
  try {
    await commentsApi.delete(props.comment?.uuid, props.postUuid, props.eventUuid);
    appStore.addToast({
      text: 'Kommentar gelöscht.',
      color: 'success',
    });
    eventStore.hydrateSpecific(props.eventUuid);
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
      text: `Löschen des Kommentars fehlgeschlagen: ${errorMessage}`,
      color: 'error',
    });
  }
  loading.value = false;
}

</script>

<template>
  <v-timeline-item
    icon="mdi-account"
    width="100%"
    :dot-color="ColorService.getAvatarColor(comment.author)"
    fill-dot
  >
    <v-card
      elevation="0"
      border
      class="ms-n4"
      :loading="loading"
      :disabled="loading"
    >
      <v-card-text class="d-flex flex-column">
        <div class="d-flex flex-row align-center gap">
          <div class="d-flex flex-column flex-sm-row gap">
            <strong>{{ comment.author.firstname }} {{ comment.author.lastname }}</strong>
            <span class="text-grey mt-n2 mt-sm-0">{{ DatetimeService.getDateTime(new Date(comment.creationDate)) }}</span>
          </div>
          <v-spacer />
          <v-btn
            icon=""
            size="25"
            variant="text"
            @click="deleteComment"
          >
            <v-icon
              v-if="canDelete"
              icon="mdi-delete"
              size="15"
            />
          </v-btn>
        </div>
        {{ comment.content }}
      </v-card-text>
    </v-card>
  </v-timeline-item>

</template>

<style scoped>

</style>
