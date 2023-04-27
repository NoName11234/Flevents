<script setup lang="ts">
import {Post} from "@/models/post";
import Comment from "@/components/Comment.vue";
import AccountAvatar from "@/components/AccountAvatar.vue";
import {useRoute} from "vue-router";
import CommentForm from "@/components/CommentForm.vue";
import {computed, ref} from "vue";
import postApi from "@/api/postsApi";
import {useAppStore} from "@/store/app";
import {AxiosError} from "axios";
import {storeToRefs} from "pinia";
import {useAccountStore} from "@/store/account";

const appStore = useAppStore();

const loading = ref(false);

const props = defineProps({
  post: {
    required: true,
    type: Object as () => Post,
  },
  eventUuid: {
    required: true,
    type: String,
  },
  adminView: {
    required: false,
    type: Boolean,
    default: false,
  }
});

const canDeleteAndEdit = computed(() => {
  if (props.adminView) return true;
  const { currentAccount } = storeToRefs(useAccountStore());
  if (currentAccount.value?.uuid === props.post?.author.uuid) {
    return true;
  }
});

async function deletePost() {
  loading.value = true;
  try {
    await postApi.delete(props.post?.uuid, props.eventUuid);
    appStore.addToast({
      text: 'Post gelöscht.',
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
      text: `Post konnte nicht gelöscht werden: ${errorMessage}`,
      color: 'error',
    });
  }
  loading.value = false;
}

async function downloadAttachment(url: string) {
  window.location.href = url;
}

</script>

<template>
  <v-expansion-panel
    color="primary"
  >

    <v-expansion-panel-title>

      <div class="d-flex flex-row justify-start align-center gap-3 w-100">

        <strong>
          {{ post.title }}
        </strong>

        <span class="text-grey">
          {{ new Date(post.creationDate).toLocaleDateString("DE-de") }}
        </span>

      </div>

    </v-expansion-panel-title>

    <v-expansion-panel-text>
      <div class="d-flex flex-column my-1 mx-n3">
        <v-card
          elevation="0"
          border
        >

          <v-container
            class="d-flex flex-column gap-2"
          >
            <div
              class="d-flex flex-row align-center gap-3"
            >
              <AccountAvatar
                :size="'40'"
                :account="post.author"
              />
              <div class="d-flex flex-column">
                <strong>
                  {{ post.author.firstname }} {{ post.author.lastname }}
                </strong>
<!--                <small class="text-grey">-->
<!--                  {{ post.creationDate.toLocaleDateString("DE-de") }}-->
<!--                </small>-->
              </div>
              <v-spacer />

              <v-btn
                v-if="canDeleteAndEdit"
                icon=""
                size="small"
                variant="text"
              >
                <v-icon
                  icon="mdi-dots-vertical"
                />
                <v-menu activator="parent">
                  <v-list>
                    <v-list-item
                      prepend-icon="mdi-pencil"
                      title="Bearbeiten"
                      :to="{ name: 'events.posts.edit', params: { uuid: props.eventUuid, postUuid: 1 } }"
                    />
                    <v-list-item
                      prepend-icon="mdi-delete"
                      title="Löschen"
                      @click="deletePost"
                    />
                  </v-list>
                </v-menu>
              </v-btn>
            </div>
            <span>
              {{ post.content }}
            </span>

            <v-chip-group
              v-if="post.attachments"
              column
            >
              <v-chip
                v-for="(attachment, index) in post.attachments"
                :key="index"
                :text="attachment.filename"
                prepend-icon="mdi-file"
                variant="tonal"
                link
                @click="downloadAttachment(attachment.url)"
              >
              </v-chip>
            </v-chip-group>

          </v-container>

        </v-card>

        <v-timeline
          side="end"
          truncate-line="end"
          density="compact"
        >
          <CommentForm
            :event-uuid="eventUuid"
            :post-uuid="post.uuid"
          />
          <Comment
            v-if="post.comments?.length > 0"
            v-for="(comment, cIndex) in post.comments"
            :key="cIndex"
            :comment="comment"
            :admin-view="adminView"
          />
        </v-timeline>

      </div>

    </v-expansion-panel-text>

  </v-expansion-panel>
</template>

<style scoped>

</style>
